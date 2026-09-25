package io.github.brainage04.togglesprint.hud.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ElementPlacementTest {
    @Test
    fun `offset puts the element back where the editor dropped it on every axis`() {
        for (axis in ElementPlacement.Axis.values()) {
            for (position in listOf(5, 17, 400)) {
                val offset = ElementPlacement.offset(axis, 427, 60, position)
                assertEquals(position, ElementPlacement.position(axis, 427, 60, offset), "$axis at $position")
            }
        }
    }

    @Test
    fun `offsets are measured inwards from the anchored edge`() {
        assertEquals(10, ElementPlacement.position(ElementPlacement.Axis.START, 400, 50, 10.0))
        assertEquals(340, ElementPlacement.position(ElementPlacement.Axis.END, 400, 50, 10.0))
        assertEquals(170, ElementPlacement.position(ElementPlacement.Axis.CENTRE, 400, 50, 10.0))
    }

    @Test
    fun `anchor cycle visits all nine anchors once`() {
        val seen = generateSequence(0) { ElementPlacement.nextAnchor(it) }.take(9).toSet()
        assertEquals((0..8).toSet(), seen)
        assertEquals(0, ElementPlacement.nextAnchor(3))
    }
}
