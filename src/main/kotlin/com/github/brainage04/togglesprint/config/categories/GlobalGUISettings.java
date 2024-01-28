package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GlobalGUISettings {
    @Expose
    @ConfigOption(name = "Primary Colour", desc = "Change the default colour of GUI elements.")
    @ConfigEditorDropdown(values = {"Dark Red", "Red", "Gold", "Yellow", "Dark Green", "Green", "Aqua", "Dark Aqua", "Dark Blue", "Blue", "Light Purple", "Dark Purple", "White", "Gray", "Dark Gray", "Black"})
    public int primaryColour = 12;

    @Expose
    @ConfigOption(name = "Secondary Colour", desc = "Change the secondary colour of GUI elements.")
    @ConfigEditorDropdown(values = {"Dark Red", "Red", "Gold", "Yellow", "Dark Green", "Green", "Aqua", "Dark Aqua", "Dark Blue", "Blue", "Light Purple", "Dark Purple", "White", "Gray", "Dark Gray", "Black"})
    public int secondaryColour = 6;

    @Expose
    @ConfigOption(name = "Primary Effect", desc = "Change the default effect of GUI elements.")
    @ConfigEditorDropdown(values = {"None", "Obfuscated", "Bold", "Strikethrough", "Underline", "Italic"})
    public int primaryEffect = 0;

    @Expose
    @ConfigOption(name = "Secondary Effect", desc = "Change the secondary effect of GUI elements.")
    @ConfigEditorDropdown(values = {"None", "Obfuscated", "Bold", "Strikethrough", "Underline", "Italic"})
    public int secondaryEffect = 2;

    @Expose
    @ConfigOption(name = "Padding", desc = "The number of pixels between lines in GUI elements.")
    @ConfigEditorSlider(minValue = 0, maxValue = 16, minStep = 1)
    public int paddingInPixels = 2;
}
