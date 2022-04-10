package io.github.janotlelapin.slate.game

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class SlateGameManager : GameManager {
    private val games: HashMap<UUID, SlateGame<out GameSettings>> = HashMap()

    override fun game(id: UUID): SlateGame<out GameSettings>? {
        return games[id]
    }

    override fun create(
        plugin: JavaPlugin,
        host: Player,
        settingsClass: Class<out GameSettings>,
        onFinish: (game: Game<out GameSettings>) -> Unit
    ) {
        val game = SlateGame(plugin, host.uniqueId, settingsClass)
        games[game.id] = game
        game.prepare(onFinish)
    }

    override fun start(game: Game<out GameSettings>, players: Collection<Player>): Game<out GameSettings> {
        (game as SlateGame<*>).start(players)

        return game
    }

    override fun stop(game: Game<out GameSettings>) {
        val g = games.remove(game.id) ?: return
        g.stop()
    }

    fun clear() {
        val it = games.iterator()
        while (it.hasNext()) {
            val game = it.next().value

            game.stop()
            it.remove()
        }
    }
}
