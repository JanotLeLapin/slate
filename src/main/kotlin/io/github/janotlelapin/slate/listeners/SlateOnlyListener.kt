package io.github.janotlelapin.slate.listeners

import io.github.janotlelapin.slate.game.GameSettings
import io.github.janotlelapin.slate.util.game
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent

class SlateOnlyListener : Listener {
    @EventHandler
    fun onFood(e: FoodLevelChangeEvent) {
        val p = e.entity
        if (p !is Player) return
        if (p.game<GameSettings>()?.running() != true)
            e.foodLevel = 20
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        val p = e.entity
        if (p is Player && p.game<GameSettings>()?.running() != true) e.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.player.game<GameSettings>()?.running() != true) e.isCancelled = true
    }
}