package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.world.EnumSkyBlock

object PlayerPositionTracker {
    // Minecraft yaw 0 faces south (+Z) and increases clockwise: 90 is west, 180/-180 north, -90 east
    private val YAW_LABEL = arrayOf(
        "S (+Z)",          // 0 : centred on 0
        "SW (-X, +Z)",     // 1 : centred on 45
        "W (-X)",          // 2 : centred on 90
        "NW (-X, -Z)",     // 3 : centred on 135
        "N (-Z)",          // 4 : centred on 180 / -180
        "NE (+X, -Z)",     // 5 : centred on 225 / -135
        "E (+X)",          // 6 : centred on 270 / -90
        "SE (+X, +Z)",     // 7 : centred on 315 / -45
    )

    /** Words in the names of Hypixel SkyBlock's farming tools, which are not vanilla axes or hoes. */
    private val FARMING_TOOL_NAME = Regex("\\b(axe|hoe|chopper|dicer|cutter|knife)\\b", RegexOption.IGNORE_CASE)

    fun yawLabel(yaw: Float): String {
        // shift by half a sector so each label's range starts at a multiple of 45, then wrap to [0, 360)
        val wrapped = ((yaw + 22.5f) % 360.0f + 360.0f) % 360.0f
        return YAW_LABEL[(wrapped / 45.0f).toInt()]
    }

    /** Whole words only, so that a pickaxe does not count as an axe. */
    fun isFarmingToolName(name: String): Boolean = FARMING_TOOL_NAME.containsMatchIn(name)

    /** Vanilla axes and hoes, and items named as farming tools (like SkyBlock's Melon Dicer). */
    private fun isFarmingTool(stack: ItemStack?): Boolean {
        if (stack == null) return false
        if (stack.item is ItemAxe || stack.item is ItemHoe) return true
        return isFarmingToolName(stack.displayName)
    }

    fun lines(): List<String> {
        val minecraft = Minecraft.getMinecraft()
        val player = minecraft.thePlayer ?: return emptyList()
        val config = ConfigUtils.guiElements.positionTracker
        val blockPos = BlockPos(player.posX, player.posY, player.posZ)
        val lines = arrayListOf<String>()

        if (config.showPosition) {
            var x = "X: ${roundDecimalPlaces(player.posX, config.positionDecimalPlaces)}"
            var y = "Y: ${roundDecimalPlaces(player.posY, config.positionDecimalPlaces)}"
            var z = "Z: ${roundDecimalPlaces(player.posZ, config.positionDecimalPlaces)}"

            if (config.showChunkPosition) {
                x += " [${blockPos.x and 15}]"
                y += " [${blockPos.y and 15}]"
                z += " [${blockPos.z and 15}]"
            }

            lines.add(x)
            lines.add(y)
            lines.add(z)
        }

        if (config.cCounter) {
            // "C: rendered/total (s) D: ...": the C counter is the part before the D counter
            lines.add(minecraft.renderGlobal.debugInfoRenders.substringBefore(" D:").trim())
        }

        if (config.eCounter) {
            lines.add(minecraft.renderGlobal.debugInfoEntities)
        }

        if (config.showDirection) {
            val entity = minecraft.renderViewEntity ?: return lines
            val yaw = MathHelper.wrapAngleTo180_float(entity.rotationYaw)
            val pitch = MathHelper.wrapAngleTo180_float(entity.rotationPitch)
            var direction = yawLabel(yaw)

            if (config.showRotation && (!config.rotationOnlyWithFarmingTool || isFarmingTool(player.heldItem))) {
                direction += " (${roundDecimalPlaces(yaw.toDouble(), config.rotationDecimalPlaces)} / " +
                    "${roundDecimalPlaces(pitch.toDouble(), config.rotationDecimalPlaces)})"
                // the stored yaw keeps winding past ±180 as the player turns
                val trueYaw = entity.rotationYaw
                if (config.showTrueYaw && (trueYaw < -180.0f || trueYaw >= 180.0f)) {
                    direction += " [${roundDecimalPlaces(trueYaw.toDouble(), config.rotationDecimalPlaces)}]"
                }
            }

            lines.add(direction)
        }

        if (config.showLight) {
            val world = player.worldObj
            lines.add("Light: ${world.getLightFor(EnumSkyBlock.SKY, blockPos)} sky, ${world.getLightFor(EnumSkyBlock.BLOCK, blockPos)} block")
        }

        if (config.showBiome) {
            lines.add("Biome: ${player.worldObj.getBiomeGenForCoords(blockPos).biomeName}")
        }

        return lines
    }
}
