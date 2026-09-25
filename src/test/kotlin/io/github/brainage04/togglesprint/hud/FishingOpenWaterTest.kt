package io.github.brainage04.togglesprint.hud

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FishingOpenWaterTest {
    @Test
    fun `accepts two water layers beneath two above-water layers`() {
        assertTrue(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, validVolume()))
    }

    @Test
    fun `accepts a surface hook with only the bottom layer in water`() {
        val volume = lookup { _, y, _ ->
            if (y == HOOK_Y - 1) FishingOpenWater.BlockType.WATER else FishingOpenWater.BlockType.ABOVE_WATER
        }

        assertTrue(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, volume))
    }

    @Test
    fun `rejects an obstruction anywhere in the required volume`() {
        val volume = validVolume(mutableMapOf(Triple(HOOK_X + 2, HOOK_Y + 2, HOOK_Z - 2) to FishingOpenWater.BlockType.OTHER))

        assertFalse(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, volume))
    }

    @Test
    fun `rejects a mixed layer`() {
        val volume = validVolume(mutableMapOf(Triple(HOOK_X, HOOK_Y, HOOK_Z) to FishingOpenWater.BlockType.ABOVE_WATER))

        assertFalse(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, volume))
    }

    @Test
    fun `rejects water above an above-water layer`() {
        val volume = lookup { _, y, _ ->
            when (y - HOOK_Y) {
                -1 -> FishingOpenWater.BlockType.WATER
                0 -> FishingOpenWater.BlockType.ABOVE_WATER
                1 -> FishingOpenWater.BlockType.WATER
                else -> FishingOpenWater.BlockType.ABOVE_WATER
            }
        }

        assertFalse(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, volume))
    }

    @Test
    fun `rejects above-water blocks in the bottom layer`() {
        val volume = lookup { _, y, _ ->
            if (y == HOOK_Y - 1) FishingOpenWater.BlockType.ABOVE_WATER else FishingOpenWater.BlockType.WATER
        }

        assertFalse(FishingOpenWater.isOpenWater(HOOK_X, HOOK_Y, HOOK_Z, volume))
    }

    private fun validVolume(
        overrides: MutableMap<Triple<Int, Int, Int>, FishingOpenWater.BlockType> = mutableMapOf(),
    ): FishingOpenWater.BlockLookup = lookup { x, y, z ->
        overrides[Triple(x, y, z)] ?: if (y <= HOOK_Y) {
            FishingOpenWater.BlockType.WATER
        } else {
            FishingOpenWater.BlockType.ABOVE_WATER
        }
    }

    private fun lookup(blockAt: (Int, Int, Int) -> FishingOpenWater.BlockType): FishingOpenWater.BlockLookup =
        object : FishingOpenWater.BlockLookup {
            override fun blockAt(x: Int, y: Int, z: Int): FishingOpenWater.BlockType = blockAt(x, y, z)
        }

    private companion object {
        const val HOOK_X = 12
        const val HOOK_Y = 64
        const val HOOK_Z = -7
    }
}
