package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.GameManager
import io.github.janotlelapin.slate.game.GameSettings
import org.bukkit.plugin.java.JavaPlugin

interface SlatePlugin {
    /**
     * A manager for every running Slate game
     */
    val gameManager: GameManager

    /**
     * Registers a listener
     */
    fun registerEvents(settingsType: Class<out GameSettings>, listener: GameListener, plugin: JavaPlugin)
}
