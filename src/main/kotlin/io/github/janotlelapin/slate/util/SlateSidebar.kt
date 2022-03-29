package io.github.janotlelapin.slate.util

import net.kyori.adventure.text.Component
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import java.util.*

class SlateSidebar(
    override val id: UUID,
    override val scoreboard: Scoreboard,
    override val objective: Objective,
    override val title: Component,
) : Sidebar {
    private var lines: Array<String?> = arrayOfNulls(10)

    override fun lines(lines: Array<String?>) {
        for (line in this.lines) {
            if (line == null) break
            scoreboard.resetScores(line)
        }
        for (i in lines.indices) objective.getScore(lines[i]).score = lines.size - i
        this.lines = lines
    }

    init {
        objective.displayName = title.legacy()
    }
}