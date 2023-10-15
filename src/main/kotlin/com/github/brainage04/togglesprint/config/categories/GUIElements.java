package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
/*
    public enum AnchorCorner {
        TOPLEFT("Top Left"),
        TOPRIGHT("Top Right"),
        BOTTOMLEFT("Bottom Left"),
        BOTTOMRIGHT("Bottom Right");

        private final String text;

        AnchorCorner(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
 */

    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public GUIElement toggleSprintElement = new GUIElement(true, 10, 10, 2, new OtherSettings(0));

    @Expose
    @ConfigOption(name = "Player Position Tracker", desc = "")
    @Accordion
    public GUIElement playerPositionElement = new GUIElement(true, 10, 10, 0, new OtherSettings(1));

    @Expose
    @ConfigOption(name = "Player Motion Tracker", desc = "")
    @Accordion
    public GUIElement playerMotionElement = new GUIElement(true, 90, 10, 0, new OtherSettings(4));

    @Expose
    @ConfigOption(name = "Player Rotation Tracker", desc = "")
    @Accordion
    public GUIElement playerRotationElement = new GUIElement(true, 10, 90, 0, new OtherSettings(4));

    @Expose
    @ConfigOption(name = "Display Size Tracker", desc = "")
    @Accordion
    public GUIElement displaySizeElement = new GUIElement(true, 10, 10, 1, new OtherSettings(0));

    public static class GUIElement {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables rendering for the element.")
        @ConfigEditorBoolean()
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "X Coordinate", desc = "The X coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1_920 * 2, minStep = 1)
        public double x;

        @Expose
        @ConfigOption(name = "Y Coordinate", desc = "The Y coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 1_080 * 2, minStep = 1)
        public double y;

        @Expose
        @ConfigOption(name = "Anchor Corner", desc = "Aligns text to a corner of the screen.")
        @ConfigEditorDropdown(values = {"Top Left", "Top Right", "Bottom Left", "Bottom Right"})
        public int anchorCorner;

        @Expose
        @ConfigOption(name = "Other Settings", desc = "")
        @Accordion
        public OtherSettings otherSettings;

        public GUIElement(boolean isEnabled, double x, double y, int anchorCorner, OtherSettings otherSettings) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.anchorCorner = anchorCorner;
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
