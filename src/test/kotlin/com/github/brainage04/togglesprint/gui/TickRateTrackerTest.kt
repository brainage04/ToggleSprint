package com.github.brainage04.togglesprint.gui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TickRateTrackerTest {
    private companion object {
        const val SECOND = 1_000_000_000L
    }

    @Test
    fun `needs two reports before it has a rate`() {
        val tracker = TickRateTracker()

        tracker.record(1000L, 0L, 3)

        assertFalse(tracker.hasRate())
    }

    @Test
    fun `measures how fast the server actually ticks`() {
        val tracker = TickRateTracker()

        // a lagging server takes two seconds to run each batch of 20 ticks
        for (report in 0..3) {
            tracker.record(1000L + report * 20L, report * 2L * SECOND, 3)
        }

        assertEquals(10.0, tracker.ticksPerSecond(), 1e-9)
    }

    @Test
    fun `weights irregular reports by elapsed time`() {
        val tracker = TickRateTracker()

        tracker.record(0L, 0L, 5)
        tracker.record(20L, SECOND, 5)
        // a lagging half second after a full-speed second: averaging the two rates (20 and 10)
        // would give 15, but 25 ticks ran in 1.5 seconds
        tracker.record(25L, SECOND + SECOND / 2, 5)

        assertEquals(25.0 / 1.5, tracker.ticksPerSecond(), 1e-9)
    }

    @Test
    fun `forgets older intervals when the window shrinks`() {
        val tracker = TickRateTracker()

        // ten slow intervals, then two healthy ones with the window lowered to two intervals
        for (report in 0..10) {
            tracker.record(report * 20L, report * 2L * SECOND, 30)
        }
        tracker.record(220L, 21L * SECOND, 2)
        tracker.record(240L, 22L * SECOND, 2)

        assertEquals(20.0, tracker.ticksPerSecond(), 1e-9)
    }

    @Test
    fun `restarts when game time goes backwards`() {
        val tracker = TickRateTracker()
        tracker.record(100_000L, 0L, 3)
        tracker.record(100_010L, SECOND, 3)

        // joining another world whose clock is behind the previous one
        tracker.record(500L, 2L * SECOND, 3)
        assertFalse(tracker.hasRate())

        tracker.record(520L, 3L * SECOND, 3)
        assertTrue(tracker.hasRate())
        assertEquals(20.0, tracker.ticksPerSecond(), 1e-9)
    }

    @Test
    fun `reset discards the previous report`() {
        val tracker = TickRateTracker()
        tracker.record(0L, 0L, 3)
        tracker.record(20L, SECOND, 3)

        tracker.reset()
        // a new world far ahead in game time must not be compared with the old one
        tracker.record(1_000_000L, 2L * SECOND, 3)

        assertFalse(tracker.hasRate())
    }
}
