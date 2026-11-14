package io.github.brainage04.togglesprint.util

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.relauncher.ReflectionHelper
import java.lang.reflect.Field

object KeyBindingUtils {
    // KeyBinding.setKeyBindState finds the binding by key code (1.8.9 KeyBinding.java:33-39), so it presses whichever binding
    // registered last on a shared key, and nothing for an unbound one. The pressed field has no setter (SRG name field_74513_e).
    private val pressed: Field = ReflectionHelper.findField(KeyBinding::class.java, "pressed", "field_74513_e")

    fun setPressed(binding: KeyBinding, isPressed: Boolean) {
        pressed.setBoolean(binding, isPressed)
    }
}
