package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.events.core.PacketEvent
import com.github.brainage04.togglesprint.gui.TPSTracker.hasPacketReceived
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PacketEventTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    fun onPacketEvent(event: PacketEvent.ReceiveEvent) {
        if (guiElements.tpsTracker.coreSettings.isEnabled) hasPacketReceived = true
    }
}