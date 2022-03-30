package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.game.GameSettings
import io.github.janotlelapin.slate.game.SlateGameManager
import io.github.janotlelapin.slate.util.game
import io.github.janotlelapin.slate.util.isGameDead
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.plugin.java.JavaPlugin

class JavaSlatePlugin : Listener, SlatePlugin, JavaPlugin() {
    override val gameManager: SlateGameManager = SlateGameManager()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)

        logger.info("Enabling Slate")
    }

    override fun onDisable() {
        gameManager.clear()
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val game = e.entity.game<GameSettings>() ?: return
        e.entity.isGameDead(true, game.plugin)
    }

    @EventHandler
    fun onChunkUnload(e: ChunkUnloadEvent) {
        if (e.world.game<GameSettings>() == null) return
        e.isCancelled = true
    }
}
