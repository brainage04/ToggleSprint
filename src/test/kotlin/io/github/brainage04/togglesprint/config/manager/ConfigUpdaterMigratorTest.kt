package io.github.brainage04.togglesprint.config.manager

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.brainage04.togglesprint.config.ToggleSprintConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

    @Test
    fun `version four elements merge into the Network and Position HUDs with renamed options`() {
        val migrated = ConfigUpdaterMigrator.fixConfig(JsonParser().parse(VERSION_FOUR_CONFIG).asJsonObject)
        val guiElements = migrated.getAsJsonObject("guiElements")
        val position = guiElements.getAsJsonObject("positionTracker")
        val network = guiElements.getAsJsonObject("networkTracker")
        val dateTime = guiElements.getAsJsonObject("realTimeTracker")
        val parity = migrated.getAsJsonObject("brainageHudParity")
        val projectile = migrated.getAsJsonObject("inventoryTrackers").getAsJsonObject("projectileTracker")

        assertEquals(5, migrated.get("lastVersion").asInt)

        // the rotation element's settings join the position HUD; the direction carries the rotation
        assertEquals(6, position.get("positionDecimalPlaces").asInt)
        assertTrue(position.get("showDirection").asBoolean)
        assertTrue(position.get("showRotation").asBoolean)
        assertEquals(3, position.get("rotationDecimalPlaces").asInt)
        assertTrue(position.get("rotationOnlyWithFarmingTool").asBoolean)
        assertFalse(position.get("cCounter").asBoolean)
        assertEquals(12.0, position.getAsJsonObject("coreSettings").get("x").asDouble)

        // the network HUD takes the ping element's position and shows TPS since that element was on
        val networkCore = network.getAsJsonObject("coreSettings")
        assertEquals(40.0, networkCore.get("y").asDouble)
        assertEquals(2, networkCore.get("anchorCorner").asInt)
        assertTrue(networkCore.get("isEnabled").asBoolean)
        assertFalse(network.get("showPing").asBoolean)
        assertTrue(network.get("showTps").asBoolean)
        assertFalse(network.get("colourValues").asBoolean)
        assertEquals(7, network.get("tpsIntervalsTracked").asInt)

        // "24-hour" in the old dropdown
        assertTrue(dateTime.get("showTime").asBoolean)
        assertFalse(dateTime.get("twelveHourFormat").asBoolean)
        assertFalse(dateTime.get("showDate").asBoolean)

        assertTrue(parity.getAsJsonObject("performance").get("showGpuFrameTime").asBoolean)
        assertFalse(parity.getAsJsonObject("performance").get("showGpuUsage").asBoolean)
        assertEquals("minecraft:smite, minecraft:knockback", parity.getAsJsonObject("enchantInfo").get("blacklistedEnchantmentIds").asString)

        assertTrue(projectile.get("showArrows").asBoolean)
        assertFalse(projectile.get("showSnowballs").asBoolean)
        assertFalse(projectile.get("showEggs").asBoolean)
        assertTrue(projectile.get("showEnderPearls").asBoolean)
        assertTrue(projectile.get("showSlotCounts").asBoolean)

        for (removed in listOf("rotationTracker", "pingTracker", "tpsTracker")) assertNull(guiElements.get(removed))
        assertNull(position.get("decimals"))
        assertNull(dateTime.get("timeFormat"))
        assertNull(projectile.get("itemTypes"))

        // options the old config lacks keep their defaults once loaded
        val config = ConfigManager.gson.fromJson(migrated, ToggleSprintConfig::class.java)
        assertTrue(config.guiElements.positionTracker.showPosition)
        assertEquals(10, config.guiElements.networkTracker.updatePingTickInterval)
        assertEquals(40.0, config.guiElements.networkTracker.coreSettings.y)
        assertTrue(config.guiElements.motionTracker.showAxes)
    }

    @Test
    fun `version four with ping and TPS both hidden keeps the Network HUD hidden with both lines`() {
        val config = JsonParser().parse(VERSION_FOUR_CONFIG).asJsonObject
        val guiElements = config.getAsJsonObject("guiElements")
        guiElements.getAsJsonObject("tpsTracker").getAsJsonObject("coreSettings").addProperty("isEnabled", false)

        val network = ConfigUpdaterMigrator.fixConfig(config).getAsJsonObject("guiElements").getAsJsonObject("networkTracker")

        assertFalse(network.getAsJsonObject("coreSettings").get("isEnabled").asBoolean)
        assertTrue(network.get("showPing").asBoolean)
        assertTrue(network.get("showTps").asBoolean)
    }

    private companion object {
        /** A version 4 config as saved by 1.2.3, with some options changed from their defaults. */
        const val VERSION_FOUR_CONFIG = """
        {
          "toggleMovementKeys": {
            "toggleSprint": { "isEnabled": true, "defaultState": true },
            "toggleSneak": { "isEnabled": true, "defaultState": false }
          },
          "globalGuiSettings": {
            "textColour": "0:255:255:255:255", "textShadows": true, "backdropOpacity": 0,
            "paddingInPixels": 2, "maxElementWidth": 0, "screenMargin": 5
          },
          "guiElements": {
            "toggleSprintElement": { "coreSettings": { "isEnabled": true, "x": 5.0, "y": 3.0, "anchorCorner": 2 }, "showInternalValues": false },
            "positionTracker": {
              "coreSettings": { "isEnabled": true, "x": 12.0, "y": 10.0, "anchorCorner": 0 },
              "decimals": 8, "showFacing": false, "showChunkCounter": false, "showEntityCounter": true,
              "showChunkPosition": true, "showLightLevels": true, "showBiome": true
            },
            "rotationTracker": {
              "coreSettings": { "isEnabled": true, "x": 10.0, "y": 120.0, "anchorCorner": 0 },
              "decimals": 3, "compactFormat": true, "showTrueYaw": true, "dependOnFarmingTool": true
            },
            "motionTracker": { "coreSettings": { "isEnabled": false, "x": 80.0, "y": 10.0, "anchorCorner": 0 }, "decimals": 2, "showTrueMotion": true },
            "realTimeTracker": {
              "coreSettings": { "isEnabled": true, "x": 10.0, "y": 10.0, "anchorCorner": 1 },
              "timeFormat": 1, "includeDate": false, "includeTimezone": true
            },
            "pingTracker": { "coreSettings": { "isEnabled": false, "x": 10.0, "y": 40.0, "anchorCorner": 2 }, "showColor": false },
            "tpsTracker": { "coreSettings": { "isEnabled": true, "x": 10.0, "y": 100.0, "anchorCorner": 0 }, "showColor": true, "intervalsTracked": 7 },
            "entityTracker": {
              "coreSettings": { "isEnabled": false, "x": 130.0, "y": 10.0, "anchorCorner": 1 },
              "showCreatures": true, "showWaterCreatures": true, "showAmbients": false, "showMonsters": true, "showOthers": true
            }
          },
          "inventoryTrackers": {
            "equipmentTracker": {
              "coreSettings": { "isEnabled": true, "x": 10.0, "y": 10.0, "anchorCorner": 3 },
              "prefixFormat": 1, "displayDurabilityBar": true, "durabilityFormat": 0, "decimals": 1, "itemTypes": [0, 1, 2, 3, 4]
            },
            "projectileTracker": {
              "coreSettings": { "isEnabled": true, "x": 10.0, "y": 50.0, "anchorCorner": 1 },
              "prefixFormat": 1, "includeArrays": true, "itemTypes": [3, 0]
            },
            "foodTracker": {
              "coreSettings": { "isEnabled": true, "x": 10.0, "y": 80.0, "anchorCorner": 3 },
              "prefixFormat": 1, "includeArrays": true, "itemTypes": [0, 1, 2]
            }
          },
          "brainageHudParity": {
            "fishing": { "coreSettings": { "isEnabled": false, "x": 0.0, "y": 60.0, "anchorCorner": 8 } },
            "performance": {
              "coreSettings": { "isEnabled": true, "x": 5.0, "y": 99.0, "anchorCorner": 0 },
              "showFps": true, "showRamUsage": false, "showCpuUsage": false, "showGpuUsage": true
            },
            "reach": { "coreSettings": { "isEnabled": true, "x": 0.0, "y": 30.0, "anchorCorner": 8 }, "decimalPlaces": 2 },
            "fullbright": 0.0,
            "waypoints": { "showInWorld": true, "showWorldCentre": true, "labelScalePercent": 100, "entries": [] },
            "enchantInfo": { "highlightMaxLevelEnchants": true, "blacklistedEnchantmentIds": ["minecraft:smite", "minecraft:knockback"] }
          },
          "lastVersion": 4
        }
        """
    }
}
