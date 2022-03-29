package io.github.janotlelapin.slate.game

import io.github.janotlelapin.slate.util.*
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*
import kotlin.collections.ArrayList

class SlateGame(
    override val plugin: JavaPlugin,
    override val settings: GameSettings,
) : Game {
    override val id: UUID = UUID.randomUUID()
    override val players: ArrayList<UUID> = ArrayList()

    override val taskManager: SlateTaskManager = SlateTaskManager()
    override val scoreboard: Scoreboard = plugin.server.scoreboardManager.newScoreboard
    override val objective: Objective = scoreboard.registerNewObjective(id.toString().substring(0, 8), "dummy")
    override val sidebar: SlateSidebar = SlateSidebar(
        id,
        scoreboard,
        objective,
        settings.brand,
    )

    private lateinit var world: World
    private var startTime: Long = -1

    override fun allPlayers(): List<OfflinePlayer> {
        return players.map { plugin.server.getOfflinePlayer(it) }
    }

    override fun onlinePlayers(): List<Player> {
        return players.map { plugin.server.getPlayer(it) }
    }

    private fun loadChunks(layer: Int, onFinish: (game: Game) -> Unit) {
        val g = this
        object: BukkitRunnable() {
            override fun run() {
                val x = layer * 16
                for (z in 0..500 step 16)
                    world.loadChunk(x, z, true)

                if (x + 16 > 500) {
                    plugin.logger.info("Chunks for $id loaded")
                    onFinish(g)
                } else loadChunks(layer + 1, onFinish)
            }
        }.runTaskLater(plugin, 20)
    }

    fun prepare(onFinish: (game: Game) -> Unit) {
        object: BukkitRunnable() {
            override fun run() {
                plugin.logger.info("Creating world for $id")
                val w = WorldCreator("world_$id").createWorld()
                if (settings.badBiomes.contains(w.getBiome(0, 0))) {
                    w.delete()
                    prepare(onFinish)
                }
                else {
                    world = w
                    loadChunks(0, onFinish)
                }
            }
        }.runTaskLater(plugin, 20)
    }

    fun start(players: Collection<Player>) {
        if (!this::world.isInitialized) throw IllegalStateException("World not initialized")

        plugin.logger.info("Starting game $id")
        startTime = world.fullTime

        players.forEach {
            this.players.add(it.uniqueId)

            it.teleport(world.randomCoordinates(500))
            it.clear()

            it.scoreboard = scoreboard
        }

        world.worldBorder.setCenter(0.0, 0.0)
        settings.borderSize.forEach { (time, size) ->
            if (time.isEmpty())
                world.worldBorder.setSize(size.toDouble(), 0)

            else {
                taskManager.register(object: BukkitRunnable() {
                    override fun run() {
                        onlinePlayers().forEach {
                            it.sendMessage(settings.shrinkBorderMessage)
                        }
                        world.worldBorder.setSize(size.toDouble(), ((time[1] - time[0]) * 60).toLong())
                    }
                }.runTaskLater(plugin, (time[0] * 20 * 60).toLong()))
            }
        }

        taskManager.register(object: BukkitRunnable() {
            override fun run() { settings.update(game) }
        }.runTaskTimer(plugin, 0, 20))
    }

    fun stop() {
        taskManager.clear()

        val l = plugin.server.getWorld("world").spawnLocation
        onlinePlayers().forEach {
            it.clear()
            it.teleport(l)
        }

        world.delete()
    }

    override val time: Long
        get() = world.fullTime - startTime

    val game: SlateGame
        get() = this

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
    }
}