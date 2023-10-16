package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.gui.TPSTracker.tickTimes
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ServerTickEvent {
    @SubscribeEvent
    fun onServerTickEvent(event: TickEvent.ServerTickEvent) {
        tickTimes.add(System.currentTimeMillis())

        if (tickTimes.size > 100) {
            tickTimes.removeAt(0) // this should ensure tickTimes only contains the previous 100 tick times
        }
    }
}