package io.github.janotlelapin.slate.game

import io.github.janotlelapin.slate.util.Sidebar
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

interface Game {
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
     * Basic settings for this game
     */
    val settings: GameSettings

    /**
     * The custom sidebar shown to players in this game
     */
    val sidebar: Sidebar

    /**
     * The scoreboard assigned to each player in this game.
     * Used by the sidebar, among other things
     */
    val scoreboard: Scoreboard

    /**
     * The objective used by the scoreboard to display the sidebar
     */
    val objective: Objective

    /**
     * A list of UUIDs for each player in the game
     */
    val players: List<UUID>

    /**
     * @return The elapsed time for this game in ticks
     */
    val time: Long

    /**
     * @return A list of online players in this game
     */
    fun onlinePlayers(): List<Player>

    /**
     * @return A list of each player in this game, including offline ones
     */
    fun allPlayers(): List<OfflinePlayer>
}
