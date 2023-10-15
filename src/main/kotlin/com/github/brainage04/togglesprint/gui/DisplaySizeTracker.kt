package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

object DisplaySizeTracker {
    var textArray = arrayOf(
        "§fWidth: ${Minecraft.getMinecraft().displayWidth}",
        "§fHeight: ${Minecraft.getMinecraft().displayHeight}",
    )

    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun displaySizeTracker() {
        if (!guiElements.displaySizeElement.isEnabled) return

        textArray = arrayOf(
            "§fWidth: ${Minecraft.getMinecraft().displayWidth}",
            "§fHeight: ${Minecraft.getMinecraft().displayHeight}",
        )

        RenderGuiData.renderElement(
            guiElements.displaySizeElement.x,
            guiElements.displaySizeElement.y,
            guiElements.displaySizeElement.anchorCorner,
            textArray,
        )
    }
}