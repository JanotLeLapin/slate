package io.github.janotlelapin.slate.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.block.Biome

abstract class GameSettings {
    /**
     * The game instance linked to this GameSettings
     */
    abstract val game: Game<out GameSettings>

    /**
     * The display name of the game mode
     */
    abstract val brand: Component

    /**
     * Runs every second
     */
    abstract fun update()

    /**
     * Runs when the game starts
     */
    abstract fun start()

    /**
     * Border size and time specifications
     *
     * <hr><blockquote><pre>
     *
     *     hashMapOf(
     *         emptyArray<Int>() to 1000,
     *         arrayOf(60, 70) to 800,
     *     )
     * </pre></blockquote><hr>
     *
     * The above snippet would make the border stay at
     * 1000 squared blocks for the first 50 minutes,
     * then reduce to 800 squared blocks from 60 to
     * 70 minutes.
     *
     * Defaults to a static border of 1000 square meters
     */
    open val borderSize: Map<Array<Int>, Int> = hashMapOf(emptyArray<Int>() to 1000)

    /**
     * A list of biomes that the game should avoid using
     * in its world.
     */
    open val badBiomes: Set<Biome> = hashSetOf(
        Biome.DEEP_OCEAN,
        Biome.DESERT,
        Biome.MESA,
        Biome.OCEAN,
    )

    /**
     * The message sent to each player when the border is shrinking
     */
    open val shrinkBorderMessage: Component = Component
        .text("The border is shrinking.")
        .color(NamedTextColor.RED)

    /**
     * Whether the chat is blocked on the game
     */
    open val disableChat: Boolean = true

    /**
     * The message sent to a player when the border is disabled
     */
    open val noChatMessage: Component = Component
        .text("The chat is disabled.")
        .color(NamedTextColor.RED)
}
