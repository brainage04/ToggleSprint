package com.github.brainage04.togglesprint.keybinds

import com.github.brainage04.togglesprint.ToggleSprintMain
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

class ToggleSneakKeybind : KeyBinding("Toggle Sneak", Keyboard.KEY_RSHIFT, ToggleSprintMain.MOD_NAME)