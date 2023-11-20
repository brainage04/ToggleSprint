package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft
import net.minecraft.network.play.client.C16PacketClientStatus
import kotlin.concurrent.fixedRateTimer

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

    var lastPingAt = -1L
    var pingCache = -1L

    fun pingTracker() {
        if (!guiElements.pingTracker.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        if (minecraft.thePlayer == null) return

        val text = if (guiElements.pingTracker.showColor) "§fPing: ${getColor(pingCache) + pingCache}ms"
        else "§fPing: ${pingCache}ms"

        RenderGuiData.renderElement(
            guiElements.pingTracker.coreSettings.x,
            guiElements.pingTracker.coreSettings.y,
            guiElements.pingTracker.coreSettings.anchorCorner,
            text,
        )
    }

    init {
        fixedRateTimer(name = "${ToggleSprintMain.MOD_ID}-send-ping", period = 1_000L) {
            val minecraft = Minecraft.getMinecraft() ?: return@fixedRateTimer
            val player = minecraft.thePlayer ?: return@fixedRateTimer

            if (lastPingAt > 0L) return@fixedRateTimer // waits until previous ping has completed

            player.sendQueue.networkManager.sendPacket( // requests server stats
                C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS)
            )

            lastPingAt = System.nanoTime() // get timestamp of outgoing packet
        }
    }
}