package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

class ToggleSprintTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = ToggleSprintMain.config.guiElements.toggleSprintElement
        private val sprint: ToggleMovementKeys.ToggleSprintCategory get() = ToggleSprintMain.config.toggleMovementKeys.toggleSprintCategory
        private val sneak: ToggleMovementKeys.ToggleSneakCategory get() = ToggleSprintMain.config.toggleMovementKeys.toggleSneakCategory

        fun toggleSprintTracker() {
            if (!config.isEnabled) return

            val player = Minecraft.getMinecraft().thePlayer ?: return

            var text: String

            if (sprint.toggleSprint) text = "§f[Sprinting (Toggled)]"
            else if (!sprint.toggleSprint && player.isSprinting) text = "§f[Sprinting (Vanilla)]"
            else if (player.isSneaking) {
                text = when (sneak.toggleSneak) {
                    true -> "§f[Sneaking (Toggled)]"
                    false -> "§f[Sneaking (Vanilla)]"
                }
            } else text = "§f"

            RenderGuiData.renderElement(
                config.x,
                config.y,
                config.horizontalAlignment,
                config.verticalAlignment,
                text
            )
        }
    }
}