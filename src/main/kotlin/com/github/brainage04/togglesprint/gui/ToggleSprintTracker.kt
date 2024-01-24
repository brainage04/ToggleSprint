package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object ToggleSprintTracker {
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    var isSprintToggled = toggleMovementKeys.toggleSprint.defaultState
    var isSneakToggled = toggleMovementKeys.toggleSneak.defaultState

    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val globalGuiSettings get() = ToggleSprintMain.config.globalGuiSettings

    val primaryChars = ChatUtils.colourChars[globalGuiSettings.primaryColour] + ChatUtils.effectChars[globalGuiSettings.primaryEffect]

    private fun getStatus(player: EntityPlayerSP): String {
        return if (player.isSneaking) {
            when (isSneakToggled) {
                true -> "${primaryChars}[Sneaking (Toggled)]"
                false -> "${primaryChars}[Sneaking (Vanilla)]"
            }
        }
        else if (isSprintToggled) "${primaryChars}[Sprinting (Toggled)]"
        else if (player.isSprinting) "${primaryChars}[Sprinting (Vanilla)]"
        else "${primaryChars + ChatUtils.grayChar}[Walking (Vanilla)]"
    }

    fun toggleSprintTracker() {
        if (!guiElements.toggleSprintElement.coreSettings.isEnabled) return

        val minecraft = Minecraft.getMinecraft() ?: return
        val player = minecraft.thePlayer ?: return

        val textArray = arrayListOf<String>()

        if (guiElements.toggleSprintElement.showInternalValues) {
            textArray.add("${primaryChars}toggleSprint: $isSprintToggled, sprintKeyDown: ${minecraft.gameSettings.keyBindSprint.isKeyDown}")
            textArray.add("${primaryChars}toggleSneak: $isSneakToggled, sneakKeyDown: ${minecraft.gameSettings.keyBindSneak.isKeyDown}")
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