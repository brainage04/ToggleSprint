package io.github.brainage04.togglesprint.hud

import io.github.brainage04.togglesprint.config.categories.GUIElements
import io.github.brainage04.togglesprint.hud.core.ElementPlacement
import io.github.brainage04.togglesprint.hud.core.RenderGuiData
import io.github.brainage04.togglesprint.util.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
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
        val entries = arrayListOf<Pair<Potion, List<String>>>()
        for (effect in player.activePotionEffects) {
            val potion = Potion.potionTypes.getOrNull(effect.potionID) ?: continue
            val duration = when {
                !settings.showDurations -> null
                // effects applied for the longest duration the network allows never run out
                effect.getIsPotionDurationMax() -> INFINITE
                else -> formatDuration(effect.duration)
            }
            val name = StatCollector.translateToLocal(potion.name)
            val level = levelName(effect.amplifier)
            if (settings.showIcons) {
                val nameLine = line(name, level, null)
                entries.add(potion to if (duration == null) listOf(nameLine) else listOf(nameLine, duration))
            } else {
                lines.add(line(name, level, duration))
            }
        }

        if (settings.showIcons) renderEntries(coreSettings, entries) else RenderGuiData.renderElement(coreSettings, lines)
    }

    /** One effect in the icon layout: its text lines (wrapped to the width beside the icon) and their widths. */
    private class Entry(val potion: Potion, val lines: List<String>, val widths: IntArray) {
        val width = ICON_SIZE + ICON_GAP + (widths.maxOrNull() ?: 0)

        /** Two unwrapped text lines are exactly the icon's height; more lines grow the entry below it. */
        val height = maxOf(ICON_SIZE, lines.size * LINE_HEIGHT)
    }

    /**
     * Draws each effect as an 18px inventory icon with its text lines to the right: name and level on the
     * first line, duration on the second, the pair exactly as tall as the icon. A single line is centred on
     * the icon. The element's padding separates effects, not the lines within one.
     */
    private fun renderEntries(coreSettings: GUIElements.CoreSettings, effects: List<Pair<Potion, List<String>>>) {
        if (effects.isEmpty()) return
        val minecraft = Minecraft.getMinecraft()
        val font = minecraft.fontRendererObj ?: return

        val padding = RenderGuiData.padding(coreSettings)
        val maxWidth = RenderGuiData.maxWidth(coreSettings)
        val textWidth = if (maxWidth > 0) (maxWidth - ICON_SIZE - ICON_GAP).coerceAtLeast(1) else 0
        val entries = effects.map { (potion, lines) ->
            val wrapped = lines.flatMap { wrap(font, it, textWidth) }
            Entry(potion, wrapped, IntArray(wrapped.size) { font.getStringWidth(wrapped[it]) })
        }

        val contentWidth = entries.maxOf { it.width }
        val contentHeight = entries.sumOf { it.height } + padding * (entries.size - 1)
        val inset = padding * 2
        val bounds = RenderGuiData.placeElement(coreSettings, contentWidth + inset * 2, contentHeight + inset * 2)

        val opacity = RenderGuiData.backdropOpacity(coreSettings)
        if (opacity > 0) Gui.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, opacity shl 24)

        val colour = RenderGuiData.textColour(coreSettings)
        val shadows = RenderGuiData.textShadows(coreSettings)
        val axis = ElementPlacement.horizontalAxis(coreSettings.anchorCorner)
        var y = bounds.top + inset
        for (entry in entries) {
            // entries hug the anchored side of the element; within one, the text sits beside the icon
            val x = when (axis) {
                ElementPlacement.Axis.START -> bounds.left + inset
                ElementPlacement.Axis.END -> bounds.right - inset - entry.width
                ElementPlacement.Axis.CENTRE -> bounds.left + inset + (contentWidth - entry.width) / 2
            }
            if (entry.potion.hasStatusIcon()) {
                val index = entry.potion.statusIconIndex
                GlStateManager.color(1f, 1f, 1f, 1f)
                GlStateManager.enableBlend()
                minecraft.textureManager.bindTexture(INVENTORY_TEXTURE)
                minecraft.ingameGUI.drawTexturedModalRect(x, y, index % 8 * ICON_SIZE, 198 + index / 8 * ICON_SIZE, ICON_SIZE, ICON_SIZE)
            }
            val textX = x + ICON_SIZE + ICON_GAP
            val textY = if (entry.lines.size == 1) y + SINGLE_LINE_OFFSET else y
            for (i in entry.lines.indices) {
                font.drawString(entry.lines[i], textX.toFloat(), (textY + LINE_HEIGHT * i).toFloat(), colour, shadows)
            }
            y += entry.height + padding
        }
    }

    /** [text] split to [width] like [RenderGuiData.drawElement] wraps lines; a [width] of 0 leaves it whole. */
    private fun wrap(font: FontRenderer, text: String, width: Int): List<String> {
        val parts = if (width > 0) font.listFormattedStringToWidth(text, width) else null
        return if (parts.isNullOrEmpty()) listOf(text) else parts
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

    /** The inventory's status icon cells, drawn at full size: two text lines tall. */
    private const val ICON_SIZE = 18
    private const val ICON_GAP = 2
    private const val LINE_HEIGHT = 9

    /**
     * A lone line's offset below the icon's top that centres it on the icon: capitals fill glyph rows 0-6
     * and their shadow row 7, so rows 5-12 share the icon's 0-17 centre.
     */
    private const val SINGLE_LINE_OFFSET = 5
}
