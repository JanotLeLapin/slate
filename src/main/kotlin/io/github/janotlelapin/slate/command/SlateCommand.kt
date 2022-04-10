package io.github.janotlelapin.slate.command

import io.github.janotlelapin.slate.JavaSlatePlugin
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.util.sendMessage
import io.github.janotlelapin.slate.util.toTimeString
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class SlateCommand(private val plugin: JavaSlatePlugin) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        var comp = Component.text("Registered games").color(NamedTextColor.BLUE)
        plugin.gameManager.games.values.forEach {
            comp = comp.append(Component.newline()).append(gameComponent(it))
        }
        sender.sendMessage(comp)
        return true
    }

    private fun gameComponent(game: Game<*>): Component {
        return Component.empty().color(NamedTextColor.GRAY)
            .append(game.settings.brand)
            .append(Component.text(" - ${(game.time / 20).toTimeString()} ${game.allPlayers().size} players"))
            .append(Component.space())
            .append(
                if (game.running()) Component.text("running").color(NamedTextColor.GREEN)
                else Component.text("waiting").color(NamedTextColor.RED)
            )
    }
}
