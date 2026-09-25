package io.github.brainage04.togglesprint.config.manager

import io.github.brainage04.togglesprint.ToggleSprintMain.Companion.LOGGER
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraftforge.fml.common.eventhandler.Event

object ConfigUpdaterMigrator {
    const val CONFIG_VERSION = 5

    /** RGB of the version 3 "Primary Colour" dropdown entries, in dropdown order (the 16 § colours). */
    private val LEGACY_PRIMARY_COLOURS = intArrayOf(
        0xAA0000, 0xFF5555, 0xFFAA00, 0xFFFF55, 0x00AA00, 0x55FF55, 0x55FFFF, 0x00AAAA,
        0x0000AA, 0x5555FF, 0xFF55FF, 0xAA00AA, 0xFFFFFF, 0xAAAAAA, 0x555555, 0x000000,
    )

    /** The MoulConfig colour string ("chroma speed:alpha:red:green:blue") for a legacy dropdown index. */
    fun legacyColourToChroma(index: Int): String {
        val rgb = LEGACY_PRIMARY_COLOURS.getOrElse(index) { 0xFFFFFF }
        return "0:255:${rgb shr 16 and 0xFF}:${rgb shr 8 and 0xFF}:${rgb and 0xFF}"
    }

    fun JsonElement.at(chain: List<String>, init: Boolean): JsonElement? {
        if (chain.isEmpty()) return this
        if (this !is JsonObject) return null
        var obj = this[chain.first()]
        if (obj == null && init) {
            obj = JsonObject()
            this.add(chain.first(), obj)
        }
        return obj?.at(chain.drop(1), init)
    }

    data class ConfigFixEvent(
        val old: JsonObject,
        val new: JsonObject,
        val oldVersion: Int,
        var movesPerformed: Int,
        val dynamicPrefix: Map<String, List<String>>,
    ) : Event() {
        init {
            dynamicPrefix.entries.filter { it.value.isEmpty() }.forEach {
                LOGGER.info("Dynamic prefix ${it.key} does not resolve to anything.")
            }
        }

        /** Paths deleted from the config once this step's moves are merged in. */
        val removals = mutableListOf<String>()

        fun transform(since: Int, path: String, transform: (JsonElement) -> JsonElement = { it }) {
            move(since, path, path, transform)
        }

        fun remove(since: Int, path: String) {
            if (since == oldVersion + 1) removals.add(path)
        }

        fun move(since: Int, oldPath: String, newPath: String, transform: (JsonElement) -> JsonElement = { it }) {
            if (since <= oldVersion) {
                LOGGER.info("Skipping move from $oldPath to $newPath ($since <= $oldVersion)")
                return
            }
            if (since > CONFIG_VERSION) {
                error("Illegally new version $since > $CONFIG_VERSION")
            }
            if (since > oldVersion + 1) {
                LOGGER.info("Skipping move from $oldPath to $newPath (will be done in another pass)")
                return
            }
            val op = oldPath.split(".")
            val np = newPath.split(".")
            if (op.first().startsWith("#")) {
                require(np.first() == op.first())
                val realPrefixes = dynamicPrefix[op.first()]
                if (realPrefixes == null) {
                    LOGGER.info("Could not resolve dynamic prefix $oldPath")
                    return
                }
                for (realPrefix in realPrefixes) {
                    move(
                        since,
                        "$realPrefix.${oldPath.substringAfter('.')}",
                        "$realPrefix.${newPath.substringAfter('.')}", transform
                    )
                    return
                }
            }
            val oldElem = old.at(op, false)
            if (oldElem == null) {
                LOGGER.info("Skipping move from $oldPath to $newPath ($oldPath not present)")
                return
            }
            val newParentElement = new.at(np.dropLast(1), true)
            if (newParentElement !is JsonObject) {
                LOGGER.info("Catastrophic: element at path $old could not be relocated to $new, since another element already inhabits that path")
                return
            }
            movesPerformed++
            newParentElement.add(np.last(), transform(oldElem))
            LOGGER.info("Moved element from $oldPath to $newPath")
        }
    }

    private fun merge(originalObject: JsonObject, overrideObject: JsonObject): Int {
        var count = 0
        overrideObject.entrySet().forEach {
            val element = originalObject[it.key]
            val newElement = it.value
            if (element is JsonObject && newElement is JsonObject) {
                count += merge(element, newElement)
            } else {
                if (element != null) {
                    LOGGER.info("Encountered destructive merge. Erasing $element in favour of $newElement.")
                    count++
                }
                originalObject.add(it.key, newElement)
            }
        }
        return count
    }

    private val JsonPrimitive.asIntOrNull get() = takeIf { it.isNumber }?.asInt

