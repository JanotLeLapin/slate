package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.GameManager
import io.github.janotlelapin.slate.game.GameSettings
import io.github.janotlelapin.slate.menu.Menu
import io.github.janotlelapin.slate.util.Sidebar
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.event.inventory.InventoryType
import org.bukkit.plugin.java.JavaPlugin

interface SlatePlugin {
    /**
     * A manager for every running Slate game
     */
    val gameManager: GameManager

    /**
     * The location for the "waiting room" world
     */
    val waitLocation: Location

    /**
     * Registers a listener
     */
    fun registerEvents(settingsType: Class<out GameSettings>, listener: GameListener, plugin: JavaPlugin)

    /**
     * Creates a menu inventory
     */
    fun createMenu(type: InventoryType, title: Component): Menu

    /**
     * Creates a sidebar
     */
    fun createSidebar(title: Component): Sidebar
}
