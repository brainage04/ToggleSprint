package io.github.brainage04.togglesprint.hud.core

import kotlin.math.floor

/**
 * Converts between an element's configured offset and its on-screen position.
 *
 * Anchors are the `CoreSettings.anchorCorner` dropdown indices: 0 top left, 1 top right, 2 bottom left,
 * 3 bottom right, 4 centre left, 5 centre right, 6 centre top, 7 centre bottom, 8 centre. On each axis an
 * offset is measured inwards from the anchored edge; on a centred axis it moves the element by half the
 * offset towards the start edge (`(screen - offset - size) / 2`), which is how existing configs are laid out.
 */
object ElementPlacement {
    enum class Axis { START, CENTRE, END }

    /** Anchors in reading order, the order the element editor cycles through them. */
    private val anchorCycle = intArrayOf(0, 6, 1, 4, 8, 5, 2, 7, 3)

    fun horizontalAxis(anchor: Int): Axis = when (anchor) {
        1, 3, 5 -> Axis.END
        6, 7, 8 -> Axis.CENTRE
        else -> Axis.START
    }

    fun verticalAxis(anchor: Int): Axis = when (anchor) {
        2, 3, 7 -> Axis.END
        4, 5, 8 -> Axis.CENTRE
        else -> Axis.START
    }

    /** The screen coordinate of the element's start edge for the configured [offset]. */
    fun position(axis: Axis, screenSize: Int, elementSize: Int, offset: Double): Int = floor(
        when (axis) {
            Axis.START -> offset
            Axis.END -> screenSize - offset - elementSize
            Axis.CENTRE -> (screenSize - offset - elementSize) / 2.0
        }
    ).toInt()

    /** The configured offset that places the element's start edge at [position]; the inverse of [position]. */
    fun offset(axis: Axis, screenSize: Int, elementSize: Int, position: Int): Double = when (axis) {
        Axis.START -> position.toDouble()
        Axis.END -> (screenSize - elementSize - position).toDouble()
        Axis.CENTRE -> (screenSize - elementSize - 2 * position).toDouble()
    }

    /** The anchor after [anchor] in reading order (top left, top, top right, left, ...), wrapping around. */
    fun nextAnchor(anchor: Int): Int {
        val index = anchorCycle.indexOf(anchor)
        return anchorCycle[(index + 1) % anchorCycle.size]
    }
}
