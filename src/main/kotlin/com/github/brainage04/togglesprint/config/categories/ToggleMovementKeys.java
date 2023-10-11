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
        public boolean toggleSprint = true;

        @Expose
        @ConfigOption(name = "Unsprint When Disabling", desc = "Reverts to walking speed when player disables toggle sprint.")
        @ConfigEditorBoolean
        public boolean unsprintWhenDisabling = false;
    }

    @Expose
    @ConfigOption(name = "Toggle Sneak", desc = "")
    @Accordion
    public ToggleSneakCategory toggleSneakCategory = new ToggleSneakCategory();

    public static class ToggleSneakCategory {
        @Expose
        @ConfigOption(name = "Toggle Sneak", desc = "Enables toggle sneak.")
        @ConfigEditorBoolean
        public boolean toggleSneak = false;

        @Expose
        @ConfigOption(name = "Unsneak When Disabling", desc = "Reverts to standing when player disables toggle sneak.")
        @ConfigEditorBoolean
        public boolean unsneakWhenDisabling = true;
    }
}
