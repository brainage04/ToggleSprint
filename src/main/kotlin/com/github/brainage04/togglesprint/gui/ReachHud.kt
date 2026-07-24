package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.util.MovingObjectPosition
import java.util.Locale

object ReachHud {
    private var cachedLines = arrayListOf<String>()
    private var attackWasDown = false
    private var wasClickMode = false

    fun reachHud() {
        val config = ConfigUtils.brainageHudParity.reach
        if (!config.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft()
        val lines = if (!config.updateOnAttackClick) {
            wasClickMode = false
            attackWasDown = false
            calculateLines(minecraft)
        } else {
            val attackDown = minecraft.gameSettings.keyBindAttack.isKeyDown
            if (!wasClickMode || (attackDown && !attackWasDown)) cachedLines = calculateLines(minecraft)
            attackWasDown = attackDown
            wasClickMode = true
            cachedLines
        }
        if (lines.isNotEmpty()) RenderGuiData.renderElement(config.coreSettings.x, config.coreSettings.y, config.coreSettings.anchorCorner, lines)
    }

    private fun calculateLines(minecraft: Minecraft): ArrayList<String> {
        val player = minecraft.thePlayer ?: return arrayListOf()
        val world = minecraft.theWorld ?: return arrayListOf()
        val hit = minecraft.objectMouseOver ?: return arrayListOf()
        if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS || hit.hitVec == null) return arrayListOf()

        val config = ConfigUtils.brainageHudParity.reach
        val lines = arrayListOf<String>()
        when (hit.typeOfHit) {
            MovingObjectPosition.MovingObjectType.BLOCK -> {
                val pos = hit.blockPos ?: return lines
                val block = world.getBlockState(pos).block
                if (config.showName) lines.add("${ConfigUtils.primaryChars}${block.localizedName}")
                if (config.showCoordinates) lines.add("${ConfigUtils.primaryChars}${pos.x}, ${pos.y}, ${pos.z}")
            }
            MovingObjectPosition.MovingObjectType.ENTITY -> {
                val entity = hit.entityHit ?: return lines
                if (config.showName) lines.add("${ConfigUtils.primaryChars}${entity.name}")
                if (config.showCoordinates) lines.add("${ConfigUtils.primaryChars}${entity.position.x}, ${entity.position.y}, ${entity.position.z}")
            }
            else -> return lines
        }
        val decimals = Math.max(0, Math.min(10, config.decimalPlaces))
        val distance = player.getPositionEyes(1.0f).distanceTo(hit.hitVec)
        lines.add("${ConfigUtils.primaryChars}${String.format(Locale.US, "%.${decimals}f", distance)} blocks")
        return lines
    }
}
