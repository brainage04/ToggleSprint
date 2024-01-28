package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumFacing

object PlayerPositionTracker {
    fun playerPositionTracker() {
        if (!ConfigUtils.guiElements.positionTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf(
            "${ConfigUtils.primaryChars}X: ${Minecraft.getMinecraft().thePlayer.posX.round(ConfigUtils.guiElements.positionTracker.decimals)}",
            "${ConfigUtils.primaryChars}Y: ${Minecraft.getMinecraft().thePlayer.posY.round(ConfigUtils.guiElements.positionTracker.decimals)}",
            "${ConfigUtils.primaryChars}Z: ${Minecraft.getMinecraft().thePlayer.posZ.round(ConfigUtils.guiElements.positionTracker.decimals)}",
        )

        if (ConfigUtils.guiElements.positionTracker.showFacing) {
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

        if (ConfigUtils.guiElements.positionTracker.showChunkCounter) {
            val text = Minecraft.getMinecraft().renderGlobal.debugInfoRenders.split(" ")
            textArray.add("${ConfigUtils.primaryChars + text[0]} ${text[1]}")
        }

        if (ConfigUtils.guiElements.positionTracker.showEntityCounter) textArray.add(ConfigUtils.primaryChars + Minecraft.getMinecraft().renderGlobal.debugInfoEntities.split(",")[0])

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.positionTracker.coreSettings.x,
            ConfigUtils.guiElements.positionTracker.coreSettings.y,
            ConfigUtils.guiElements.positionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}