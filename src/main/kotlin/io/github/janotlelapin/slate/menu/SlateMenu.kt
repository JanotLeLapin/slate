package io.github.janotlelapin.slate.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SlateMenu(private val inventory: Inventory) : Menu {
    val elements: HashMap<Number, MenuElement> = hashMapOf()

    override fun open(player: Player) {
        player.openInventory(inventory)
    }

    override fun element(slot: Number, element: MenuElement): SlateMenu {
        elements[slot] = element
        return this
    }

    fun draw() {
        inventory.viewers.forEach {
            if (it !is Player) return
            inventory.clear()
            elements.forEach { (s, e) -> if (!e.hidden) inventory.setItem(s.toInt(), e.display(it)) }
        }
    }
}
