package io.github.brainage04.togglesprint.hud

import kotlin.test.Test
import kotlin.test.assertEquals

class StatusEffectHudTest {
    @Test
    fun `durations under an hour are minutes and padded seconds`() {
        assertEquals("0:00", StatusEffectHud.formatDuration(0))
        assertEquals("0:05", StatusEffectHud.formatDuration(5 * 20))
        assertEquals("1:23", StatusEffectHud.formatDuration(83 * 20))
        assertEquals("59:59", StatusEffectHud.formatDuration(3599 * 20))
    }

    @Test
    fun `partial seconds round down`() {
        assertEquals("0:00", StatusEffectHud.formatDuration(19))
        assertEquals("0:01", StatusEffectHud.formatDuration(39))
    }

    @Test
    fun `durations from an hour gain an hours field and padded minutes`() {
        assertEquals("1:00:00", StatusEffectHud.formatDuration(3600 * 20))
        assertEquals("2:03:04", StatusEffectHud.formatDuration((2 * 3600 + 3 * 60 + 4) * 20))
    }

    @Test
    fun `level I and a hidden duration are left out`() {
        assertEquals("Speed II: 1:23", StatusEffectHud.line("Speed", "II", "1:23"))
        assertEquals("Regeneration: 0:05", StatusEffectHud.line("Regeneration", "", "0:05"))
        assertEquals("Speed II", StatusEffectHud.line("Speed", "II", null))
        assertEquals("Regeneration", StatusEffectHud.line("Regeneration", "", null))
    }
}
