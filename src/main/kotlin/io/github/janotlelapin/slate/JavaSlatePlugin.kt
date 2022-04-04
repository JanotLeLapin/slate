package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.game.GameSettings
import io.github.janotlelapin.slate.game.SlateGameManager
import io.github.janotlelapin.slate.scenarios.CutCleanScenario
import io.github.janotlelapin.slate.scenarios.HastyBoysScenario
import io.github.janotlelapin.slate.util.game
import io.github.janotlelapin.slate.util.isGameDead
import io.github.janotlelapin.slate.util.lastAttacker
import io.github.janotlelapin.slate.util.sendMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

class JavaSlatePlugin : Listener, SlatePlugin, JavaPlugin() {
    override val gameManager: SlateGameManager = SlateGameManager()

    override fun registerEvents(settingsType: Class<out GameSettings>, listener: GameListener, plugin: JavaPlugin) {
        for (handler in listener::class.java.declaredMethods) {
            val annotation = handler.getDeclaredAnnotation(EventHandler::class.java) ?: continue

            if (handler.parameterCount != 2) continue
            val eventType = handler.parameterTypes[0] as Class<Event>

            plugin.server.pluginManager.registerEvent(
                eventType,
                listener,
                annotation.priority,
                { _, e ->
                    val game: Game<GameSettings> = when (e) {
                        is BlockEvent -> e.block.world.game()
                        is EntityEvent -> e.entity.world.game()
                        is InventoryEvent -> e.view.player.world.game()
                        is PlayerEvent -> e.player.game()
                        is VehicleEvent -> e.vehicle.world.game()
                        is WeatherEvent -> e.world.game()
                        else -> null
                    } ?: return@registerEvent

                    if (game.settings::class.java == settingsType && eventType.isInstance(e))
                        handler.invoke(listener, e, game)
                },
                plugin,
            )
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        registerEvents(GameSettings::class.java, CutCleanScenario(), this)
        registerEvents(GameSettings::class.java, HastyBoysScenario(), this)

        logger.info("Enabling Slate")
    }

    override fun onDisable() {
        gameManager.clear()
    }

    @EventHandler
    fun onFood(e: FoodLevelChangeEvent) {
        val p = e.entity
        if (p !is Player) return

        if (p.game<GameSettings>() == null)
            e.foodLevel = 20
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        val p = e.entity
        if (p !is Player) return

        if (p.game<GameSettings>() == null)
            e.isCancelled = true
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        val game = e.player.game<GameSettings>() ?: return

        e.isCancelled = true
        e.player.sendMessage(game.settings.noChatMessage)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val msg = Component
            .text("[").color(NamedTextColor.GRAY)
            .append(Component.text("+").color(NamedTextColor.GREEN))
            .append(Component.text("]"))
            .append(Component.space())
            .append(Component.text("${e.player.name} a rejoint la partie"))

        e.joinMessage = null
        e.player.world.players.forEach { it.sendMessage(msg) }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val msg = Component
            .text("[").color(NamedTextColor.GRAY)
            .append(Component.text("-").color(NamedTextColor.RED))
            .append(Component.text("]"))
            .append(Component.space())
            .append(Component.text("${e.player.name} a quitté la partie"))

        e.quitMessage = null
        e.player.world.players.forEach { it.sendMessage(msg) }
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player && e.damager is Player) {
            (e.entity as Player).lastAttacker(e.damager as Player, this)
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val p = e.entity
        val game = p.game<GameSettings>() ?: return
        val l = p.location
        p.isGameDead(true, game.plugin)
        p.spigot().respawn()
        p.teleport(l)
        p.gameMode = GameMode.SPECTATOR
    }

    @EventHandler
    fun onChunkUnload(e: ChunkUnloadEvent) {
        if (e.world.game<GameSettings>() == null) return
        e.isCancelled = true
    }
}
