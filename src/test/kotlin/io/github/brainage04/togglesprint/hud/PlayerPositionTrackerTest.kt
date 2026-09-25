package io.github.brainage04.togglesprint.hud

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayerPositionTrackerTest {
    @Test
    fun `farming tool names match whole words only`() {
        assertTrue(PlayerPositionTracker.isFarmingToolName("Melon Dicer 3.0"))
        assertTrue(PlayerPositionTracker.isFarmingToolName("Euclid's Wheat HOE Tier 2"))
        assertTrue(PlayerPositionTracker.isFarmingToolName("Jungle Axe"))
        assertFalse(PlayerPositionTracker.isFarmingToolName("Diamond Pickaxe"))
        assertFalse(PlayerPositionTracker.isFarmingToolName("Shoes"))
    }
}
