package com.github.brainage04.togglesprint.gui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TpsSamplerTest {
    @Test
    fun `ten packet-bearing ticks in half a second report twenty TPS`() {
        val sampler = TpsSampler(intervalTicks = 10, intervalsTracked = 3)

        repeat(9) {
            sampler.recordPacket()
            assertFalse(sampler.tick())
        }
        sampler.recordPacket()

        assertTrue(sampler.tick())
        assertEquals(20.0, sampler.tps)
    }

    @Test
    fun `multiple packets during one client tick count once`() {
        val sampler = TpsSampler(intervalTicks = 2, intervalsTracked = 1)

        repeat(5) { sampler.recordPacket() }
        assertFalse(sampler.tick())
        assertTrue(sampler.tick())

        assertEquals(10.0, sampler.tps)
    }

    @Test
    fun `rolling average drops the oldest interval`() {
        val sampler = TpsSampler(intervalTicks = 2, intervalsTracked = 2)

        repeat(2) {
            sampler.recordPacket()
            sampler.tick()
        }
        sampler.recordPacket()
        sampler.tick()
        sampler.tick()
        assertEquals(15.0, sampler.tps)

        repeat(2) { sampler.tick() }
        assertEquals(5.0, sampler.tps)
    }

    @Test
    fun `reset clears samples and pending packets`() {
        val sampler = TpsSampler(intervalTicks = 1, intervalsTracked = 2)
        sampler.recordPacket()
        sampler.tick()
        sampler.recordPacket()

        sampler.reset()

        assertTrue(sampler.tick())
        assertEquals(0.0, sampler.tps)
    }
}
