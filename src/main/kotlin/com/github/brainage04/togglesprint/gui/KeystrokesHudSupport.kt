package com.github.brainage04.togglesprint.gui

/** Pure state helpers so input transitions are deterministic independently of rendering. */
object KeystrokesHudSupport {
    const val TRANSITION_NANOS = 100_000_000L

    data class Transition(var progress: Float, var down: Boolean, var updatedAtNanos: Long)

    fun updateTransition(transition: Transition, down: Boolean, nowNanos: Long): Float {
        val elapsed = Math.max(0L, nowNanos - transition.updatedAtNanos)
        val amount = Math.min(1f, elapsed.toFloat() / TRANSITION_NANOS.toFloat())
        transition.progress = if (down) Math.min(1f, transition.progress + amount) else Math.max(0f, transition.progress - amount)
        transition.down = down
        transition.updatedAtNanos = nowNanos
        return transition.progress
    }

    fun lerpRgb(from: Int, to: Int, progress: Float): Int {
        val t = Math.max(0f, Math.min(1f, progress))
        val red = ((from ushr 16 and 0xFF) + ((to ushr 16 and 0xFF) - (from ushr 16 and 0xFF)) * t).toInt()
        val green = ((from ushr 8 and 0xFF) + ((to ushr 8 and 0xFF) - (from ushr 8 and 0xFF)) * t).toInt()
        val blue = ((from and 0xFF) + ((to and 0xFF) - (from and 0xFF)) * t).toInt()
        return red shl 16 or (green shl 8) or blue
    }

    fun withAlpha(rgb: Int, alpha: Int): Int = (Math.max(0, Math.min(255, alpha)) shl 24) or (rgb and 0xFFFFFF)

    fun expireClicks(clickTimes: MutableList<Long>, nowMillis: Long) {
        while (clickTimes.isNotEmpty() && nowMillis - clickTimes[0] > 1_000L) clickTimes.removeAt(0)
    }
}
