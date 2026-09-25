package io.github.brainage04.togglesprint.hud.core

import io.github.brainage04.togglesprint.config.categories.GUIElements
import io.github.brainage04.togglesprint.hud.EnchantInfoHud
import io.github.brainage04.togglesprint.hud.EntityTracker
import io.github.brainage04.togglesprint.hud.FishingHud
import io.github.brainage04.togglesprint.hud.KeystrokesHud
import io.github.brainage04.togglesprint.hud.PerformanceHud
import io.github.brainage04.togglesprint.hud.NetworkTracker
import io.github.brainage04.togglesprint.hud.PlayerMotionTracker
import io.github.brainage04.togglesprint.hud.PlayerPositionTracker
import io.github.brainage04.togglesprint.hud.ReachHud
import io.github.brainage04.togglesprint.hud.RealTimeTracker
import io.github.brainage04.togglesprint.hud.StatusEffectHud
import io.github.brainage04.togglesprint.hud.ToggleSprintTracker
import io.github.brainage04.togglesprint.hud.inventory_trackers.EquipmentTracker
import io.github.brainage04.togglesprint.hud.inventory_trackers.FoodTracker
import io.github.brainage04.togglesprint.hud.inventory_trackers.ProjectileTracker
import io.github.brainage04.togglesprint.util.ConfigUtils
import io.github.moulberry.moulconfig.ChromaColour
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.IdentityHashMap

class RenderGuiData {
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        placedBounds.clear()
        val editing = Minecraft.getMinecraft().currentScreen is HudElementEditor

