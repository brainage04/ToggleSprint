package io.github.brainage04.togglesprint.keys

import io.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

class ElementEditorKeybind : KeyBinding("Open Element Editor", Keyboard.KEY_ADD, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        if (this.isPressed) ToggleSprintMain.configManager.openElementEditor()
    }
}
