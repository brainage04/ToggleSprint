package com.github.brainage04.togglesprint.events

import kotlin.test.Test
import kotlin.test.assertEquals

class FullbrightHandlerTest {
    @Test
    fun `zero ambient light reproduces vanilla endpoint brightness`() {
        val table = FloatArray(16)

        FullbrightHandler.fillLightTable(table, 0.0f)

        assertEquals(0.0f, table[0])
        assertEquals(1.0f, table[15])
        assertEquals((1.0f / 15.0f) / ((14.0f / 15.0f) * 3.0f + 1.0f), table[1], absoluteTolerance = 0.000001f)
    }

    @Test
    fun `full ambient light makes every light level fully bright`() {
        val table = FloatArray(16)

        FullbrightHandler.fillLightTable(table, 1.0f)

        table.forEach { assertEquals(1.0f, it) }
    }

    @Test
    fun `negative ambient light follows the same dimension formula`() {
        val table = FloatArray(16)

        FullbrightHandler.fillLightTable(table, -1.0f)

        assertEquals(-1.0f, table[0])
        assertEquals(1.0f, table[15])
    }
}
