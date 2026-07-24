package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraft.util.BlockPos
import net.minecraft.world.EnumSkyBlock

object PlayerPositionTracker {
    fun playerPositionTracker() {
        if (!ConfigUtils.guiElements.positionTracker.coreSettings.isEnabled) return

        val player = Minecraft.getMinecraft().thePlayer ?: return
        val config = ConfigUtils.guiElements.positionTracker
        val blockPos = BlockPos(player.posX, player.posY, player.posZ)
        val chunkX = if (config.showChunkPosition) " [${blockPos.x and 15}]" else ""
        val chunkY = if (config.showChunkPosition) " [${blockPos.y and 15}]" else ""
        val chunkZ = if (config.showChunkPosition) " [${blockPos.z and 15}]" else ""
        val textArray = arrayListOf(
            "${ConfigUtils.primaryChars}X: ${player.posX.round(config.decimals)}$chunkX",
            "${ConfigUtils.primaryChars}Y: ${player.posY.round(config.decimals)}$chunkY",
            "${ConfigUtils.primaryChars}Z: ${player.posZ.round(config.decimals)}$chunkZ",
        )

        if (config.showFacing) {
            val facing = Minecraft.getMinecraft().thePlayer.horizontalFacing

            val facingString = when (facing) {
                EnumFacing.NORTH -> "North (-Z)"
                EnumFacing.SOUTH -> "South (+Z)"
                EnumFacing.WEST -> "West (-X)"
                EnumFacing.EAST -> "East (+X)"
                else -> "???"
            }

            textArray.add("${ConfigUtils.primaryChars}Facing: $facingString")
        }

        if (config.showLightLevels) {
            val world = player.worldObj
            textArray.add("${ConfigUtils.primaryChars}Light: ${world.getLightFor(EnumSkyBlock.SKY, blockPos)}/${world.getLightFor(EnumSkyBlock.BLOCK, blockPos)}")
        }
        if (config.showBiome) {
            textArray.add("${ConfigUtils.primaryChars}Biome: ${player.worldObj.getBiomeGenForCoords(blockPos).biomeName}")
        }
        if (config.showChunkCounter) {
            val parts = Minecraft.getMinecraft().renderGlobal.debugInfoRenders.split(" ")
            if (parts.size > 1) textArray.add("${ConfigUtils.primaryChars + parts[0]} ${parts[1]}")
        }
        if (config.showEntityCounter) {
            val entities = Minecraft.getMinecraft().renderGlobal.debugInfoEntities.split(",")
            if (entities.isNotEmpty()) textArray.add(ConfigUtils.primaryChars + entities[0])
        }

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.positionTracker.coreSettings.x,
            ConfigUtils.guiElements.positionTracker.coreSettings.y,
            ConfigUtils.guiElements.positionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}