package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.keybinds.ToggleSneakKeybind
import com.github.brainage04.togglesprint.keybinds.ToggleSprintKeybind
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object ToggleSprintTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    private fun getStatus(player: EntityPlayerSP): String {
        return if (player.isSneaking) {
            when (ToggleSneakKeybind.isToggled) {
                true -> "§f[Sneaking (Toggled)]"
                false -> "§f[Sneaking (Vanilla)]"
            }
        } else if (ToggleSprintKeybind.isToggled) "§f[Sprinting (Toggled)]"
        else if (player.isSprinting) "§f[Sprinting (Vanilla)]"
        else "§f"
    }

    fun toggleSprintTracker() {
        if (!guiElements.toggleSprintElement.coreSettings.isEnabled || (!toggleMovementKeys.toggleSprint && !toggleMovementKeys.toggleSneak)) return

        val player = Minecraft.getMinecraft().thePlayer ?: return

        if (guiElements.toggleSprintElement.showInternalValues) {
            RenderGuiData.renderElement(
                guiElements.toggleSprintElement.coreSettings.x,
                guiElements.toggleSprintElement.coreSettings.y,
                guiElements.toggleSprintElement.coreSettings.anchorCorner,
                arrayOf(
                    "§ftoggleSprint: ${ToggleSprintKeybind.isToggled}",
                    "§ftoggleSneak: ${ToggleSneakKeybind.isToggled}",
                    getStatus(player),
                ),
            )
        } else {
            RenderGuiData.renderElement(
                guiElements.toggleSprintElement.coreSettings.x,
                guiElements.toggleSprintElement.coreSettings.y,
                guiElements.toggleSprintElement.coreSettings.anchorCorner,
                getStatus(player),
            )
        }
    }
}