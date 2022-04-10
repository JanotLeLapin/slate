package io.github.janotlelapin.slate.menu

import org.bukkit.entity.Player

interface Menu {
    /**
     * Opens the menu for a player
     */
    fun open(player: Player)

    /**
     * Adds an element to the menu
     */
    fun element(slot: Number, element: MenuElement): Menu
}
