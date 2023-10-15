package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.keybinds.ToggleSneakKeybind
import com.github.brainage04.togglesprint.keybinds.ToggleSprintKeybind
import net.minecraft.client.Minecraft

object ToggleSprintTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    fun toggleSprintTracker() {
        if (!guiElements.toggleSprintElement.coreSettings.isEnabled || (!toggleMovementKeys.toggleSprint && !toggleMovementKeys.toggleSneak)) return

        val player = Minecraft.getMinecraft().thePlayer ?: return

        val text = if (player.isSneaking) {
            when (ToggleSneakKeybind.isToggled) {
                true -> "§f[Sneaking (Toggled)]"
                false -> "§f[Sneaking (Vanilla)]"
            }
        } else if (ToggleSprintKeybind.isToggled) "§f[Sprinting (Toggled)]"
        else if (player.isSprinting) "§f[Sprinting (Vanilla)]"
        else "§f"

        RenderGuiData.renderElement(
            guiElements.toggleSprintElement.coreSettings.x,
            guiElements.toggleSprintElement.coreSettings.y,
            guiElements.toggleSprintElement.coreSettings.anchorCorner,
            text
        )
    }
}