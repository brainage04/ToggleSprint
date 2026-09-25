package io.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

/** Style defaults for every GUI element; an element's Style Overrides replace individual values. */
public class GlobalGUISettings {
    @Expose
    @ConfigOption(name = "Text Colour", desc = "The colour of text without its own colour code.")
    @ConfigEditorColour
    public String textColour = "0:255:255:255:255";

    @Expose
    @ConfigOption(name = "Text Shadows", desc = "Draw a shadow behind the text.")
    @ConfigEditorBoolean
    public boolean textShadows = true;

    @Expose
    @ConfigOption(name = "Backdrop Opacity", desc = "The opacity of the dark backdrop behind each element (0 = no backdrop).")
    @ConfigEditorSlider(minValue = 0, maxValue = 255, minStep = 1)
    public int backdropOpacity = 0;

    @Expose
    @ConfigOption(name = "Padding", desc = "The number of pixels between lines and around each element.")
    @ConfigEditorSlider(minValue = 0, maxValue = 16, minStep = 1)
    public int paddingInPixels = 2;

    @Expose
    @ConfigOption(name = "Max Element Width", desc = "Lines wider than this many pixels wrap onto the next line (0 = no limit).")
    @ConfigEditorSlider(minValue = 0, maxValue = 1_000, minStep = 1)
    public int maxElementWidth = 0;

    @Expose
    @ConfigOption(name = "Screen Margin", desc = "The closest the element editor lets an element get to the edge of the screen, in pixels.")
    @ConfigEditorSlider(minValue = 0, maxValue = 50, minStep = 1)
    public int screenMargin = 5;
}
