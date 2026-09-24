package com.github.brainage04.togglesprint.gui

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
                true -> "[Sneaking (Toggled)]"
                false -> "[Sneaking (Vanilla)]"
            }
        }
        else if (isSprintToggled) "[Sprinting (Toggled)]"
        else if (player.isSprinting) "[Sprinting (Vanilla)]"
        else "${ChatUtils.grayChar}[Walking (Vanilla)]"
    }

    fun lines(): List<String> {
        val minecraft = Minecraft.getMinecraft() ?: return emptyList()
        val player = minecraft.thePlayer ?: return emptyList()

        val textArray = arrayListOf<String>()

        if (ConfigUtils.guiElements.toggleSprintElement.showInternalValues) {
            textArray.add("toggleSprint: $isSprintToggled, sprintKeyDown: ${minecraft.gameSettings.keyBindSprint.isKeyDown}")
            textArray.add("toggleSneak: $isSneakToggled, sneakKeyDown: ${minecraft.gameSettings.keyBindSneak.isKeyDown}")
        }

        textArray.add(getStatus(player))

        return textArray
    }
}