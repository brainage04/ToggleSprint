package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.block.BlockLiquid
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

object FishingHud {
    private const val TREASURE_UNAVAILABLE = "Treasure: unavailable"
    private const val TREASURE_ELIGIBLE = "Treasure: eligible"
    private const val TREASURE_INELIGIBLE = "Treasure: ineligible"

    /** Renders the active cast's open-water and treasure state. */
    fun fishingHud() {
        val settings = ConfigUtils.brainageHudParity.fishing.coreSettings
        if (!settings.isEnabled) return

        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return
        val fishHook = player.fishEntity
        val lines = arrayListOf<String>()

        if (fishHook == null) {
            lines.add(ConfigUtils.primaryChars + TREASURE_UNAVAILABLE)
        } else {
            val world = minecraft.theWorld ?: return
            val hookPosition = BlockPos(fishHook.posX, fishHook.posY, fishHook.posZ)
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
            lines.add("${ConfigUtils.primaryChars}Open water: $openWater")
            lines.add(ConfigUtils.primaryChars + if (openWater) TREASURE_ELIGIBLE else TREASURE_INELIGIBLE)
        }

        RenderGuiData.renderElement(settings.x, settings.y, settings.anchorCorner, lines)
    }
}
