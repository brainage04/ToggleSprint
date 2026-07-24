package com.github.brainage04.togglesprint.gui

import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicBoolean

/** Estimates server TPS from the proportion of client ticks that receive packets. */
internal class TpsSampler(
    private val intervalTicks: Int = 10,
    private val intervalsTracked: Int = 3,
) {
    private val packetReceivedThisTick = AtomicBoolean()
    private val samples = ArrayDeque<Int>(intervalsTracked)
    private var elapsedTicks = 0
    private var packetTicks = 0

    var tps = 0.0
        private set

    init {
        require(intervalTicks > 0)
        require(intervalsTracked > 0)
    }

    fun recordPacket() {
        packetReceivedThisTick.set(true)
    }

    /** Returns true when this tick completes a sampling interval. */
    fun tick(): Boolean {
        if (packetReceivedThisTick.getAndSet(false)) packetTicks++
        elapsedTicks++
        if (elapsedTicks < intervalTicks) return false

        if (samples.size == intervalsTracked) samples.removeFirst()
        samples.addLast(packetTicks)

        var totalPacketTicks = 0
        for (sample in samples) totalPacketTicks += sample
        tps = (totalPacketTicks.toDouble() / samples.size * 20.0 / intervalTicks).coerceAtMost(20.0)

        elapsedTicks = 0
        packetTicks = 0
        return true
    }

    fun reset() {
        packetReceivedThisTick.set(false)
        samples.clear()
        elapsedTicks = 0
        packetTicks = 0
        tps = 0.0
    }
}
