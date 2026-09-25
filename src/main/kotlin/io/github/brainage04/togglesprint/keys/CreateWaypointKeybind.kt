package io.github.brainage04.togglesprint.keys

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.brainage04.togglesprint.waypoint.WaypointActions
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

/** Creates a waypoint where the player stands, named "Waypoint 1", "Waypoint 2", and so on. */
class CreateWaypointKeybind : KeyBinding("Create Waypoint", Keyboard.KEY_B, ToggleSprintMain.MOD_NAME) {
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        if (Minecraft.getMinecraft().thePlayer == null) return

        while (isPressed) {
            WaypointActions.createAtPlayer(null)
        }
    }
}
