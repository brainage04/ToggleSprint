package io.github.brainage04.togglesprint.hud

import kotlin.test.Test
import kotlin.test.assertEquals

class FishingChancesTest {
    @Test
    fun `an unenchanted rod uses Forge's base chances`() {
        assertEquals("Chances: Fish 85%, Treasure 5%, Junk 10%", FishingChances.line(luck = 0, lure = 0))
    }

    @Test
    fun `Luck of the Sea III and Lure III clamp junk at zero`() {
        assertEquals("Chances: Fish 95%, Treasure 5%, Junk 0%", FishingChances.line(luck = 3, lure = 3))
    }

    @Test
    fun `Luck of the Sea III trades junk for treasure`() {
        // 89.5% fish, 8% treasure, 2.5% junk
        assertEquals("Chances: Fish 90%, Treasure 8%, Junk 2%", FishingChances.line(luck = 3, lure = 0))
    }

    @Test
    fun `half percents round by largest remainder to a total of 100`() {
        // 86.5% fish, 6% treasure, 7.5% junk: the tied remainder goes to fish
        assertEquals(listOf(87, 6, 7), FishingChances.percentages(luck = 1, lure = 0).toList())

        for (luck in 0..10) {
            for (lure in 0..10) {
                assertEquals(100, FishingChances.percentages(luck, lure).sum(), "luck $luck, lure $lure")
            }
        }
    }
}
