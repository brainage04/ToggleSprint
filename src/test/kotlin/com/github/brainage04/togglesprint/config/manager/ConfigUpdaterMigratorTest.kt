package com.github.brainage04.togglesprint.config.manager

import com.google.gson.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigUpdaterMigratorTest {
    @Test
    fun `version one parity controls migrate to amount and CPS format`() {
        val keystrokes = JsonObject().apply { addProperty("showCps", true) }
        val parity = JsonObject().apply {
            addProperty("fullbright", true)
            add("keystrokes", keystrokes)
        }
        val config = JsonObject().apply {
            addProperty("lastVersion", 1)
            add("brainageHudParity", parity)
        }

        val migrated = ConfigUpdaterMigrator.fixConfig(config)
        val migratedParity = migrated.getAsJsonObject("brainageHudParity")
        val fullbright = migratedParity.getAsJsonPrimitive("fullbright")
        val cpsFormat = migratedParity.getAsJsonObject("keystrokes").getAsJsonPrimitive("clicksPerSecondFormat")

        assertEquals(ConfigUpdaterMigrator.CONFIG_VERSION, migrated.get("lastVersion").asInt)
        assertTrue(fullbright.isNumber)
        assertEquals(1.0f, fullbright.asFloat)
        assertEquals(3, cpsFormat.asInt)
    }

    @Test
    fun `version two chunk coordinate toggle migrates to chunk position`() {
        val position = JsonObject().apply { addProperty("showChunkCoordinates", true) }
        val guiElements = JsonObject().apply { add("positionTracker", position) }
        val config = JsonObject().apply {
            addProperty("lastVersion", 2)
            add("guiElements", guiElements)
        }

        val migrated = ConfigUpdaterMigrator.fixConfig(config)
        val migratedPosition = migrated.getAsJsonObject("guiElements").getAsJsonObject("positionTracker")

        assertEquals(ConfigUpdaterMigrator.CONFIG_VERSION, migrated.get("lastVersion").asInt)
        assertTrue(migratedPosition.get("showChunkPosition").asBoolean)
    }

    @Test
    fun `version three primary colour dropdown migrates to the same RGB text colour`() {
        val globalGuiSettings = JsonObject().apply { addProperty("primaryColour", 6) } // Aqua, §b
        val config = JsonObject().apply {
            addProperty("lastVersion", 3)
            add("globalGuiSettings", globalGuiSettings)
        }

        val migrated = ConfigUpdaterMigrator.fixConfig(config)

        assertEquals("0:255:85:255:255", migrated.getAsJsonObject("globalGuiSettings").get("textColour").asString)
    }
}
