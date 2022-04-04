package io.github.janotlelapin.slate.scenarios

import io.github.janotlelapin.slate.Scenario
import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.Game
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack

class HastyBoysScenario : GameListener {
    private val items = arrayOf(
        "AXE",
        "PICKAXE",
        "SPADE",
    )

    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent, game: Game<*>) {
        if (!game.scenarios.contains(Scenario.HASTY_BOYS)) return

        val result = e.recipe.result.type
        val tokens = result.name.split("_")
        if (tokens.size < 2 || !items.contains(tokens[1])) return

        val it = ItemStack(result)
        it.addEnchantment(Enchantment.DIG_SPEED, 3)
        e.inventory.setItem(0, it)
    }
}
