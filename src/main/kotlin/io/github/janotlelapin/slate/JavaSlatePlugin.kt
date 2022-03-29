package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.game.SlateGameManager
import io.github.janotlelapin.slate.util.game
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

class JavaSlatePlugin : SlatePlugin, JavaPlugin() {
    override val gameManager: SlateGameManager = SlateGameManager()

    override fun onEnable() {
        logger.info("Enabling Slate")
    }

    override fun onDisable() {
        gameManager.clear()
    }
}
