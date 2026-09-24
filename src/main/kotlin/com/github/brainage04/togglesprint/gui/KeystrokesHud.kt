package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.settings.KeyBinding
import java.util.IdentityHashMap

object KeystrokesHud {
    private const val KEY_SIZE = 20
    private data class KeyItem(val key: KeyBinding?, val label: String, val x: Int, val y: Int, val width: Int, val height: Int)

    private val transitions = IdentityHashMap<KeyBinding, KeystrokesHudSupport.Transition>()
    private val leftClicks = ArrayList<Long>()
    private val rightClicks = ArrayList<Long>()
    private var leftWasDown = false
    private var rightWasDown = false

    fun render(coreSettings: GUIElements.CoreSettings) {
        val config = ConfigUtils.brainageHudParity.keystrokes
        val minecraft = Minecraft.getMinecraft()
        val nowMillis = System.currentTimeMillis()
        updateClicks(minecraft, nowMillis)
        val items = buildItems(minecraft, RenderGuiData.padding(coreSettings))
        if (items.isEmpty()) return

        var width = 0
        var height = 0
        for (item in items) {
            width = Math.max(width, item.x + item.width)
            height = Math.max(height, item.y + item.height)
        }
        val bounds = RenderGuiData.placeElement(coreSettings, width, height)
        val position = Pair(bounds.left, bounds.top)
        val nowNanos = System.nanoTime()
        val textRgb = RenderGuiData.textColour(coreSettings) and 0xFFFFFF
        val shadows = RenderGuiData.textShadows(coreSettings)
        val backdropAlpha = config.keyBackdropOpacity
        val seen = IdentityHashMap<KeyBinding, Boolean>()
        val font = minecraft.fontRendererObj ?: return

        for (item in items) {
            var backdrop = KeystrokesHudSupport.withAlpha(0, backdropAlpha)
            var textColour = KeystrokesHudSupport.withAlpha(textRgb, 255)
            val key = item.key
            if (key != null) {
                val down = key.isKeyDown
                var transition = transitions[key]
                if (transition == null) {
                    transition = KeystrokesHudSupport.Transition(if (down) 1f else 0f, down, nowNanos)
                    transitions.put(key, transition)
                } else {
                    KeystrokesHudSupport.updateTransition(transition, down, nowNanos)
                }
                seen.put(key, true)
                backdrop = KeystrokesHudSupport.withAlpha(KeystrokesHudSupport.lerpRgb(0, textRgb, transition.progress), backdropAlpha)
                textColour = KeystrokesHudSupport.withAlpha(KeystrokesHudSupport.lerpRgb(textRgb, 0, transition.progress), 255)
            }
            Gui.drawRect(position.first + item.x, position.second + item.y, position.first + item.x + item.width, position.second + item.y + item.height, backdrop)
            font.drawString(item.label, (position.first + item.x + (item.width - font.getStringWidth(item.label)) / 2).toFloat(), (position.second + item.y + (item.height - font.FONT_HEIGHT) / 2).toFloat(), textColour, shadows)
        }
        val iterator = transitions.keys.iterator()
        while (iterator.hasNext()) if (!seen.containsKey(iterator.next())) iterator.remove()
    }

    private fun updateClicks(minecraft: Minecraft, nowMillis: Long) {
        val config = ConfigUtils.brainageHudParity.keystrokes
        val format = config.clicksPerSecondFormat.coerceIn(0, 3)
        if (config.showMouseButtons && (format == 1 || format == 3)) {
            val leftDown = minecraft.gameSettings.keyBindAttack.isKeyDown
            if (leftDown && !leftWasDown) leftClicks.add(nowMillis)
            leftWasDown = leftDown
            KeystrokesHudSupport.expireClicks(leftClicks, nowMillis)
        }
        if (config.showMouseButtons && (format == 2 || format == 3)) {
            val rightDown = minecraft.gameSettings.keyBindUseItem.isKeyDown
            if (rightDown && !rightWasDown) rightClicks.add(nowMillis)
            rightWasDown = rightDown
            KeystrokesHudSupport.expireClicks(rightClicks, nowMillis)
        }
    }

    private fun buildItems(minecraft: Minecraft, gap: Int): List<KeyItem> {
        val config = ConfigUtils.brainageHudParity.keystrokes
        val items = ArrayList<KeyItem>()
        var y = 0
        if (config.showWasd) {
            items.add(KeyItem(minecraft.gameSettings.keyBindForward, "W", KEY_SIZE + gap, y, KEY_SIZE, KEY_SIZE))
            y += KEY_SIZE + gap
            items.add(KeyItem(minecraft.gameSettings.keyBindLeft, "A", 0, y, KEY_SIZE, KEY_SIZE))
            items.add(KeyItem(minecraft.gameSettings.keyBindBack, "S", KEY_SIZE + gap, y, KEY_SIZE, KEY_SIZE))
            items.add(KeyItem(minecraft.gameSettings.keyBindRight, "D", (KEY_SIZE + gap) * 2, y, KEY_SIZE, KEY_SIZE))
            y += KEY_SIZE + gap
        }
        if (config.showSpace) {
            items.add(KeyItem(minecraft.gameSettings.keyBindJump, "_____", 0, y, KEY_SIZE * 3 + gap * 2, KEY_SIZE))
            y += KEY_SIZE + gap
        }
        if (config.showMouseButtons) {
            val mouseWidth = KEY_SIZE * 3 / 2 + gap / 2
            items.add(KeyItem(minecraft.gameSettings.keyBindAttack, "LMB", 0, y, mouseWidth, KEY_SIZE))
            items.add(KeyItem(minecraft.gameSettings.keyBindUseItem, "RMB", mouseWidth + gap, y, mouseWidth, KEY_SIZE))
            y += KEY_SIZE + gap
        }
        cpsLabel(config.clicksPerSecondFormat, leftClicks.size, rightClicks.size)?.let {
            items.add(KeyItem(null, it, 0, y, KEY_SIZE * 3 + gap * 2, KEY_SIZE))
        }
        return items
    }

    fun cpsLabel(format: Int, leftClicks: Int, rightClicks: Int): String? = when (format.coerceIn(0, 3)) {
        1 -> "$leftClicks CPS"
        2 -> "$rightClicks CPS (R)"
        3 -> "$leftClicks | $rightClicks CPS"
        else -> null
    }
}
