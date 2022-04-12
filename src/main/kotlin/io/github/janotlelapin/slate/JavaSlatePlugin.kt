package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.game.GameSettings
import io.github.janotlelapin.slate.game.SlateGameManager
import io.github.janotlelapin.slate.menu.SlateMenu
import io.github.janotlelapin.slate.scenarios.CutCleanScenario
import io.github.janotlelapin.slate.scenarios.HastyBoysScenario
import io.github.janotlelapin.slate.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class JavaSlatePlugin : Listener, SlatePlugin, JavaPlugin() {
    override val gameManager: SlateGameManager = SlateGameManager()

    override val waitLocation: Location
        get() = Location(
            server.getWorld(config.getString("wait.world")),
            config.getDouble("wait.x"),
            config.getDouble("wait.y"),
            config.getDouble("wait.z"),
        )

    private val menus: HashMap<Inventory, SlateMenu> = hashMapOf()

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

                    if (settingsType.isInstance(game.settings) && eventType.isInstance(e))
                        handler.invoke(listener, e, game)
                },
                plugin,
            )
        }
    }

    override fun createMenu(type: InventoryType, title: Component): SlateMenu {
        val inv = server.createInventory(null, type, title.legacy())
        val menu = SlateMenu(inv)
        menus[inv] = menu
        return menu
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        registerEvents(GameSettings::class.java, CutCleanScenario(), this)
        registerEvents(GameSettings::class.java, HastyBoysScenario(), this)

        WorldCreator("wait").createWorld()

        saveDefaultConfig()

        logger.info("Enabling Slate")
    }

    override fun onDisable() {
        gameManager.clear()
    }

    @EventHandler
    fun onFood(e: FoodLevelChangeEvent) {
        val p = e.entity
        if (p !is Player) return

        if (p.game<GameSettings>()?.running() != true)
            e.foodLevel = 20
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        val p = e.entity

        if (p is Player && p.game<GameSettings>()?.running() != true)
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
        val p = e.player

        val game = p.game<GameSettings>()
        if (game == null)
            p.clear(this)
        else
            p.scoreboard = game.scoreboard

        val msg = Component
            .text("[").color(NamedTextColor.GRAY)
            .append(Component.text("+").color(NamedTextColor.GREEN))
            .append(Component.text("]"))
            .append(Component.space())
            .append(Component.text("${e.player.name} a rejoint la partie"))

        e.joinMessage = null
        p.world.players.forEach { it.sendMessage(msg) }
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
        val ent = e.entity
        val dmg = e.damager
        if (ent is Player && dmg is Player)
            ent.lastAttacker(dmg, this)
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val p = e.entity
        val game = p.game<GameSettings>() ?: return
        if (!game.running()) return

        val l = p.location
        p.isGameDead(true, game.plugin)
        p.spigot().respawn()
        p.teleport(l)
    }

    @EventHandler
    fun onChunkUnload(e: ChunkUnloadEvent) {
        if (e.world.game<GameSettings>() == null) return
        e.isCancelled = true
    }

    @EventHandler
    fun onWorldInit(e: WorldInitEvent) {
        if (e.world.game<GameSettings>() == null) return
        e.world.keepSpawnInMemory = false
    }

    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        val menu = menus[e.inventory] ?: return
        menu.draw()
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val menu = menus[e.inventory] ?: return
        e.isCancelled = true

        val element = menu.elements[e.slot] ?: return
        val p = e.view.player as Player
        element.click(p)
        menu.draw()
    }
}
