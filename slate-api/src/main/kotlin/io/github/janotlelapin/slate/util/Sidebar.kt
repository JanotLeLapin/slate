package io.github.janotlelapin.slate.util

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Objective

/**
 * Sets the sidebar of the player
 */
fun Player.sidebar(sidebar: Sidebar) {
    scoreboard = sidebar.objective.scoreboard
}

/**
 * An efficient and easy to use sidebar API
 */
interface Sidebar {
    /**
     * The objective used by this sidebar
     */
    val objective: Objective

    /**
     * The title of the sidebar as a JSON object
     */
    val title: Component

    /**
     * Updates the lines of the scoreboard
     */
    fun lines(vararg lines: String?)

    /**
     * Updates the lines of the scoreboard
     */
    fun lines(vararg lines: Component?)
}
