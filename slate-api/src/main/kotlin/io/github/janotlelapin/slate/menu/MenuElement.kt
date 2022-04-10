package io.github.janotlelapin.slate.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class MenuElement {
    /**
     * Whether the element should be hidden
     */
    var hidden: Boolean = false

    /**
     * @return Called when the element is clicked
     */
    abstract fun click(player: Player)

    /**
     * @return An item stack from the element
     */
    abstract fun display(player: Player): ItemStack
}
