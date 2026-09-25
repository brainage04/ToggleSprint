package io.github.brainage04.togglesprint.hud

import net.minecraft.block.BlockLiquid
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

object FishingHud {
    private const val NOT_IN_WATER = "Bobber: not in water"
    private const val OPEN_WATER = "Open water: yes"
    private const val NOT_OPEN_WATER = "Open water: no (needs 5x5 water, 2 deep, open above)"
    private const val TREASURE_POSSIBLE = "Treasure: possible"
    private const val TREASURE_IMPOSSIBLE = "Treasure: impossible"

    /** The active cast's open-water and treasure state; nothing until a bobber is out. */
    fun lines(): List<String> {
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return emptyList()
        val fishHook = player.fishEntity ?: return emptyList()
        val world = minecraft.theWorld ?: return emptyList()
        val lines = arrayListOf<String>()

        val hookPosition = BlockPos(fishHook.posX, fishHook.posY, fishHook.posZ)
        if (world.getBlockState(hookPosition).block.material != Material.water) {
            lines.add(NOT_IN_WATER)
        } else {
            val openWater = FishingOpenWater.isOpenWater(
                hookPosition.x,
                hookPosition.y,
                hookPosition.z,
                object : FishingOpenWater.BlockLookup {
                    override fun blockAt(x: Int, y: Int, z: Int): FishingOpenWater.BlockType {
                        val state = world.getBlockState(BlockPos(x, y, z))
                        val block = state.block
                        return when {
                            (block == Blocks.water || block == Blocks.flowing_water) &&
                                state.getValue(BlockLiquid.LEVEL) == 0 -> FishingOpenWater.BlockType.WATER
                            block == Blocks.air || block == Blocks.waterlily -> FishingOpenWater.BlockType.ABOVE_WATER
                            else -> FishingOpenWater.BlockType.OTHER
                        }
                    }
                },
            )
            lines.add(if (openWater) OPEN_WATER else NOT_OPEN_WATER)
            // vanilla's fishing loot table only rolls treasure for a hook in open water
            lines.add(if (openWater) TREASURE_POSSIBLE else TREASURE_IMPOSSIBLE)
        }

        return lines
    }
}
