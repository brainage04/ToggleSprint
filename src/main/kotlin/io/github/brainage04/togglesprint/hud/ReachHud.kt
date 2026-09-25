package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.brainage04.togglesprint.util.MathUtils.roundDecimalPlaces
import net.minecraft.client.Minecraft
import net.minecraft.util.MovingObjectPosition

object ReachHud {
    private var cachedLines = arrayListOf<String>()
    private var attackWasDown = false
    private var wasClickMode = false

    fun lines(): List<String> {
        val config = ConfigUtils.brainageHudParity.reach
        val minecraft = Minecraft.getMinecraft()
        return if (!config.updateOnAttackClick) {
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
                if (config.showName) lines.add("${block.localizedName}")
                if (config.showCoordinates) lines.add("${pos.x}, ${pos.y}, ${pos.z}")
            }
            MovingObjectPosition.MovingObjectType.ENTITY -> {
                val entity = hit.entityHit ?: return lines
                if (config.showName) lines.add("${entity.name}")
                if (config.showCoordinates) lines.add("${entity.position.x}, ${entity.position.y}, ${entity.position.z}")
            }
            else -> return lines
        }
        val distance = player.getPositionEyes(1.0f).distanceTo(hit.hitVec)
        lines.add("${roundDecimalPlaces(distance, config.decimalPlaces)} blocks")
        return lines
    }
}
