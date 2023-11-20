package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.events.core.PacketEvent
import com.github.brainage04.togglesprint.gui.PingTracker
import com.github.brainage04.togglesprint.gui.TPSTracker.hasPacketReceived
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S37PacketStatistics
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs

class PacketEventTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    fun onPacketEvent(event: PacketEvent.ReceiveEvent) {
        // TPS Tracker
        if (guiElements.tpsTracker.coreSettings.isEnabled) hasPacketReceived = true

        // Ping Tracker
        if (PingTracker.lastPingAt > 0L) { // if waiting for ping, check packet type
            when (event.packet) {
                is S01PacketJoinGame -> {
                    PingTracker.lastPingAt = -1L

                    ChatUtils.messageToChat("Joined world. Not a ping.")
                }

                is S37PacketStatistics -> {
                    val diff = (abs(System.nanoTime() - PingTracker.lastPingAt) / 1_000_000)
                    PingTracker.lastPingAt = -1L
                    PingTracker.pingCache = diff

                    ChatUtils.messageToChat("Server pinged.")
                }
            }
        }
    }
}