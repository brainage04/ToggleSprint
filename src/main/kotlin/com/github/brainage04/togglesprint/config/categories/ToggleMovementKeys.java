package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.Accordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigEditorKeybind;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ToggleMovementKeys {
    @Expose
    @ConfigOption(name = "Toggle Sprint", desc = "")
    @Accordion
    public ToggleSprintCategory toggleSprintCategory = new ToggleSprintCategory();

    public static class ToggleSprintCategory {
        @Expose
        @ConfigOption(name = "Toggle Sprint", desc = "Enables toggle sprint.")
        @ConfigEditorBoolean
        public boolean toggleSprint = false;

        @Expose
        @ConfigOption(name = "Toggle Sprint Keybind", desc = "The keybind used to enable toggle sprint during gameplay.")
        @ConfigEditorKeybind(defaultKey = Keyboard.KEY_RCONTROL)
        public KeyBinding toggleSprintKeybind;
    }

    @Expose
    @ConfigOption(name = "Toggle Sprint", desc = "")
    @Accordion
    public ToggleSneakCategory toggleSneakCategory = new ToggleSneakCategory();

    public static class ToggleSneakCategory {
        @Expose
        @ConfigOption(name = "Toggle Sneak", desc = "Enables toggle sneak.")
        @ConfigEditorBoolean
        public boolean toggleSneak = false;

        @Expose
        @ConfigOption(name = "Toggle Sneak Keybind", desc = "The keybind used to enable toggle sneak during gameplay.")
        @ConfigEditorKeybind(defaultKey = Keyboard.KEY_RSHIFT)
        public KeyBinding toggleSneakKeybind;
    }
}
