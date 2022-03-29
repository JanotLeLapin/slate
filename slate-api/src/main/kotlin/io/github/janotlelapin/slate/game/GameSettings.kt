package io.github.janotlelapin.slate.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.block.Biome

abstract class GameSettings {
    /**
     * The display name of the game mode
     */
    abstract val brand: Component

    /**
     * Runs every second
     */
    abstract fun update(game: Game<out GameSettings>)

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
    val borderSize: Map<Array<Int>, Int> = hashMapOf(emptyArray<Int>() to 1000)

    /**
     * A list of biomes that the game should avoid using
     * in its world.
     */
    val badBiomes: Set<Biome> = hashSetOf(
        Biome.DEEP_OCEAN,
        Biome.DESERT,
        Biome.MESA,
        Biome.OCEAN,
    )

    /**
     * The message sent to each player when the border is shrinking
     */
    val shrinkBorderMessage: Component = Component
        .text("The border is shrinking.")
        .color(NamedTextColor.RED)
}

