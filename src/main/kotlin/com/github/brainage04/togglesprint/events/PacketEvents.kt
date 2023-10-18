package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.events.core.PacketEvent
import com.github.brainage04.togglesprint.gui.TPSTracker
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class PacketEvents {
    private val guiElements get() = ToggleSprintMain.config.guiElements

    @SubscribeEvent
    fun onPacketEvent(event: PacketEvent.ReceiveEvent) {
        if (!guiElements.tpsTracker.coreSettings.isEnabled) {
            TPSTracker.tickTimes.add(System.currentTimeMillis())

            if (TPSTracker.tickTimes.size > guiElements.tpsTracker.tickRange) {
                TPSTracker.tickTimes.removeAt(0) // this should ensure tickTimes only contains the previous 100 (default) tick times
            }
        }
    }
}