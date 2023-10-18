package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

object PingTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun getColor(ping: Long): String {
        return when {
            ping < 50 -> "§2"
            ping < 100 -> "§a"
            ping < 200 -> "§6"
            ping < 300 -> "§c"
            else -> "§4"
        }
    }

    fun pingTracker() {
        if (!guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return
        val currentServer = minecraft.currentServerData ?: return

        val ping = currentServer.pingToServer

        val text = if (guiElements.pingTracker.showColor) "§fPing: ${getColor(ping) + ping}ms"
        else "§fPing: ${ping}ms"

        RenderGuiData.renderElement(
            guiElements.pingTracker.coreSettings.x,
            guiElements.pingTracker.coreSettings.y,
            guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }
}