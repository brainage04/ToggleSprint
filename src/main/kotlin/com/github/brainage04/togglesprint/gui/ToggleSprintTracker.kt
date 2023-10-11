package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.Main
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft

class ToggleSprintTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = Main.config.guiElements.toggleSprintElement
        private val sprint: ToggleMovementKeys.ToggleSprintCategory get() = Main.config.toggleMovementKeys.toggleSprintCategory
        private val sneak: ToggleMovementKeys.ToggleSneakCategory get() = Main.config.toggleMovementKeys.toggleSneakCategory

        fun toggleSprintTracker() {
            if (!config.isEnabled) return

            val player = Minecraft.getMinecraft().thePlayer ?: return

            var text = ""

            if (player.isSprinting) {
                text = when (sprint.toggleSprint) {
                    true -> "[Sprinting (Toggled)]"
                    false -> "[Sprinting (Vanilla)]"
                }
            }

            if (player.isSneaking) {
                text = when (sneak.toggleSneak) {
                    true -> "[Sneaking (Toggled)]"
                    false -> "[Sneaking (Vanilla)]"
                }
            }

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