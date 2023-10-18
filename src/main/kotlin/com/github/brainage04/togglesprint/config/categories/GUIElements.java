package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public ToggleSprintElement toggleSprintElement = new ToggleSprintElement(new CoreSettings(true, 5, 3, 2), false);

    @Expose
    @ConfigOption(name = "Position Tracker", desc = "")
    @Accordion
    public PositionTracker positionTracker = new PositionTracker(new CoreSettings(true, 10, 10, 0), 1);

    @Expose
    @ConfigOption(name = "Motion Tracker", desc = "")
    @Accordion
    public MotionTracker motionTracker = new MotionTracker(new CoreSettings(true, 10, 10, 1), 4, false);

    @Expose
    @ConfigOption(name = "Rotation Tracker", desc = "")
    @Accordion
    public RotationTracker rotationTracker = new RotationTracker(new CoreSettings(true, 10, 50, 0), 2, false);

    @Expose
    @ConfigOption(name = "Entity Tracker", desc = "")
    @Accordion
    public EntityTracker entityTracker = new EntityTracker(new CoreSettings(true, 10, 10, 3), true, false, false, false, true);

    @Expose
    @ConfigOption(name = "Real Time Tracker", desc = "")
    @Accordion
    public RealTimeTracker realTimeTracker = new RealTimeTracker(new CoreSettings(true, 10, 50, 1), 1, true, true);

    @Expose
    @ConfigOption(name = "TPS Tracker", desc = "")
    @Accordion
    public TPSTracker tpsTracker = new TPSTracker(new CoreSettings(true, 10, 90, 0), 1, true);

    @Expose
    @ConfigOption(name = "Ping Tracker", desc = "")
    @Accordion
    public PingTracker pingTracker = new PingTracker(new CoreSettings(true, 10, 120, 0));

/*
    public static class ExampleElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        public ExampleElement(CoreSettings coreSettings) {
            this.coreSettings = coreSettings;
        }
    }
 */

    public static class PingTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        public PingTracker(CoreSettings coreSettings) {
            this.coreSettings = coreSettings;
        }
    }

    public static class TPSTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Range (Seconds)", desc = "The number of seconds worth of ticks used to calculate the average TPS.")
        @ConfigEditorSlider(minValue = 1, maxValue = 10, minStep = 1)
        public int rangeInSeconds;

        @Expose
        @ConfigOption(name = "Show Difference", desc = "Show the difference in milliseconds for the specified tick range.")
        @ConfigEditorBoolean
        public boolean showDifference;

        public TPSTracker(CoreSettings coreSettings, int rangeInSeconds, boolean showDifference) {
            this.coreSettings = coreSettings;
            this.rangeInSeconds = rangeInSeconds;
            this.showDifference = showDifference;
        }
    }

    public static class RealTimeTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "24 Hour Format", desc = "Switches to 24-hour format instead of 12.")
        @ConfigEditorDropdown(values = {"None", "12-hour", "24-hour"})
        public int dateFormat;

        @Expose
        @ConfigOption(name = "Include Date", desc = "Appends today's date to the end of the element.")
        @ConfigEditorBoolean
        public boolean includeDate;

        @Expose
        @ConfigOption(name = "Include Timezone", desc = "Appends your computer's timezone to the end of the element.")
        @ConfigEditorBoolean
        public boolean includeTimezone;

        public RealTimeTracker(CoreSettings coreSettings, int dateFormat, boolean includeDate, boolean includeTimezone) {
            this.coreSettings = coreSettings;
            this.dateFormat = dateFormat;
            this.includeDate = includeDate;
            this.includeTimezone = includeTimezone;
        }
    }

    public static class EntityTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Players", desc = "Show players (and their names) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showPlayers;

        @Expose
        @ConfigOption(name = "Show Creatures", desc = "Show non-hostile land mobs in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showCreatures;

        @Expose
        @ConfigOption(name = "Show Water Creatures", desc = "Show non-hostile water mobs in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showWaterCreatures;

        @Expose
        @ConfigOption(name = "Show Ambients", desc = "Show ambient mobs (bats) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showAmbients;

        @Expose
        @ConfigOption(name = "Show Monsters", desc = "Show hostile mobs in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showMonsters;

        public EntityTracker(CoreSettings coreSettings, boolean showPlayers, boolean showCreatures, boolean showWaterCreatures, boolean showAmbients, boolean showMonsters) {
            this.coreSettings = coreSettings;
            this.showPlayers = showPlayers;
            this.showCreatures = showCreatures;
            this.showWaterCreatures = showWaterCreatures;
            this.showAmbients = showAmbients;
            this.showMonsters = showMonsters;
        }
    }

    public static class ToggleSprintElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Internal Values", desc = "Show internal values for toggleSprint and toggleSneak (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showInternalValues;

        public ToggleSprintElement(CoreSettings coreSettings, boolean showInternalValues) {
            this.coreSettings = coreSettings;
            this.showInternalValues = showInternalValues;
        }
    }

    public static class PositionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        public PositionTracker(CoreSettings coreSettings, int decimals) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
        }
    }

    public static class MotionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Points", desc = "The number of decimal points displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show True Motion", desc = "Also shows the player's motion in blocks (metres) per tick (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueMotion;

        public MotionTracker(CoreSettings coreSettings, int decimals, boolean showTrueMotion) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.showTrueMotion = showTrueMotion;
        }
    }

    public static class RotationTracker {
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

        public RotationTracker(CoreSettings coreSettings, int decimals, boolean showTrueYaw) {
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