    private fun JsonObject.booleanAt(path: String): Boolean? =
        (at(path.split("."), false) as? JsonPrimitive)?.takeIf { it.isBoolean }?.asBoolean

    /** Decimal places above the new maximum of 6 become 6. */
    private fun clampDecimals(value: JsonElement): JsonElement {
        val places = (value as? JsonPrimitive)?.asIntOrNull ?: return value
        return JsonPrimitive(places.coerceIn(0, 6))
    }

    /**
     * Version 5 matches BrainageHUD: the Rotation element joins the Position HUD, the Ping and TPS
     * elements become the Network HUD (at the Ping element's position), and options are renamed to
     * BrainageHUD's names. Elements keep the user's positions.
     */
    private fun migrateToVersion5(migration: ConfigFixEvent) {
        val old = migration.old
        val position = "guiElements.positionTracker"
        val rotation = "guiElements.rotationTracker"
        val rotationEnabled = old.booleanAt("$rotation.coreSettings.isEnabled") == true
        migration.move(5, "$position.decimals", "$position.positionDecimalPlaces", ::clampDecimals)
        // the rotation is shown after the direction, so a shown rotation needs the direction too
        migration.move(5, "$position.showFacing", "$position.showDirection") { JsonPrimitive(it.asBoolean || rotationEnabled) }
        migration.move(5, "$position.showChunkCounter", "$position.cCounter")
        migration.move(5, "$position.showEntityCounter", "$position.eCounter")
        migration.move(5, "$position.showLightLevels", "$position.showLight")
        migration.move(5, "$rotation.coreSettings.isEnabled", "$position.showRotation")
        migration.move(5, "$rotation.decimals", "$position.rotationDecimalPlaces", ::clampDecimals)
        migration.move(5, "$rotation.showTrueYaw", "$position.showTrueYaw")
        migration.move(5, "$rotation.dependOnFarmingTool", "$position.rotationOnlyWithFarmingTool")

        val motion = "guiElements.motionTracker"
        migration.move(5, "$motion.decimals", "$motion.decimalPlaces", ::clampDecimals)
        migration.move(5, "$motion.showTrueMotion", "$motion.showBlocksPerTick")

        // the time format dropdown was 12-hour, 24-hour or None
        val dateTime = "guiElements.realTimeTracker"
        migration.move(5, "$dateTime.timeFormat", "$dateTime.showTime") { JsonPrimitive(it.asInt != 2) }
        migration.move(5, "$dateTime.timeFormat", "$dateTime.twelveHourFormat") { JsonPrimitive(it.asInt != 1) }
        migration.move(5, "$dateTime.includeDate", "$dateTime.showDate")
        migration.move(5, "$dateTime.includeTimezone", "$dateTime.showTimezone")

        val ping = "guiElements.pingTracker"
        val tps = "guiElements.tpsTracker"
        val network = "guiElements.networkTracker"
        val pingEnabled = old.booleanAt("$ping.coreSettings.isEnabled") == true
        val tpsEnabled = old.booleanAt("$tps.coreSettings.isEnabled") == true
        // with both elements hidden, the Network HUD is hidden but shows both lines once turned on
        migration.move(5, "$ping.coreSettings.isEnabled", "$network.showPing") { JsonPrimitive(pingEnabled || !tpsEnabled) }
        migration.move(5, "$tps.coreSettings.isEnabled", "$network.showTps") { JsonPrimitive(tpsEnabled || !pingEnabled) }
        // the Ping element's settings are removed below, so they are reused rather than copied
        migration.move(5, "$ping.coreSettings", "$network.coreSettings") { coreSettings ->
            coreSettings.also { (it as? JsonObject)?.addProperty("isEnabled", pingEnabled || tpsEnabled) }
        }
        migration.move(5, "$ping.showColor", "$network.colourValues")
        migration.move(5, "$tps.intervalsTracked", "$network.tpsIntervalsTracked")

        migration.move(5, "guiElements.entityTracker.showAmbients", "guiElements.entityTracker.showAmbient")

        val performance = "brainageHudParity.performance"
        // the old GPU option showed the frame time; GPU usage is now a percentage of its own
        migration.move(5, "$performance.showGpuUsage", "$performance.showGpuFrameTime")
        migration.transform(5, "$performance.showGpuUsage") { JsonPrimitive(false) }
        migration.transform(5, "brainageHudParity.reach.decimalPlaces", ::clampDecimals)
        migration.transform(5, "brainageHudParity.enchantInfo.blacklistedEnchantmentIds") { ids ->
            if (ids is JsonArray) JsonPrimitive(ids.joinToString(", ") { it.asString }) else ids
        }

        val equipment = "inventoryTrackers.equipmentTracker"
        migration.move(5, "$equipment.displayDurabilityBar", "$equipment.showDurabilityBar")
        migration.move(5, "$equipment.decimals", "$equipment.durabilityDecimalPlaces", ::clampDecimals)

        // the item types list held the indices of arrows, snowballs, eggs and ender pearls
        val projectile = "inventoryTrackers.projectileTracker"
        migration.move(5, "$projectile.includeArrays", "$projectile.showSlotCounts")
        listOf("showArrows", "showSnowballs", "showEggs", "showEnderPearls").forEachIndexed { index, option ->
            migration.move(5, "$projectile.itemTypes", "$projectile.$option") { types ->
                JsonPrimitive(types is JsonArray && types.any { it.asInt == index })
            }
        }
        val food = "inventoryTrackers.foodTracker"
        migration.move(5, "$food.includeArrays", "$food.showSlotCounts")

        for (path in listOf(
            rotation, ping, tps,
            "$position.decimals", "$position.showFacing", "$position.showChunkCounter", "$position.showEntityCounter", "$position.showLightLevels",
            "$motion.decimals", "$motion.showTrueMotion",
            "$dateTime.timeFormat", "$dateTime.includeDate", "$dateTime.includeTimezone",
            "guiElements.entityTracker.showAmbients",
            "$equipment.displayDurabilityBar", "$equipment.decimals",
            "$projectile.includeArrays", "$projectile.prefixFormat", "$projectile.itemTypes",
            "$food.includeArrays", "$food.prefixFormat", "$food.itemTypes",
        )) migration.remove(5, path)
    }

