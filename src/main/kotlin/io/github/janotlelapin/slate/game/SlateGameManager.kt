package io.github.janotlelapin.slate.game

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class SlateGameManager : GameManager {
    private val games: HashMap<UUID, SlateGame<out GameSettings>> = HashMap()

    private var pending: SlateGame<out GameSettings>? = null

    override fun game(id: UUID): SlateGame<out GameSettings>? {
        return games[id]
    }

    override fun create(
        plugin: JavaPlugin,
        settingsClass: Class<out GameSettings>,
        onFinish: (game: Game<out GameSettings>) -> Unit
    ) {
        pending = SlateGame(plugin, settingsClass)
        pending!!.prepare(onFinish)
    }

    override fun start(players: Collection<Player>): Game<out GameSettings> {
        if (pending == null) throw IllegalStateException("No game currently pending")

        val game = pending!!
        pending = null

        games[game.id] = game
        game.start(players)

        return game
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
