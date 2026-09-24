package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing
import net.minecraft.util.BlockPos
import net.minecraft.world.EnumSkyBlock

object PlayerPositionTracker {
    fun lines(): List<String> {
        val player = Minecraft.getMinecraft().thePlayer ?: return emptyList()
        val config = ConfigUtils.guiElements.positionTracker
        val blockPos = BlockPos(player.posX, player.posY, player.posZ)
        val chunkX = if (config.showChunkPosition) " [${blockPos.x and 15}]" else ""
        val chunkY = if (config.showChunkPosition) " [${blockPos.y and 15}]" else ""
        val chunkZ = if (config.showChunkPosition) " [${blockPos.z and 15}]" else ""
        val textArray = arrayListOf(
            "X: ${player.posX.round(config.decimals)}$chunkX",
            "Y: ${player.posY.round(config.decimals)}$chunkY",
            "Z: ${player.posZ.round(config.decimals)}$chunkZ",
        )

        if (config.showFacing) {
            val facing = player.horizontalFacing

            val facingString = when (facing) {
                EnumFacing.NORTH -> "North (-Z)"
                EnumFacing.SOUTH -> "South (+Z)"
                EnumFacing.WEST -> "West (-X)"
                EnumFacing.EAST -> "East (+X)"
                else -> "???"
            }

            textArray.add("Facing: $facingString")
        }

        if (config.showLightLevels) {
            val world = player.worldObj
            textArray.add("Light: ${world.getLightFor(EnumSkyBlock.SKY, blockPos)}/${world.getLightFor(EnumSkyBlock.BLOCK, blockPos)}")
        }
        if (config.showBiome) {
            textArray.add("Biome: ${player.worldObj.getBiomeGenForCoords(blockPos).biomeName}")
        }
        if (config.showChunkCounter) {
            val parts = Minecraft.getMinecraft().renderGlobal.debugInfoRenders.split(" ")
            if (parts.size > 1) textArray.add("${parts[0]} ${parts[1]}")
        }
        if (config.showEntityCounter) {
            val entities = Minecraft.getMinecraft().renderGlobal.debugInfoEntities.split(",")
            if (entities.isNotEmpty()) textArray.add(entities[0])
        }

        return textArray
    }
}