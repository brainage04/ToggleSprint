package io.github.brainage04.togglesprint.keys

import io.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

class ToggleSprintKeybind : KeyBinding("Toggle Sprint", Keyboard.KEY_RCONTROL, ToggleSprintMain.MOD_NAME)