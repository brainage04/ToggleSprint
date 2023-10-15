package com.github.brainage04.togglesprint.config.categories;

import com.github.brainage04.togglesprint.gui.core.RenderGuiData;
import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;
import io.github.moulberry.moulconfig.observer.Property;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public GUIElement toggleSprintElement = new GUIElement(true, 10, 10, Property.of(RenderGuiData.DisplayAnchor.BOTTOMLEFT), new OtherSettings(0));

    @Expose
    @ConfigOption(name = "Player Position Tracker", desc = "")
    @Accordion
    public GUIElement playerPositionElement = new GUIElement(true, 10, 10, Property.of(RenderGuiData.DisplayAnchor.TOPLEFT), new OtherSettings(1));

    @Expose
    @ConfigOption(name = "Player Motion Tracker", desc = "")
    @Accordion
    public GUIElement playerMotionElement = new GUIElement(true, 90, 10, Property.of(RenderGuiData.DisplayAnchor.TOPLEFT), new OtherSettings(4));

    @Expose
    @ConfigOption(name = "Player Rotation Tracker", desc = "")
    @Accordion
    public GUIElement playerRotationElement = new GUIElement(true, 10, 90, Property.of(RenderGuiData.DisplayAnchor.TOPLEFT), new OtherSettings(4));

    public static class GUIElement {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables rendering for the element.")
        @ConfigEditorBoolean()
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "X Coordinate", desc = "The X coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1920 * 2, minStep = 1)
        public double x;

        @Expose
        @ConfigOption(name = "Y Coordinate", desc = "The Y coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1080 * 2, minStep = 1)
        public double y;

        @Expose
        @ConfigOption(name = "Horizontal Alignment", desc = "Aligns text to a corner of the screen.")
        @ConfigEditorDropdown()
        public Property<RenderGuiData.DisplayAnchor> displayAnchor;

        @Expose
        @ConfigOption(name = "Other Settings", desc = "")
        @Accordion
        public OtherSettings otherSettings;

        public GUIElement(boolean isEnabled, double x, double y, Property<RenderGuiData.DisplayAnchor> displayAnchor, OtherSettings otherSettings) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.displayAnchor = displayAnchor;
            this.otherSettings = otherSettings;
        }
    }

    public static class OtherSettings {
        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed (if applicable).")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        OtherSettings(int decimals) {
            this.decimals = decimals;
        }
    }
}
