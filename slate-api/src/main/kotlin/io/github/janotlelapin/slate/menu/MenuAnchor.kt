package io.github.janotlelapin.slate.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MenuAnchor(val item: ItemStack, val to: Menu) : MenuElement() {
    override fun click(player: Player) {
        to.open(player)
    }

    override fun display(player: Player): ItemStack {
        return item
    }
}