package io.github.janotlelapin.slate.scenarios

import io.github.janotlelapin.slate.Scenario
import io.github.janotlelapin.slate.event.GameListener
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.util.setDrops
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

/**
 * A scenario that replaces raw meat/ores into their cooked form when obtained
 */
class CutCleanScenario : GameListener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent, game: Game<*>) {
        if (!game.scenarios.contains(Scenario.CUT_CLEAN)) return

        val material = when (e.block.type) {
            Material.IRON_ORE -> Material.IRON_INGOT
            Material.GOLD_ORE -> Material.GOLD_INGOT
            else -> null
        } ?: return

        e.setDrops(arrayListOf(ItemStack(material)))
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent, game: Game<*>) {
        if (!game.scenarios.contains(Scenario.CUT_CLEAN)) return

        for (i in e.drops.indices) {
            val material = when (e.drops[i].type) {
                Material.RAW_BEEF -> Material.COOKED_BEEF
                Material.RAW_CHICKEN -> Material.COOKED_CHICKEN
                Material.RABBIT -> Material.COOKED_RABBIT
                Material.MUTTON -> Material.COOKED_MUTTON
                Material.PORK -> Material.GRILLED_PORK
                else -> null
            } ?: continue

            e.drops[i] = ItemStack(material)
        }
    }
}
