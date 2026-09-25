package io.github.brainage04.togglesprint.keys

import io.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

class ToggleSneakKeybind : KeyBinding("Toggle Sneak", Keyboard.KEY_RSHIFT, ToggleSprintMain.MOD_NAME)