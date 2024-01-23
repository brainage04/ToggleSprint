package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object ToggleSprintTracker {
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    var isSprintToggled = toggleMovementKeys.toggleSprint.defaultState
    var isSneakToggled = toggleMovementKeys.toggleSneak.defaultState

    private val guiElements get() = ToggleSprintMain.config.guiElements

    private fun getStatus(player: EntityPlayerSP): String {
        return if (player.isSneaking) {
            when (isSneakToggled) {
                true -> "§f[Sneaking (Toggled)]"
                false -> "§f[Sneaking (Vanilla)]"
            }
        }
        else if (isSprintToggled) "§f[Sprinting (Toggled)]"
        else if (player.isSprinting) "§f[Sprinting (Vanilla)]"
        else "§7[Walking (Vanilla)]"
    }

    fun toggleSprintTracker() {
        if (!guiElements.toggleSprintElement.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        val player = minecraft.thePlayer ?: return

        val textArray = arrayListOf<String>()

        if (guiElements.toggleSprintElement.showInternalValues) {
            textArray.add("§ftoggleSprint: $isSprintToggled, sprintKeyDown: ${minecraft.gameSettings.keyBindSprint.isKeyDown}")
            textArray.add("§ftoggleSneak: $isSneakToggled, sneakKeyDown: ${minecraft.gameSettings.keyBindSneak.isKeyDown}")
        }

        textArray.add(getStatus(player))

        RenderGuiData.renderElement(
            guiElements.toggleSprintElement.coreSettings.x,
            guiElements.toggleSprintElement.coreSettings.y,
            guiElements.toggleSprintElement.coreSettings.anchorCorner,
            textArray,
        )
    }
}