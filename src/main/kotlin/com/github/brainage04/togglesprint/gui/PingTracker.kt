package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

object PingTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    fun pingTracker() {
        if (!guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return
        val currentServer = minecraft.currentServerData ?: return

        val ping = currentServer.pingToServer

        val prefix = when {
            ping < 50 -> "§2"
            ping < 100 -> "§a"
            ping < 150 -> "§6"
            ping < 200 -> "§c"
            else -> "§4"
        }

        val text = "§fTPS: ${prefix + ping}ms"

        RenderGuiData.renderElement(
            guiElements.pingTracker.coreSettings.x,
            guiElements.pingTracker.coreSettings.y,
            guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }
}