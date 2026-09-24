package com.github.brainage04.togglesprint.gui.core

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import io.github.moulberry.moulconfig.ChromaColour
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.util.IdentityHashMap

/**
 * Moves the elements in [RenderGuiData.elements]: click to select, drag or use the arrow keys to move, Space to
 * cycle the anchor. Elements stay at least the global screen margin away from the screen edges. Closing saves
 * the config; Escape and "Undo & Close" first restore every element's position from when the editor opened.
 *
 * The HUD keeps rendering underneath the screen, so the bounds read from [RenderGuiData.boundsOf] are from the
 * frame being drawn.
 */
class HudElementEditor : GuiScreen() {
    private class SavedPosition(val x: Double, val y: Double, val anchorCorner: Int)

    private val savedPositions = IdentityHashMap<GUIElements.CoreSettings, SavedPosition>()
    private var selected: HudElement? = null
    private var dragging = false
    private var dragMouseX = 0
    private var dragMouseY = 0
    private var dragLeft = 0
    private var dragTop = 0

    init {
        for (element in RenderGuiData.elements) {
            val coreSettings = element.coreSettings
            savedPositions[coreSettings] = SavedPosition(coreSettings.x, coreSettings.y, coreSettings.anchorCorner)
        }
    }

    override fun initGui() {
        buttonList.clear()
        buttonList.add(GuiButton(UNDO_BUTTON, width / 2 - 205, height - 30, 200, 20, "Undo & Close"))
        buttonList.add(GuiButton(SAVE_BUTTON, width / 2 + 5, height - 30, 200, 20, "Save & Close"))
    }

    override fun doesGuiPauseGame() = false

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            UNDO_BUTTON -> closeWithoutSaving()
            SAVE_BUTTON -> mc.displayGuiScreen(null)
        }
    }

    override fun onGuiClosed() {
        ToggleSprintMain.configManager.save()
    }

    private fun closeWithoutSaving() {
        for ((coreSettings, position) in savedPositions) {
            coreSettings.x = position.x
            coreSettings.y = position.y
            coreSettings.anchorCorner = position.anchorCorner
        }
        mc.displayGuiScreen(null)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closeWithoutSaving()
            return
        }

        val element = selected ?: return
        val bounds = RenderGuiData.boundsOf(element.coreSettings) ?: return
        if (keyCode == Keyboard.KEY_SPACE) {
            cycleAnchor(element.coreSettings, bounds)
            return
        }

        var step = 1
        if (isShiftKeyDown()) step *= 10
        if (isCtrlKeyDown()) step *= 5
        when (keyCode) {
            Keyboard.KEY_UP -> moveTo(element.coreSettings, bounds, bounds.left, bounds.top - step)
            Keyboard.KEY_DOWN -> moveTo(element.coreSettings, bounds, bounds.left, bounds.top + step)
            Keyboard.KEY_LEFT -> moveTo(element.coreSettings, bounds, bounds.left - step, bounds.top)
            Keyboard.KEY_RIGHT -> moveTo(element.coreSettings, bounds, bounds.left + step, bounds.top)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (buttonList.any { it.mousePressed(mc, mouseX, mouseY) }) {
            super.mouseClicked(mouseX, mouseY, mouseButton)
            return
        }
        if (mouseButton != 0) return

        selected = elementAt(mouseX, mouseY)
        val bounds = selected?.let { RenderGuiData.boundsOf(it.coreSettings) } ?: return
        dragging = true
        dragMouseX = mouseX
        dragMouseY = mouseY
        dragLeft = bounds.left
        dragTop = bounds.top
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        if (!dragging) return
        val element = selected ?: return
        val bounds = RenderGuiData.boundsOf(element.coreSettings) ?: return
        moveTo(element.coreSettings, bounds, dragLeft + mouseX - dragMouseX, dragTop + mouseY - dragMouseY)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        dragging = false
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val hovered = elementAt(mouseX, mouseY)
        for (element in listOfNotNull(hovered, selected?.takeIf { it !== hovered })) {
            val bounds = RenderGuiData.boundsOf(element.coreSettings) ?: continue
            Gui.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, HIGHLIGHT_COLOUR)
        }

        val lines = arrayListOf(
            "${ChatUtils.boldChar}Element Editor",
            "Click to select, hold click and drag to move",
            "Arrow keys for precise movement, Ctrl = x5, Shift = x10",
            "Space to cycle alignment",
            "Escape to close without saving",
            "",
        )
        val shown = hovered ?: selected
        if (shown != null) {
            val coreSettings = shown.coreSettings
            lines.add(shown.name)
            lines.add("X: ${coreSettings.x.toInt()} Y: ${coreSettings.y.toInt()}")
            lines.add("Anchor: ${ANCHOR_NAMES.getOrElse(coreSettings.anchorCorner) { "?" }}")
            lines.add(
                when {
                    shown === hovered && shown === selected -> "Highlighted & Selected"
                    shown === hovered -> "Highlighted"
                    else -> "Selected"
                }
            )
        }

        val colour = ChromaColour.specialToChromaRGB(ConfigUtils.globalGuiSettings.textColour) or OPAQUE
        for (i in lines.indices) {
            drawCenteredString(fontRendererObj, lines[i], width / 2, 10 + (fontRendererObj.FONT_HEIGHT + 2) * i, colour)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    /** The topmost element drawn at the given point; later elements are drawn over earlier ones. */
    private fun elementAt(x: Int, y: Int): HudElement? =
        RenderGuiData.elements.lastOrNull { RenderGuiData.boundsOf(it.coreSettings)?.contains(x, y) == true }

    /** Moves the element's top left corner to the given point, kept within the screen margin. */
    private fun moveTo(coreSettings: GUIElements.CoreSettings, bounds: RenderGuiData.ElementBounds, left: Int, top: Int) {
        val margin = ConfigUtils.globalGuiSettings.screenMargin.coerceAtLeast(0)
        val clampedLeft = left.coerceIn(margin, maxOf(margin, width - bounds.width - margin))
        val clampedTop = top.coerceIn(margin, maxOf(margin, height - bounds.height - margin))
        coreSettings.x = ElementPlacement.offset(ElementPlacement.horizontalAxis(coreSettings.anchorCorner), width, bounds.width, clampedLeft)
        coreSettings.y = ElementPlacement.offset(ElementPlacement.verticalAxis(coreSettings.anchorCorner), height, bounds.height, clampedTop)
    }

    /** Switches to the next anchor, adjusting the offsets so the element stays where it is. */
    private fun cycleAnchor(coreSettings: GUIElements.CoreSettings, bounds: RenderGuiData.ElementBounds) {
        coreSettings.anchorCorner = ElementPlacement.nextAnchor(coreSettings.anchorCorner)
        coreSettings.x = ElementPlacement.offset(ElementPlacement.horizontalAxis(coreSettings.anchorCorner), width, bounds.width, bounds.left)
        coreSettings.y = ElementPlacement.offset(ElementPlacement.verticalAxis(coreSettings.anchorCorner), height, bounds.height, bounds.top)
    }

    private companion object {
        const val UNDO_BUTTON = 0
        const val SAVE_BUTTON = 1
        const val HIGHLIGHT_COLOUR = 0x7FFFFFFF
        const val OPAQUE = 0xFF000000.toInt()

        /** Names of the `CoreSettings.anchorCorner` dropdown values, by index. */
        val ANCHOR_NAMES = arrayOf(
            "Top Left", "Top Right", "Bottom Left", "Bottom Right",
            "Centre Left", "Centre Right", "Centre Top", "Centre Bottom", "Centre",
        )
    }
}
