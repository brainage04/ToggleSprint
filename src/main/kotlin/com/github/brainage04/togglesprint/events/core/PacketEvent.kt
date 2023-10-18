package com.github.brainage04.togglesprint.events.core

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
/**
 * Note: This event is async and may not be executed on the main minecraft thread.
 */
abstract class PacketEvent : Event() {
    abstract val direction: Direction
    abstract val packet: Packet<*>

    /**
     * Note: This event is async and may not be executed on the main minecraft thread.
     */
    data class ReceiveEvent(override val packet: Packet<*>) : PacketEvent() {
        override val direction = Direction.INBOUND
    }

    /**
     * Note: This event is async and may not be executed on the main minecraft thread.
     */
    data class SendEvent(override val packet: Packet<*>) : PacketEvent() {
        override val direction = Direction.OUTBOUND
    }

    enum class Direction {
        INBOUND, OUTBOUND
    }
}