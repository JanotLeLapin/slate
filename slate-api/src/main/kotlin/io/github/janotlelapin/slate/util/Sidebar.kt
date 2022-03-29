package io.github.janotlelapin.slate.util

import net.kyori.adventure.text.Component
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

/**
 * An efficient and easy to use sidebar API
 */
interface Sidebar {
    /**
     * The id of the game responsible for this sidebar
     */
    val id: UUID

    /**
     * The scoreboard used by this sidebar
     */
    val scoreboard: Scoreboard

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
