package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.Accordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class ToggleMovementKeys {

    @Expose
    @ConfigOption(name = "Toggle Sprint", desc = "")
    @Accordion
    public ToggleKey toggleSprint = new ToggleKey(true, true);

    @Expose
    @ConfigOption(name = "Toggle Sneak", desc = "")
    @Accordion
    public ToggleKey toggleSneak = new ToggleKey(true, false);

    public static class ToggleKey {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables the ability to toggle the key.")
        @ConfigEditorBoolean
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "Default State", desc = "The default state of the key on startup.")
        @ConfigEditorBoolean
        public boolean defaultState;

        public ToggleKey(boolean isEnabled, boolean defaultState) {
            this.isEnabled = isEnabled;
            this.defaultState = defaultState;
        }
    }
}
