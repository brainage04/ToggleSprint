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
        if (!guiElements.toggleSprintElement.isEnabled || (!toggleMovementKeys.toggleSprintCategory.toggleSprint && !toggleMovementKeys.toggleSneakCategory.toggleSneak)) return

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
            guiElements.toggleSprintElement.x,
            guiElements.toggleSprintElement.y,
            guiElements.toggleSprintElement.anchorCorner,
            text
        )
    }
}