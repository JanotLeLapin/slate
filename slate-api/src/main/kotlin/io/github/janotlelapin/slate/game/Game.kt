package io.github.janotlelapin.slate.game

import io.github.janotlelapin.slate.Scenario
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import java.util.*

interface Game<S : GameSettings> {
    /**
     * The unique identifier of this game
     */
    val id: UUID

    /**
     * The plugin responsible for this game
     */
    val plugin: JavaPlugin

    /**
     * The task manager for this game
     */
    val taskManager: TaskManager

    /**
     * A list of scenarios for this game
     */
    val scenarios: HashSet<Scenario>

    /**
     * Basic settings for this game
     */
    val settings: S

    /**
     * The scoreboard assigned to each player in this game.
     * Used by the sidebar, among other things
     */
    val scoreboard: Scoreboard

    /**
     * The world in which this game takes place
     */
    var world: World

    /**
     * The id of the creator of the game
     */
    val host: UUID

    /**
     * A list of UUIDs for each player in the game
     */
    val players: List<UUID>

    /**
     * @return The elapsed time for this game in ticks
     */
    val time: Long

    /**
     * @return The owner of this game
     */
    fun host(): OfflinePlayer

    /**
     * @return A list of online players in this game
     */
    fun onlinePlayers(): List<Player>

    /**
     * @return A list of each player in this game, including offline ones
     */
    fun allPlayers(): List<OfflinePlayer>

    /**
     * @return Whether this game can be started
     */
    fun ready(): Boolean

    /**
     * @return Whether this game is running
     */
    fun running(): Boolean
}
