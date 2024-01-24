package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.MathUtils.round
import net.minecraft.client.Minecraft

object PlayerPositionTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun playerPositionTracker() {
        if (!guiElements.positionTracker.coreSettings.isEnabled) return

        val textArray = arrayListOf(
            "${ChatUtils.whiteChar}X: ${Minecraft.getMinecraft().thePlayer.posX.round(guiElements.positionTracker.decimals)}",
            "${ChatUtils.whiteChar}Y: ${Minecraft.getMinecraft().thePlayer.posY.round(guiElements.positionTracker.decimals)}",
            "${ChatUtils.whiteChar}Z: ${Minecraft.getMinecraft().thePlayer.posZ.round(guiElements.positionTracker.decimals)}",
        )

        if (guiElements.positionTracker.showChunkCounter) {
            val text = ChatUtils.whiteChar + Minecraft.getMinecraft().renderGlobal.debugInfoRenders.split(" ")
            textArray.add("${ChatUtils.whiteChar + text[0]} ${text[1]}")
        }
        if (guiElements.positionTracker.showEntityCounter) textArray.add(Minecraft.getMinecraft().renderGlobal.debugInfoEntities.split(",")[0])

        RenderGuiData.renderElement(
            guiElements.positionTracker.coreSettings.x,
            guiElements.positionTracker.coreSettings.y,
            guiElements.positionTracker.coreSettings.anchorCorner,
            textArray,
        )
    }
}