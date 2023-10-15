package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.config.categories.GUIElements
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.keybinds.ToggleSneakKeybind
import com.github.brainage04.togglesprint.keybinds.ToggleSprintKeybind
import net.minecraft.client.Minecraft

class ToggleSprintTracker {
    companion object {
        private val config: GUIElements.GUIElement get() = ToggleSprintMain.config.guiElements.toggleSprintElement
        private val sprint: ToggleMovementKeys.ToggleSprintCategory get() = ToggleSprintMain.config.toggleMovementKeys.toggleSprintCategory
        private val sneak: ToggleMovementKeys.ToggleSneakCategory get() = ToggleSprintMain.config.toggleMovementKeys.toggleSneakCategory

        fun toggleSprintTracker() {
            if (!config.isEnabled || (!sprint.toggleSprint && !sneak.toggleSneak)) return

            val player = Minecraft.getMinecraft().thePlayer ?: return

            val text: String = if (ToggleSprintKeybind.isToggled) "§f[Sprinting (Toggled)]"
            else if (player.isSprinting) "§f[Sprinting (Vanilla)]"
            else if (player.isSneaking) {
                when (ToggleSneakKeybind.isToggled) {
                    true -> "§f[Sneaking (Toggled)]"
                    false -> "§f[Sneaking (Vanilla)]"
                }
            } else "§f"

            RenderGuiData.renderElement(
                config.x,
                config.y,
                config.displayAnchor,
                text
            )
        }
    }
}