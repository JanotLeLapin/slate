package io.github.janotlelapin.slate.game

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashMap

class SlateGameManager : GameManager {
    private val games: HashMap<UUID, SlateGame> = HashMap()

    private var pending: SlateGame? = null

    override fun game(id: UUID): SlateGame? {
        return games[id]
    }

    override fun create(
        plugin: JavaPlugin,
        settings: GameSettings,
        onFinish: (game: Game) -> Unit
    ) {
        pending = SlateGame(plugin, settings)
        pending!!.prepare(onFinish)
    }

    override fun start(players: Collection<Player>): Game {
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
