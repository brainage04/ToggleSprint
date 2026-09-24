package com.github.brainage04.togglesprint.gui.core

import com.github.brainage04.togglesprint.config.categories.GUIElements

/**
 * One entry in [RenderGuiData.elements]: a movable HUD element.
 *
 * Text elements only supply [lines] (an empty list draws nothing); the registry skips disabled elements and
 * draws the rest through [RenderGuiData.renderElement], which also records the bounds the element editor
 * uses. Elements that draw more than text override [render] and must still place themselves through
 * [RenderGuiData.renderElement] or [RenderGuiData.placeElement] so the editor can find them.
 *
 * @param name shown in the element editor, and drawn in place of the element while the editor is open and
 * the element has nothing to show.
 * @param settings reads the element's settings from the live config on every frame, so a config reload
 * or reset is picked up.
 */
open class HudElement(
    val name: String,
    private val settings: () -> GUIElements.CoreSettings,
    private val lines: () -> List<String> = { emptyList() },
) {
    val coreSettings: GUIElements.CoreSettings get() = settings()

    /** Draws the element; [editing] is true while the element editor is open. */
    open fun render(coreSettings: GUIElements.CoreSettings, editing: Boolean) {
        val text = lines()
        RenderGuiData.renderElement(coreSettings, if (text.isEmpty() && editing) listOf(name) else text)
    }
}
