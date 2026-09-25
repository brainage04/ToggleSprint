package io.github.brainage04.togglesprint.hud

import java.util.ArrayDeque

/**
 * Estimates the server's ticks per second from the game time it reports.
 *
 * The server sends its game time roughly once a second. Each report is compared with the previous one:
 * the game ticks that elapsed divided by the wall-clock time that elapsed is the rate at which the server
 * is actually ticking. Summing both over a window, rather than averaging per-interval rates, keeps
 * irregularly spaced reports correctly weighted. Not thread-safe: call it from one thread.
 */
class TickRateTracker {
    private class Interval(val ticks: Long, val nanos: Long)

    private val intervals = ArrayDeque<Interval>()
    private var totalTicks = 0L
    private var totalNanos = 0L
    private var hasPrevious = false
    private var previousGameTime = 0L
    private var previousNanos = 0L

    /**
     * Records a game time report received at [nowNanos], keeping at most [maxIntervals] intervals.
     * A game time that goes backwards means a different world, so the history restarts.
     */
    fun record(gameTime: Long, nowNanos: Long, maxIntervals: Int) {
        if (hasPrevious) {
            val ticks = gameTime - previousGameTime
            val nanos = nowNanos - previousNanos

            if (ticks < 0L || nanos <= 0L) {
                clearIntervals()
            } else {
                intervals.addLast(Interval(ticks, nanos))
                totalTicks += ticks
                totalNanos += nanos
            }
        }

        val limit = maxIntervals.coerceAtLeast(1)
        while (intervals.size > limit) {
            val oldest = intervals.removeFirst()
            totalTicks -= oldest.ticks
            totalNanos -= oldest.nanos
        }

        hasPrevious = true
        previousGameTime = gameTime
        previousNanos = nowNanos
    }

    /** Whether at least one interval has been measured since the last reset. */
    fun hasRate(): Boolean = totalNanos > 0L

    /** Game ticks per wall-clock second over the retained intervals, or 0 before any interval. */
    fun ticksPerSecond(): Double = if (hasRate()) totalTicks * 1_000_000_000.0 / totalNanos else 0.0

    fun reset() {
        clearIntervals()
        hasPrevious = false
    }

    private fun clearIntervals() {
        intervals.clear()
        totalTicks = 0L
        totalNanos = 0L
    }
}
