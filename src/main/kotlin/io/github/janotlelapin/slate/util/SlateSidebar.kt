package io.github.janotlelapin.slate.util

import net.kyori.adventure.text.Component
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class SlateSidebar(
    scoreboard: Scoreboard,
    override val title: Component,
) : Sidebar {
    override val objective: Objective = scoreboard.registerNewObjective("Plugin", "dummy")

    private var lines: Array<out String?> = arrayOfNulls(10)

    override fun lines(vararg lines: String?) {
        for (line in this.lines) {
            if (line == null) break
            objective.scoreboard.resetScores(line)
        }
        for (i in lines.indices) objective.getScore(lines[i]).score = lines.size - i
        this.lines = lines
    }

    override fun lines(vararg lines: Component?) {
        this.lines(*lines.map { it?.legacy() }.toTypedArray())
    }

    init {
        objective.displayName = title.legacy()
        objective.displaySlot = DisplaySlot.SIDEBAR
    }
}
