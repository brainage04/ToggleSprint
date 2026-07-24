package com.github.brainage04.togglesprint.gui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class KeystrokesHudSupportTest {
    @Test
    fun `transition rises and falls according to elapsed time`() {
        val transition = KeystrokesHudSupport.Transition(progress = 0f, down = false, updatedAtNanos = 1_000L)

        assertEquals(0.5f, KeystrokesHudSupport.updateTransition(transition, true, 1_000L + KeystrokesHudSupport.TRANSITION_NANOS / 2))
        assertEquals(1f, KeystrokesHudSupport.updateTransition(transition, true, 1_000L + KeystrokesHudSupport.TRANSITION_NANOS * 2))
        assertEquals(0f, KeystrokesHudSupport.updateTransition(transition, false, 1_000L + KeystrokesHudSupport.TRANSITION_NANOS * 4))
    }

    @Test
    fun `transition ignores a clock moving backwards`() {
        val transition = KeystrokesHudSupport.Transition(progress = 0.4f, down = true, updatedAtNanos = 10_000L)

        assertEquals(0.4f, KeystrokesHudSupport.updateTransition(transition, false, 9_000L))
        assertEquals(9_000L, transition.updatedAtNanos)
    }

    @Test
    fun `rgb interpolation clamps progress and interpolates channels`() {
        assertEquals(0x123456, KeystrokesHudSupport.lerpRgb(0x123456, 0xABCDEF, -1f))
        assertEquals(0x7F7F7F, KeystrokesHudSupport.lerpRgb(0x000000, 0xFFFFFF, 0.5f))
        assertEquals(0xABCDEF, KeystrokesHudSupport.lerpRgb(0x123456, 0xABCDEF, 2f))
    }

    @Test
    fun `alpha clamps without changing rgb channels`() {
        assertEquals(0x00123456, KeystrokesHudSupport.withAlpha(0xFF123456.toInt(), -1))
        assertEquals(0x80123456.toInt(), KeystrokesHudSupport.withAlpha(0x00123456, 128))
        assertEquals(0xFF123456.toInt(), KeystrokesHudSupport.withAlpha(0x00123456, 300))
    }

    @Test
    fun `CPS labels match every configured format`() {
        assertNull(KeystrokesHud.cpsLabel(0, 4, 7))
        assertEquals("4 CPS", KeystrokesHud.cpsLabel(1, 4, 7))
        assertEquals("7 CPS (R)", KeystrokesHud.cpsLabel(2, 4, 7))
        assertEquals("4 | 7 CPS", KeystrokesHud.cpsLabel(3, 4, 7))
    }

    @Test
    fun `click expiry retains the inclusive one-second boundary`() {
        val clickTimes = mutableListOf(999L, 1_000L, 1_500L, 2_000L)

        KeystrokesHudSupport.expireClicks(clickTimes, 2_000L)

        assertEquals(listOf(1_000L, 1_500L, 2_000L), clickTimes)
    }
}
