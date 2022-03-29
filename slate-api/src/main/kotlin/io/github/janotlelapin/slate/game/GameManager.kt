package io.github.janotlelapin.slate.game

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

interface GameManager {
    /**
     * @return A running game with the specified ID
     */
    fun game(id: UUID): Game<out GameSettings>?

    /**
     * Creates a game
     */
    fun create(
        plugin: JavaPlugin,
        settingsClass: Class<out GameSettings>,
        onFinish: (game: Game<out GameSettings>) -> Unit,
    )

    /**
     * Starts the most recently created game
     *
     * @throws IllegalStateException No game is currently pending
     * @return The started game
     */
    fun start(players: Collection<Player>): Game<out GameSettings>
}
