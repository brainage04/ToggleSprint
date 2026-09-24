package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import com.github.brainage04.togglesprint.waypoint.WaypointsScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

/** Opens the screen to add, edit, hide/show and delete the current world's waypoints. */
class ManageWaypointsKeybind : KeyBinding("Manage Waypoints", Keyboard.KEY_U, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        val minecraft = Minecraft.getMinecraft()
        if (minecraft.thePlayer == null) return

        if (isPressed && minecraft.currentScreen == null) {
            minecraft.displayGuiScreen(WaypointsScreen(null))
        }
    }
}
