package io.github.janotlelapin.slate

import io.github.janotlelapin.slate.game.GameManager

interface SlatePlugin {
    /**
     * A manager for every running Slate game
     */
    val gameManager: GameManager
}
