package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerPositionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val globalGuiSettings get() = ToggleSprintMain.config.globalGuiSettings

    val primaryChars = ChatUtils.colourChars[globalGuiSettings.primaryColour] + ChatUtils.effectChars[globalGuiSettings.primaryEffect]

    fun playerPositionTracker() {
        if (!guiElements.positionTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf(
            "${primaryChars}X: ${Minecraft.getMinecraft().thePlayer.posX.round(guiElements.positionTracker.decimals)}",
            "${primaryChars}Y: ${Minecraft.getMinecraft().thePlayer.posY.round(guiElements.positionTracker.decimals)}",
            "${primaryChars}Z: ${Minecraft.getMinecraft().thePlayer.posZ.round(guiElements.positionTracker.decimals)}",
        )

        if (guiElements.positionTracker.showChunkCounter) {
            val text = primaryChars + Minecraft.getMinecraft().renderGlobal.debugInfoRenders.split(" ")
            textArray.add("${primaryChars + text[0]} ${text[1]}")
        }
        if (guiElements.positionTracker.showEntityCounter) textArray.add(primaryChars + Minecraft.getMinecraft().renderGlobal.debugInfoEntities.split(",")[0])

        RenderGuiData.renderElement(
            guiElements.positionTracker.coreSettings.x,
            guiElements.positionTracker.coreSettings.y,
            guiElements.positionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}