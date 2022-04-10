package io.github.janotlelapin.slate.util

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(material: Material) {
    private val stack: ItemStack = ItemStack(material)
    private val meta: ItemMeta = stack.itemMeta

    fun amount(amount: Number): ItemBuilder {
        stack.amount = amount.toInt()
        return this
    }

    fun name(name: Component): ItemBuilder {
        meta.displayName = name.legacy()
        return this
    }

    fun build(): ItemStack {
        stack.itemMeta = meta
        return stack
    }
}