        GlStateManager.pushMatrix()
        for (element in elements) {
            val coreSettings = element.coreSettings
            if (coreSettings.isEnabled) element.render(coreSettings, editing)
        }
        GlStateManager.popMatrix()
    }

    /** The screen rectangle an element occupied on the last frame, backdrop and padding included. */
    data class ElementBounds(val left: Int, val top: Int, val right: Int, val bottom: Int) {
        val width get() = right - left
        val height get() = bottom - top

        fun contains(x: Int, y: Int) = x in left until right && y in top until bottom
    }

    /** Where [RenderGuiData.renderElement] drew one (possibly wrapped) line of an element. */
    data class PlacedLine(val text: String, val sourceIndex: Int, val width: Int, val x: Int, val y: Int)

    companion object {
        /**
         * Every HUD element, drawn in this order each frame; this is also the list the element editor moves.
         *
         * To add a text element, append one entry:
         * `HudElement("Name", { ConfigUtils.<category>.<element>.coreSettings }) { MyHud.lines() }`,
         * where `lines()` returns plain text (inline § codes only for per-value colours, `§l` for headers) and
         * an empty list when there is nothing to show. Do not check `isEnabled`; disabled elements are skipped.
         * Elements that draw more than text subclass [HudElement] and override [HudElement.render].
         */
        val elements: List<HudElement> = listOf(
            HudElement("Toggle Sprint HUD", { ConfigUtils.guiElements.toggleSprintElement.coreSettings }) { ToggleSprintTracker.lines() },
            HudElement("Position HUD", { ConfigUtils.guiElements.positionTracker.coreSettings }) { PlayerPositionTracker.lines() },
            HudElement("Motion HUD", { ConfigUtils.guiElements.motionTracker.coreSettings }) { PlayerMotionTracker.lines() },
            object : HudElement("Armour Info HUD", { ConfigUtils.inventoryTrackers.equipmentTracker.coreSettings }) {
                override fun render(coreSettings: GUIElements.CoreSettings, editing: Boolean) = EquipmentTracker.render(coreSettings)
            },
            HudElement("Projectile HUD", { ConfigUtils.inventoryTrackers.projectileTracker.coreSettings }) { ProjectileTracker.lines() },
            HudElement("Food HUD", { ConfigUtils.inventoryTrackers.foodTracker.coreSettings }) { FoodTracker.lines() },
            HudElement("Date/Time HUD", { ConfigUtils.guiElements.realTimeTracker.coreSettings }) { RealTimeTracker.lines() },
            HudElement("Network HUD", { ConfigUtils.guiElements.networkTracker.coreSettings }) { NetworkTracker.lines() },
            HudElement("Entity HUD", { ConfigUtils.guiElements.entityTracker.coreSettings }) { EntityTracker.lines() },
            HudElement("Fishing HUD", { ConfigUtils.brainageHudParity.fishing.coreSettings }) { FishingHud.lines() },
            HudElement("Performance HUD", { ConfigUtils.brainageHudParity.performance.coreSettings }) { PerformanceHud.lines() },
            HudElement("Reach HUD", { ConfigUtils.brainageHudParity.reach.coreSettings }) { ReachHud.lines() },
            object : HudElement("Keystrokes HUD", { ConfigUtils.brainageHudParity.keystrokes.coreSettings }) {
                override fun render(coreSettings: GUIElements.CoreSettings, editing: Boolean) = KeystrokesHud.render(coreSettings)
            },
            HudElement("Enchant Info HUD", { ConfigUtils.brainageHudParity.enchantInfoHud.coreSettings }) { EnchantInfoHud.lines() },
            object : HudElement("Status Effect HUD", { ConfigUtils.brainageHudParity.statusEffectHud.coreSettings }) {
                override fun render(coreSettings: GUIElements.CoreSettings, editing: Boolean) = StatusEffectHud.render(coreSettings)
            },
        )

        private val placedBounds = IdentityHashMap<GUIElements.CoreSettings, ElementBounds>()

        /** Where the element drew itself on the last frame, or null if it drew nothing. */
        fun boundsOf(coreSettings: GUIElements.CoreSettings): ElementBounds? = placedBounds[coreSettings]

        /**
         * Draws [lines] as one element: positioned by its anchor and offsets, wrapped to its max width, on its
         * backdrop, in its text colour (lines without a § code) with or without shadows. Nothing is drawn for
         * an empty list.
         */
        fun renderElement(coreSettings: GUIElements.CoreSettings, lines: List<String>) {
            drawElement(coreSettings, lines)
        }

        /** [renderElement], returning where each line was drawn so callers can decorate lines (e.g. item icons). */
        fun drawElement(coreSettings: GUIElements.CoreSettings, lines: List<String>): List<PlacedLine> {
            if (lines.isEmpty()) return emptyList()
            val font = Minecraft.getMinecraft().fontRendererObj ?: return emptyList()

            val padding = padding(coreSettings)
            val maxWidth = maxWidth(coreSettings)
            val wrapped = ArrayList<String>(lines.size)
            val sources = ArrayList<Int>(lines.size)
            for (index in lines.indices) {
                val parts = if (maxWidth > 0) font.listFormattedStringToWidth(lines[index], maxWidth) else null
                if (parts.isNullOrEmpty()) {
                    wrapped.add(lines[index])
                    sources.add(index)
                } else {
                    for (part in parts) {
                        wrapped.add(part)
                        sources.add(index)
                    }
                }
            }

            val widths = IntArray(wrapped.size) { font.getStringWidth(wrapped[it]) }
            val contentWidth = widths.maxOrNull() ?: 0
            val lineHeight = font.FONT_HEIGHT + padding
            val contentHeight = lineHeight * wrapped.size - padding
            val inset = padding * 2
            val bounds = placeElement(coreSettings, contentWidth + inset * 2, contentHeight + inset * 2)

            val opacity = backdropOpacity(coreSettings)
            if (opacity > 0) Gui.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, opacity shl 24)

            val colour = textColour(coreSettings)
            val shadows = textShadows(coreSettings)
            val axis = ElementPlacement.horizontalAxis(coreSettings.anchorCorner)
            val placed = ArrayList<PlacedLine>(wrapped.size)
            for (i in wrapped.indices) {
                // lines hug the anchored side of the element, like the element hugs that side of the screen
                val x = when (axis) {
                    ElementPlacement.Axis.START -> bounds.left + inset
                    ElementPlacement.Axis.END -> bounds.right - inset - widths[i]
                    ElementPlacement.Axis.CENTRE -> bounds.left + inset + (contentWidth - widths[i]) / 2
                }
                val y = bounds.top + inset + lineHeight * i
                font.drawString(wrapped[i], x.toFloat(), y.toFloat(), colour, shadows)
                placed.add(PlacedLine(wrapped[i], sources[i], widths[i], x, y))
            }
            return placed
        }

        /**
         * Positions an element of the given size by its anchor and offsets and records its bounds for the
         * element editor. Elements with their own layout call this and draw inside the returned bounds.
         */
        fun placeElement(coreSettings: GUIElements.CoreSettings, width: Int, height: Int): ElementBounds {
            val resolution = ScaledResolution(Minecraft.getMinecraft())
            val left = ElementPlacement.position(ElementPlacement.horizontalAxis(coreSettings.anchorCorner), resolution.scaledWidth, width, coreSettings.x)
            val top = ElementPlacement.position(ElementPlacement.verticalAxis(coreSettings.anchorCorner), resolution.scaledHeight, height, coreSettings.y)
            val bounds = ElementBounds(left, top, left + width, top + height)
            placedBounds[coreSettings] = bounds
            return bounds
        }

        /** Opaque ARGB text colour for lines without their own colour code. */
        fun textColour(coreSettings: GUIElements.CoreSettings): Int {
            val overrides = coreSettings.elementOverrides
            val colour = if (overrides.overrideTextColour) overrides.textColour else ConfigUtils.globalGuiSettings.textColour
            return ChromaColour.specialToChromaRGB(colour) or OPAQUE
        }

        fun textShadows(coreSettings: GUIElements.CoreSettings): Boolean {
            val overrides = coreSettings.elementOverrides
            return if (overrides.overrideTextShadows) overrides.textShadows else ConfigUtils.globalGuiSettings.textShadows
        }

        /** Backdrop alpha from 0 (none) to 255. */
        fun backdropOpacity(coreSettings: GUIElements.CoreSettings): Int {
            val overrides = coreSettings.elementOverrides
            val opacity = if (overrides.overrideBackdropOpacity) overrides.backdropOpacity else ConfigUtils.globalGuiSettings.backdropOpacity
            return opacity.coerceIn(0, 255)
        }

        fun padding(coreSettings: GUIElements.CoreSettings): Int {
            val overrides = coreSettings.elementOverrides
            val padding = if (overrides.overridePadding) overrides.padding else ConfigUtils.globalGuiSettings.paddingInPixels
            return padding.coerceAtLeast(0)
        }

        /** Maximum line width in pixels before wrapping, or 0 for no limit. */
        fun maxWidth(coreSettings: GUIElements.CoreSettings): Int {
            val overrides = coreSettings.elementOverrides
            val maxWidth = if (overrides.overrideMaxWidth) overrides.maxWidth else ConfigUtils.globalGuiSettings.maxElementWidth
            return maxWidth.coerceAtLeast(0)
        }

        private const val OPAQUE = 0xFF000000.toInt()
    }
}
