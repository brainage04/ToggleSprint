package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public GUIElement toggleSprintElement = new GUIElement(true, 5, 3, 2);

    @Expose
    @ConfigOption(name = "Player Position Tracker", desc = "")
    @Accordion
    public PositionElement playerPositionElement = new PositionElement(new GUIElement(true, 10, 10, 0), 1);

    @Expose
    @ConfigOption(name = "Player Motion Tracker", desc = "")
    @Accordion
    public MotionElement playerMotionElement = new MotionElement(new GUIElement(true, 10, 10, 1), 4, false);

    @Expose
    @ConfigOption(name = "Player Rotation Tracker", desc = "")
    @Accordion
    public RotationElement playerRotationElement = new RotationElement(new GUIElement(true, 10, 50, 0), 2, false);

    public static class PositionElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElement guiElement;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        public PositionElement(GUIElement guiElement, int decimals) {
            this.guiElement = guiElement;
            this.decimals = decimals;
        }
    }

    public static class MotionElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElement guiElement;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show True Motion", desc = "Also shows the player's motion in blocks (metres) per tick (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueMotion;

        public MotionElement(GUIElement guiElement, int decimals, boolean showTrueMotion) {
            this.guiElement = guiElement;
            this.decimals = decimals;
            this.showTrueMotion = showTrueMotion;
        }
    }

    public static class RotationElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElement guiElement;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show True Yaw", desc = "Shows the player's true yaw value (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueYaw;

        public RotationElement(GUIElement guiElement, int decimals, boolean showTrueYaw) {
            this.guiElement = guiElement;
            this.decimals = decimals;
            this.showTrueYaw = showTrueYaw;
        }
    }

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

        public GUIElement(boolean isEnabled, int x, int y, int anchorCorner) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.anchorCorner = anchorCorner;
        }
    }
}
