package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public GUIElement toggleSprintElement = new GUIElement(new CoreSettings(true, 10, 10, 2));

    @Expose
    @ConfigOption(name = "Player Position Tracker", desc = "")
    @Accordion
    public DecimalElement playerPositionElement = new DecimalElement(new CoreSettings(true, 10, 10, 0), 1);

    @Expose
    @ConfigOption(name = "Player Motion Tracker", desc = "")
    @Accordion
    public DecimalElement playerMotionElement = new DecimalElement(new CoreSettings(true, 90, 10, 0), 4);

    @Expose
    @ConfigOption(name = "Player Rotation Tracker", desc = "")
    @Accordion
    public RotationElement playerRotationElement = new RotationElement(new CoreSettings(true, 10, 90, 0), 2, false);

    /*

     */

    public static class GUIElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        public GUIElement(CoreSettings coreSettings) {
            this.coreSettings = coreSettings;
        }
    }

    public static class DecimalElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        public DecimalElement(CoreSettings coreSettings, int decimals) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
        }
    }

    public static class RotationElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show True Yaw", desc = "Shows the player's true yaw value (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueYaw;

        public RotationElement(CoreSettings coreSettings, int decimals, boolean showTrueYaw) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.showTrueYaw = showTrueYaw;
        }
    }

    public static class CoreSettings {
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

        public CoreSettings(boolean isEnabled, int x, int y, int anchorCorner) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.anchorCorner = anchorCorner;
        }
    }
}
