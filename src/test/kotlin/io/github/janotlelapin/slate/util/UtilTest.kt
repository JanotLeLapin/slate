package io.github.janotlelapin.slate.util

import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class UtilTest {
    private val air = mockBlock(Material.AIR)
    private val grass = mockBlock(Material.GRASS)
    private val stone = mockBlock(Material.STONE)

    private val w1 = mockWorld(67)
    private val w2 = mockWorld(52)
    private val w3 = mockWorld(89)

    private fun mockBlock(type: Material): Block {
        val block = mockk<Block>()
        every { block.type } returns type

        return block
    }

    private fun mockWorld(ground: Int): World {
        val world = mockk<World>()
        for (i in 0 until ground) { every { world.getBlockAt(0, i, 0) } returns stone }
        every { world.getBlockAt(0, ground, 0) } returns grass
        for (i in (ground + 1)..255) { every { world.getBlockAt(0, i, 0) } returns air }

        return world
    }

    @Test
    fun timeString() {
        Assertions.assertEquals("00:04", 4.toTimeString())
        Assertions.assertEquals("10:10", 610.toTimeString())
        Assertions.assertEquals("100:30", 6030.toTimeString())
    }

    @Test
    fun worldGround() {
        Assertions.assertEquals(68, w1.ground(0, 0))
        Assertions.assertEquals(53, w2.ground(0, 0))
        Assertions.assertEquals(90, w3.ground(0, 0))
    }
}
