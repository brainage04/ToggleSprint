package com.github.brainage04.togglesprint.features

import com.github.brainage04.togglesprint.Main
import com.github.brainage04.togglesprint.config.categories.ToggleMovementKeys
import com.github.brainage04.togglesprint.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class ToggleMovementKeyHandler {
    private var sneakTempDisabled = false

    companion object {
        val sprint: ToggleMovementKeys.ToggleSprintCategory get() = Main.config.toggleMovementKeys.toggleSprintCategory
        val sneak: ToggleMovementKeys.ToggleSneakCategory get() = Main.config.toggleMovementKeys.toggleSneakCategory
    }

    private fun toggleSprint() {
        sprint.toggleSprint = !sprint.toggleSprint

        if (sprint.toggleSprint) {
            ChatUtils.messageToChat("[Sprinting (Toggled)]", ChatUtils.PrefixType.GREEN, ChatUtils.SoundType.NOTIFICATION)
            ChatUtils.messageToChat("[Sprinting (Untoggled)]", ChatUtils.PrefixType.RED, ChatUtils.SoundType.ALERT)
        }
    }

    private fun toggleSneak() {
        sneak.toggleSneak = !sneak.toggleSneak

        if (sneak.toggleSneak) {
            ChatUtils.messageToChat("[Sneaking (Toggled)]", ChatUtils.PrefixType.GREEN, ChatUtils.SoundType.NOTIFICATION)
            ChatUtils.messageToChat("[Sneaking (Untoggled)]", ChatUtils.PrefixType.RED, ChatUtils.SoundType.ALERT)
        }
    }


    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (Minecraft.getMinecraft().thePlayer == null) return

        if (event.phase != TickEvent.Phase.START) return

        // toggle sprint
        if (sprint.toggleSprintKeybind.isPressed) toggleSprint()
        if (sprint.toggleSprint) KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.keyCode, true)

        // toggle sneak
        if (sneak.toggleSneakKeybind.isPressed) toggleSneak()
        if (sneak.toggleSneak) KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSneak.keyCode, true)

        if (Minecraft.getMinecraft().currentScreen == null && sneakTempDisabled) { // GUI handler (sneaking while in a GUI is illegal on most servers)
            toggleSneak()
            sneakTempDisabled = false
        }
    }

    @SubscribeEvent
    fun onGuiOpenEvent(event: GuiOpenEvent) {
        if (Minecraft.getMinecraft().thePlayer == null) return

        sneakTempDisabled = true

        toggleSneak()
    }
}