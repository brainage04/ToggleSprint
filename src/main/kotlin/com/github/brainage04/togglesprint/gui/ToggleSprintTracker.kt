package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import com.github.brainage04.togglesprint.utils.ConfigUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object ToggleSprintTracker {
    var isSprintToggled = ConfigUtils.toggleMovementKeys.toggleSprint.defaultState
    var isSneakToggled = ConfigUtils.toggleMovementKeys.toggleSneak.defaultState

    private fun getStatus(player: EntityPlayerSP): String {
        return if (player.isSneaking) {
            when (isSneakToggled) {
                true -> "${ConfigUtils.primaryChars}[Sneaking (Toggled)]"
                false -> "${ConfigUtils.primaryChars}[Sneaking (Vanilla)]"
            }
        }
        else if (isSprintToggled) "${ConfigUtils.primaryChars}[Sprinting (Toggled)]"
        else if (player.isSprinting) "${ConfigUtils.primaryChars}[Sprinting (Vanilla)]"
        else "${ConfigUtils.primaryChars + ChatUtils.grayChar}[Walking (Vanilla)]"
    }

    fun toggleSprintTracker() {
        if (!ConfigUtils.guiElements.toggleSprintElement.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        val player = minecraft.thePlayer ?: return

        val textArray = arrayListOf<String>()

        if (ConfigUtils.guiElements.toggleSprintElement.showInternalValues) {
            textArray.add("${ConfigUtils.primaryChars}toggleSprint: $isSprintToggled, sprintKeyDown: ${minecraft.gameSettings.keyBindSprint.isKeyDown}")
            textArray.add("${ConfigUtils.primaryChars}toggleSneak: $isSneakToggled, sneakKeyDown: ${minecraft.gameSettings.keyBindSneak.isKeyDown}")
        }

        textArray.add(getStatus(player))

        RenderGuiData.renderElement(
            ConfigUtils.guiElements.toggleSprintElement.coreSettings.x,
            ConfigUtils.guiElements.toggleSprintElement.coreSettings.y,
            ConfigUtils.guiElements.toggleSprintElement.coreSettings.anchorCorner,
            textArray,
        )
    }
}