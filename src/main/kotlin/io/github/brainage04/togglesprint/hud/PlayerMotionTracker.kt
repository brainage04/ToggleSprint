package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.config.categories.GUIElements
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import net.minecraft.client.Minecraft
import kotlin.math.sqrt

/**
 * How fast the camera entity is moving, from how far it moved over the last tick. Unlike its
 * motion fields, this is what actually happened after collisions, friction and vehicles.
 */
object PlayerMotionTracker {
    private const val TICKS_PER_SECOND = 20.0

    fun lines(): List<String> {
        val entity = Minecraft.getMinecraft().renderViewEntity ?: return emptyList()
        val config = ConfigUtils.guiElements.motionTracker
        val dx = entity.posX - entity.prevPosX
        val dy = entity.posY - entity.prevPosY
        val dz = entity.posZ - entity.prevPosZ
        val lines = arrayListOf<String>()

        if (config.showAxes) {
            lines.add(speed("Motion X", dx, config))
            lines.add(speed("Motion Y", dy, config))
            lines.add(speed("Motion Z", dz, config))
        }
        if (config.showHorizontalSpeed) {
            lines.add(speed("Speed", sqrt(dx * dx + dz * dz), config))
        }

        return lines
    }

    private fun speed(label: String, blocksPerTick: Double, config: GUIElements.MotionTracker): String {
        var line = "$label: ${roundDecimalPlaces(blocksPerTick * TICKS_PER_SECOND, config.decimalPlaces)} m/s"
        if (config.showBlocksPerTick) line += " (${roundDecimalPlaces(blocksPerTick, config.decimalPlaces)} m/tick)"
        return line
    }
}