    fun fixConfig(config: JsonObject): JsonObject {
        val lastVersion = (config["lastVersion"] as? JsonPrimitive)?.asIntOrNull ?: -1
        if (lastVersion > CONFIG_VERSION) {
            error("Cannot downgrade config")
        }
        if (lastVersion == CONFIG_VERSION) return config
        return (lastVersion until CONFIG_VERSION).fold(config) { accumulator, i ->
            LOGGER.info("Starting config transformation from $i to ${i + 1}")
            val storage = accumulator["storage"]?.asJsonObject
            val dynamicPrefix: Map<String, List<String>> = mapOf(
                "#profile" to
                        (storage?.get("players")?.asJsonObject?.entrySet()
                            ?.flatMap { player ->
                                player.value.asJsonObject["profiles"]?.asJsonObject?.entrySet()?.map {
                                    "storage.players.${player.key}.profiles.${it.key}"
                                } ?: listOf()
                            }
                            ?: listOf()),
                "#player" to
                        (storage?.get("players")?.asJsonObject?.entrySet()?.map { "storage.players.${it.key}" }
                            ?: listOf()),
            )
            val migration = ConfigFixEvent(accumulator, JsonObject().also {
                it.add("lastVersion", JsonPrimitive(i + 1))
            }, i, 0, dynamicPrefix)
            when (i + 1) {
                2 -> {
                    migration.transform(2, "brainageHudParity.fullbright") { value ->
                        val primitive = value as? JsonPrimitive
                        if (primitive?.isBoolean == true) JsonPrimitive(if (primitive.asBoolean) 1.0f else 0.0f) else value
                    }
                    migration.move(
                        2,
                        "brainageHudParity.keystrokes.showCps",
                        "brainageHudParity.keystrokes.clicksPerSecondFormat",
                    ) { value ->
                        val primitive = value as? JsonPrimitive
                        if (primitive?.isBoolean == true) JsonPrimitive(if (primitive.asBoolean) 3 else 0) else value
                    }
                }
                3 -> migration.move(
                    3,
                    "guiElements.positionTracker.showChunkCoordinates",
                    "guiElements.positionTracker.showChunkPosition",
                )
                4 -> migration.move(
                    4,
                    "globalGuiSettings.primaryColour",
                    "globalGuiSettings.textColour",
                ) { value ->
                    val index = (value as? JsonPrimitive)?.asIntOrNull ?: -1
                    JsonPrimitive(legacyColourToChroma(index))
                }
                5 -> migrateToVersion5(migration)
            }
            LOGGER.info("Transformations scheduled: ${migration.new}")
            val mergesPerformed = merge(migration.old, migration.new)
            for (path in migration.removals) {
                val chain = path.split(".")
                (migration.old.at(chain.dropLast(1), false) as? JsonObject)?.remove(chain.last())
            }
            LOGGER.info("Migration done with $mergesPerformed merges and ${migration.movesPerformed} moves performed")
            migration.old
        }.also {
            LOGGER.info("Final config: $it")
        }
    }
}