package io.github.brainage04.togglesprint.config

import io.github.brainage04.togglesprint.ToggleSprintMain
import io.github.moulberry.moulconfig.gui.GuiElement
import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

/** Returns to the screen that opened the editor instead of always leaving to the title screen. */
class ConfigEditorScreen(editor: GuiElement, private val parent: GuiScreen?) : GuiScreenElementWrapper(editor) {
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            ToggleSprintMain.configManager.save()
            mc.displayGuiScreen(parent)
        } else {
            super.keyTyped(typedChar, keyCode)
        }
    }
}
