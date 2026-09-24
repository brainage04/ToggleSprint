package com.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint/Sneak", desc = "")
    @Accordion
    public ToggleSprintElement toggleSprintElement = new ToggleSprintElement(new CoreSettings(true, 5, 3, 2), false);

    @Expose
    @ConfigOption(name = "Position Display", desc = "")
    @Accordion
    public PositionTracker positionTracker = new PositionTracker(new CoreSettings(true, 10, 10, 0), 1, true, true, true, true, true, true);

    @Expose
    @ConfigOption(name = "Rotation Display", desc = "")
    @Accordion
    public RotationTracker rotationTracker = new RotationTracker(new CoreSettings(true, 10, 120, 0), 2, false, false, false);

    @Expose
    @ConfigOption(name = "Motion Display", desc = "")
    @Accordion
    public MotionTracker motionTracker = new MotionTracker(new CoreSettings(false, 80, 10, 0), 2, false);

    @Expose
    @ConfigOption(name = "Date/Time Display", desc = "")
    @Accordion
    public RealTimeTracker realTimeTracker = new RealTimeTracker(new CoreSettings(true, 10, 10, 1), 1, true, true);

    @Expose
    @ConfigOption(name = "Ping", desc = "")
    @Accordion
    public LagTracker pingTracker = new LagTracker(new CoreSettings(true, 10, 80, 0), true);

    @Expose
    @ConfigOption(name = "TPS", desc = "")
    @Accordion
    public TpsTracker tpsTracker = new TpsTracker(new CoreSettings(false, 10, 100, 0), true);

    @Expose
    @ConfigOption(name = "Entity Tracker", desc = "")
    @Accordion
    public EntityTracker entityTracker = new EntityTracker(new CoreSettings(false, 130, 10, 1), true, true, true, true, true);

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

    public static class ToggleSprintElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Internal Values", desc = "Show internal values used for toggle sprint/sneak (useful for debugging purposes).")
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
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Show Facing", desc = "Show the direction that the player is facing.")
        @ConfigEditorBoolean
        public boolean showFacing;

        @Expose
        @ConfigOption(name = "C Counter", desc = "Shows the number of loaded cubic chunks (16x16x16) containing air blocks (excluding 0% and 100% air chunks) within the player's viewport.")
        @ConfigEditorBoolean
        public boolean showChunkCounter;

        @Expose
        @ConfigOption(name = "E Counter", desc = "Shows the number of rendered entities within the player's viewport (even through walls, unless you are culling them using a mod such as Patcher).")
        @ConfigEditorBoolean
        public boolean showEntityCounter;

        @Expose
        @ConfigOption(name = "Position Within Chunk", desc = "Show the player's block position within the current 16x16x16 chunk section.")
        @ConfigEditorBoolean
        public boolean showChunkPosition;

        @Expose
        @ConfigOption(name = "Light Levels", desc = "Show sky and block light at the player's feet.")
        @ConfigEditorBoolean
        public boolean showLightLevels;

        @Expose
        @ConfigOption(name = "Biome", desc = "Show the biome at the player's position.")
        @ConfigEditorBoolean
        public boolean showBiome;

        public PositionTracker(CoreSettings coreSettings, int decimals, boolean showFacing, boolean showChunkCounter, boolean showEntityCounter,
                               boolean showChunkPosition, boolean showLightLevels, boolean showBiome) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.showFacing = showFacing;
            this.showChunkCounter = showChunkCounter;
            this.showEntityCounter = showEntityCounter;
            this.showChunkPosition = showChunkPosition;
            this.showLightLevels = showLightLevels;
            this.showBiome = showBiome;
        }
    }

    public static class RotationTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int decimals;

        @Expose
        @ConfigOption(name = "Compact Format", desc = "Compacts the format to match that of the F3 debug menu.")
        @ConfigEditorBoolean
        public boolean compactFormat;

        @Expose
        @ConfigOption(name = "Show True Yaw", desc = "Shows the player's true yaw value (If different from the clamped value. Useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showTrueYaw;

        @Expose
        @ConfigOption(name = "Only Show with Farming Tool", desc = "Only shows when the player is holding a farming tool. Compatible with Hypixel Skyblock farming equipment.")
        @ConfigEditorBoolean
        public boolean dependOnFarmingTool;

        public RotationTracker(CoreSettings coreSettings, int decimals, boolean compactFormat, boolean showTrueYaw,  boolean dependOnFarmingTool) {
            this.coreSettings = coreSettings;
            this.decimals = decimals;
            this.compactFormat = compactFormat;
            this.showTrueYaw = showTrueYaw;
            this.dependOnFarmingTool = dependOnFarmingTool;
        }
    }

    public static class MotionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
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

    public static class RealTimeTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Time Format", desc = "Switches between 12 and 24 hour format. Select \"None\" to remove the time from this element.")
        @ConfigEditorDropdown(values = {"12-hour", "24-hour", "None"})
        public int timeFormat;

        @Expose
        @ConfigOption(name = "Include Date", desc = "Appends your computer's date to the start of the element.")
        @ConfigEditorBoolean
        public boolean includeDate;

        @Expose
        @ConfigOption(name = "Include Timezone", desc = "Appends your computer's timezone to the end of the element.")
        @ConfigEditorBoolean
        public boolean includeTimezone;

        public RealTimeTracker(CoreSettings coreSettings, int timeFormat, boolean includeDate, boolean includeTimezone) {
            this.coreSettings = coreSettings;
            this.timeFormat = timeFormat;
            this.includeDate = includeDate;
            this.includeTimezone = includeTimezone;
        }
    }

    public static class LagTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Colour", desc = "Colour the number based on how good or bad it is (green = good, red = bad).")
        @ConfigEditorBoolean
        public boolean showColor;

        public LagTracker(CoreSettings coreSettings, boolean showColor) {
            this.coreSettings = coreSettings;
            this.showColor = showColor;
        }
    }

    public static class TpsTracker extends LagTracker {
        @Expose
        @ConfigOption(name = "Intervals Tracked", desc = "How many of the server's game time reports (sent about once a second) the TPS is averaged over.")
        @ConfigEditorSlider(minValue = 1, maxValue = 30, minStep = 1)
        public int intervalsTracked = 3;

        /** Used by Gson so that fields added later keep their defaults in existing configs. */
        public TpsTracker() {
            super(null, true);
        }

        public TpsTracker(CoreSettings coreSettings, boolean showColor) {
            super(coreSettings, showColor);
        }
    }

    public static class EntityTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings;

        @Expose
        @ConfigOption(name = "Show Creatures", desc = "Show non-hostile land mobs (animals) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showCreatures;

        @Expose
        @ConfigOption(name = "Show Water Creatures", desc = "Show non-hostile water mobs (marine life) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showWaterCreatures;

        @Expose
        @ConfigOption(name = "Show Ambients", desc = "Show ambient mobs (bats) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showAmbients;

        @Expose
        @ConfigOption(name = "Show Monsters", desc = "Show hostile mobs (monsters) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showMonsters;

        @Expose
        @ConfigOption(name = "Show Others", desc = "Show the count of non-categorised mobs (armour stands, items, etc.) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showOthers;

        public EntityTracker(CoreSettings coreSettings, boolean showCreatures, boolean showWaterCreatures, boolean showAmbients, boolean showMonsters, boolean showOthers) {
            this.coreSettings = coreSettings;
            this.showCreatures = showCreatures;
            this.showWaterCreatures = showWaterCreatures;
            this.showAmbients = showAmbients;
            this.showMonsters = showMonsters;
            this.showOthers = showOthers;
        }
    }

    public static class CoreSettings {
        @Expose
        @ConfigOption(name = "Is Enabled", desc = "Enables rendering for the element.")
        @ConfigEditorBoolean()
        public boolean isEnabled;

        @Expose
        @ConfigOption(name = "X Coordinate", desc = "The X coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = -1_920, maxValue = 1_920, minStep = 1)
        public double x;

        @Expose
        @ConfigOption(name = "Y Coordinate", desc = "The Y coordinate of the GUI element.")
        @ConfigEditorSlider(minValue = -1_080, maxValue = 1_080, minStep = 1)
        public double y;

        @Expose
        @ConfigOption(name = "Anchor Corner", desc = "Aligns text to a corner of the screen.")
        @ConfigEditorDropdown(values = {"Top Left", "Top Right", "Bottom Left", "Bottom Right", "Centre Left", "Centre Right", "Centre Top", "Centre Bottom", "Centre"})
        public int anchorCorner;

        @Expose
        @ConfigOption(name = "Style Overrides", desc = "Replace the global GUI style settings for this element only.")
        @Accordion
        public ElementOverrides elementOverrides = new ElementOverrides();

        /** Used by Gson so that fields added later keep their defaults in existing configs. */
        public CoreSettings() {
        }

        public CoreSettings(boolean isEnabled, int x, int y, int anchorCorner) {
            this.isEnabled = isEnabled;
            this.x = x;
            this.y = y;
            this.anchorCorner = anchorCorner;
        }
    }

    /** Per-element replacements for the global GUI style; each value applies only while its toggle is on. */
    public static class ElementOverrides {
        @Expose
        @ConfigOption(name = "Override Text Colour", desc = "Use the text colour below instead of the global one.")
        @ConfigEditorBoolean
        public boolean overrideTextColour = false;

        @Expose
        @ConfigOption(name = "Text Colour", desc = "The colour of text without its own colour code.")
        @ConfigEditorColour
        public String textColour = "0:255:255:255:255";

        @Expose
        @ConfigOption(name = "Override Text Shadows", desc = "Use the text shadow setting below instead of the global one.")
        @ConfigEditorBoolean
        public boolean overrideTextShadows = false;

        @Expose
        @ConfigOption(name = "Text Shadows", desc = "Draw a shadow behind the text.")
        @ConfigEditorBoolean
        public boolean textShadows = true;

        @Expose
        @ConfigOption(name = "Override Backdrop Opacity", desc = "Use the backdrop opacity below instead of the global one.")
        @ConfigEditorBoolean
        public boolean overrideBackdropOpacity = false;

        @Expose
        @ConfigOption(name = "Backdrop Opacity", desc = "The opacity of the dark backdrop behind the element (0 = no backdrop).")
        @ConfigEditorSlider(minValue = 0, maxValue = 255, minStep = 1)
        public int backdropOpacity = 100;

        @Expose
        @ConfigOption(name = "Override Padding", desc = "Use the padding below instead of the global one.")
        @ConfigEditorBoolean
        public boolean overridePadding = false;

        @Expose
        @ConfigOption(name = "Padding", desc = "The number of pixels between lines and around the element.")
        @ConfigEditorSlider(minValue = 0, maxValue = 16, minStep = 1)
        public int padding = 2;

        @Expose
        @ConfigOption(name = "Override Max Width", desc = "Use the maximum width below instead of the global one.")
        @ConfigEditorBoolean
        public boolean overrideMaxWidth = false;

        @Expose
        @ConfigOption(name = "Max Width", desc = "Lines wider than this many pixels wrap onto the next line (0 = no limit).")
        @ConfigEditorSlider(minValue = 0, maxValue = 1_000, minStep = 1)
        public int maxWidth = 0;
    }
}
