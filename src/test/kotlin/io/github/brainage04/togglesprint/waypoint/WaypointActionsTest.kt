package io.github.brainage04.togglesprint.waypoint

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WaypointActionsTest {
    @Test
    fun `empty and overlong waypoint names are rejected at the documented boundary`() {
        assertNotNull(WaypointActions.validateName(""))
        assertNull(WaypointActions.validateName("a".repeat(64)))
        assertNotNull(WaypointActions.validateName("a".repeat(65)))
    }

    @Test
    fun `formatting quotes and control characters cannot enter saved names`() {
        for (name in listOf("§aBase", "My \"Base\"", "Base\nHome", "Base\u0000", "Base\u007f")) {
            assertNotNull(WaypointActions.validateName(name), name)
        }
        assertNull(WaypointActions.validateName("Café 山 Base"))
    }

    @Test
    fun `waypoint chat colours choose the nearest palette colour`() {
        assertEquals('c', WaypointActions.chatColourCode(0xFF5555))
        assertEquals('c', WaypointActions.chatColourCode(0xFA5050))
        assertEquals('0', WaypointActions.chatColourCode(0x000000))
        assertEquals('f', WaypointActions.chatColourCode(0xFFFFFF))
    }
}
