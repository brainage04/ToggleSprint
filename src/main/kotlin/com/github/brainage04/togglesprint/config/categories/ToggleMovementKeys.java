package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigOption;

public class ToggleMovementKeys {
    @Expose
    @ConfigOption(name = "Toggle Sprint", desc = "Enables toggle sprint.")
    @ConfigEditorBoolean
    public boolean toggleSprint = true;

    @Expose
    @ConfigOption(name = "Toggle Sneak", desc = "Enables toggle sneak.")
    @ConfigEditorBoolean
    public boolean toggleSneak = false;
}
