package com.github.brainage04.togglesprint.gui

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.events.InputEventTracker.Companion.isSneakToggled
import com.github.brainage04.togglesprint.events.InputEventTracker.Companion.isSprintToggled
import com.github.brainage04.togglesprint.gui.core.RenderGuiData
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object ToggleSprintTracker {
    private val guiElements get() = ToggleSprintMain.config.guiElements
    private val toggleMovementKeys get() = ToggleSprintMain.config.toggleMovementKeys

    private fun getStatus(player: EntityPlayerSP): String {
        return if (player.isSneaking) {
            when (isSneakToggled) {
                true -> "§f[Sneaking (Toggled)]"
                false -> "§f[Sneaking (Vanilla)]"
            }
        } else if (isSprintToggled) "§f[Sprinting (Toggled)]"
        else if (player.isSprinting) "§f[Sprinting (Vanilla)]"
        else "§f"
    }

    fun toggleSprintTracker() {
        if (!guiElements.toggleSprintElement.coreSettings.isEnabled || (!toggleMovementKeys.toggleSprint.isEnabled && !toggleMovementKeys.toggleSneak.isEnabled)) return

        val player = Minecraft.getMinecraft().thePlayer ?: return

        val textArray = arrayListOf<String>()

        if (guiElements.toggleSprintElement.showInternalValues) {
            textArray.add("§ftoggleSprint: $isSprintToggled")
            textArray.add("§ftoggleSneak: $isSneakToggled")
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