package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.config.categories.GUIElements
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import net.minecraft.util.StatCollector

/** Lists the player's active status effects, in the order the inventory screen lists them. */
object StatusEffectHud {
    fun render(coreSettings: GUIElements.CoreSettings) {
        val player = Minecraft.getMinecraft().thePlayer ?: return
        val settings = ConfigUtils.brainageHudParity.statusEffectHud

        val lines = arrayListOf<String>()
        val potions = arrayListOf<Potion>()
        for (effect in player.activePotionEffects) {
            val potion = Potion.potionTypes.getOrNull(effect.potionID) ?: continue
            val duration = when {
                !settings.showDurations -> null
                // effects applied for the longest duration the network allows never run out
                effect.getIsPotionDurationMax() -> INFINITE
                else -> formatDuration(effect.duration)
            }
            val text = line(StatCollector.translateToLocal(potion.name), levelName(effect.amplifier), duration)
            // reserve the icon column, even for effects without an icon, so every line's text lines up
            lines.add(if (settings.showIcons) ICON_COLUMN + text else text)
            potions.add(potion)
        }

        val placedLines = RenderGuiData.drawElement(coreSettings, lines)
        if (settings.showIcons) renderIcons(potions, placedLines)
    }

    /** Draws each effect's inventory icon, scaled to the line height, over the column reserved at its line's start. */
    private fun renderIcons(potions: List<Potion>, placedLines: List<RenderGuiData.PlacedLine>) {
        val minecraft = Minecraft.getMinecraft()
        var previousSource = -1
        for (line in placedLines) {
            // a wrapped line continues its effect; only its first part carries the icon
            if (line.sourceIndex == previousSource) continue
            previousSource = line.sourceIndex
            val potion = potions.getOrNull(line.sourceIndex) ?: continue
            if (!potion.hasStatusIcon()) continue
            val index = potion.statusIconIndex
            GlStateManager.pushMatrix()
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.enableBlend()
            minecraft.textureManager.bindTexture(INVENTORY_TEXTURE)
            // the inventory's 18px icon cells are drawn at half scale to match the 9px line height
            GlStateManager.translate(line.x.toFloat(), line.y.toFloat(), 0f)
            GlStateManager.scale(ICON_SCALE, ICON_SCALE, 1f)
            minecraft.ingameGUI.drawTexturedModalRect(0, 0, index % 8 * ICON_CELL, 198 + index / 8 * ICON_CELL, ICON_CELL, ICON_CELL)
            GlStateManager.popMatrix()
        }
    }

    /** Vanilla's level name for [amplifier]: empty for level I, then II, III and so on. */
    private fun levelName(amplifier: Int): String {
        val key = "potion.potency.$amplifier"
        return if (StatCollector.canTranslate(key)) StatCollector.translateToLocal(key) else (amplifier + 1).toString()
    }

    /** `<name>[ <level>][: <duration>]`; a blank [level] (level I) and a null [duration] are left out. */
    fun line(name: String, level: String, duration: String?): String {
        val levelPart = if (level.isBlank()) "" else " $level"
        val durationPart = if (duration == null) "" else ": $duration"
        return name + levelPart + durationPart
    }

    /** [ticks] as `m:ss`, or `h:mm:ss` from an hour, rounding partial seconds down like vanilla. */
    fun formatDuration(ticks: Int): String {
        val totalSeconds = ticks.coerceAtLeast(0) / 20
        val hours = totalSeconds / 3600
        val minutes = totalSeconds / 60 % 60
        val seconds = (totalSeconds % 60).toString().padStart(2, '0')
        return if (hours > 0) "$hours:${minutes.toString().padStart(2, '0')}:$seconds" else "$minutes:$seconds"
    }

    private const val INFINITE = "Infinite"

    private val INVENTORY_TEXTURE = ResourceLocation("textures/gui/container/inventory.png")
    private const val ICON_CELL = 18

    /** Icons are one font line (9px) tall. */
    private const val ICON_SCALE = 0.5f

    /** Three 4px spaces: the 9px icon plus a 3px gap before the text. */
    private const val ICON_COLUMN = "   "
}
