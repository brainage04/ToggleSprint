package io.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.*;

/**
 * Element settings keep their defaults in field initialisers and have no-argument constructors, so
 * Gson leaves options missing from an existing config at their defaults.
 */
public class GUIElements {
    @Expose
    @ConfigOption(name = "Toggle Sprint HUD", desc = "")
    @Accordion
    public ToggleSprintElement toggleSprintElement = new ToggleSprintElement();

    @Expose
    @ConfigOption(name = "Position HUD", desc = "")
    @Accordion
    public PositionTracker positionTracker = new PositionTracker();

    @Expose
    @ConfigOption(name = "Motion HUD", desc = "")
    @Accordion
    public MotionTracker motionTracker = new MotionTracker();

    @Expose
    @ConfigOption(name = "Date/Time HUD", desc = "")
    @Accordion
    public RealTimeTracker realTimeTracker = new RealTimeTracker();

    @Expose
    @ConfigOption(name = "Network HUD", desc = "")
    @Accordion
    public NetworkTracker networkTracker = new NetworkTracker();

    @Expose
    @ConfigOption(name = "Entity HUD", desc = "")
    @Accordion
    public EntityTracker entityTracker = new EntityTracker();

    public static class ToggleSprintElement {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(true, 5, 5, 2);

        @Expose
        @ConfigOption(name = "Show Internal Values", desc = "Show internal values used for toggle sprint/sneak (useful for debugging purposes).")
        @ConfigEditorBoolean
        public boolean showInternalValues = false;
    }

    public static class PositionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(true, 5, 5, 0);

        @Expose
        @ConfigOption(name = "Show Position", desc = "Show the player's X, Y and Z coordinates.")
        @ConfigEditorBoolean
        public boolean showPosition = true;

        @Expose
        @ConfigOption(name = "Position Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int positionDecimalPlaces = 1;

        @Expose
        @ConfigOption(name = "Show Chunk Position", desc = "Show the player's block position within the current 16x16x16 chunk section.")
        @ConfigEditorBoolean
        public boolean showChunkPosition = true;

        @Expose
        @ConfigOption(name = "C Counter", desc = "Shows how many of the 16x16x16 chunk sections around the player are rendered, out of all of them, as on the F3 debug screen.")
        @ConfigEditorBoolean
        public boolean cCounter = true;

        @Expose
        @ConfigOption(name = "E Counter", desc = "Shows how many entities are rendered, out of all loaded ones, as on the F3 debug screen.")
        @ConfigEditorBoolean
        public boolean eCounter = true;

        @Expose
        @ConfigOption(name = "Show Direction", desc = "Show the direction that the player is facing.")
        @ConfigEditorBoolean
        public boolean showDirection = true;

        @Expose
        @ConfigOption(name = "Show Rotation", desc = "Show the player's yaw and pitch after the direction.")
        @ConfigEditorBoolean
        public boolean showRotation = true;

        @Expose
        @ConfigOption(name = "Show True Yaw", desc = "When your yaw has wound past ±180°, also show the raw value the game stores.")
        @ConfigEditorBoolean
        public boolean showTrueYaw = false;

        @Expose
        @ConfigOption(name = "Rotation Only With Farming Tool", desc = "Only show the rotation numbers while you hold an axe, a hoe or a Hypixel SkyBlock farming tool.")
        @ConfigEditorBoolean
        public boolean rotationOnlyWithFarmingTool = false;

        @Expose
        @ConfigOption(name = "Rotation Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int rotationDecimalPlaces = 2;

        @Expose
        @ConfigOption(name = "Show Light", desc = "Show sky and block light at the player's feet.")
        @ConfigEditorBoolean
        public boolean showLight = true;

        @Expose
        @ConfigOption(name = "Show Biome", desc = "Show the biome at the player's position.")
        @ConfigEditorBoolean
        public boolean showBiome = true;
    }

    public static class MotionTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(false, 5, 140, 0);

        @Expose
        @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int decimalPlaces = 2;

        @Expose
        @ConfigOption(name = "Show Axes", desc = "Show the speed along each of the X, Y and Z axes.")
        @ConfigEditorBoolean
        public boolean showAxes = true;

        @Expose
        @ConfigOption(name = "Show Horizontal Speed", desc = "Show the speed across the ground.")
        @ConfigEditorBoolean
        public boolean showHorizontalSpeed = true;

        @Expose
        @ConfigOption(name = "Show Blocks Per Tick", desc = "Also show the speed in blocks per tick.")
        @ConfigEditorBoolean
        public boolean showBlocksPerTick = false;
    }

    public static class RealTimeTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(true, 77, 5, 1);

        @Expose
        @ConfigOption(name = "Show Date", desc = "Shows your computer's date.")
        @ConfigEditorBoolean
        public boolean showDate = true;

        @Expose
        @ConfigOption(name = "Show Time", desc = "Shows your computer's time.")
        @ConfigEditorBoolean
        public boolean showTime = true;

        @Expose
        @ConfigOption(name = "Twelve Hour Format", desc = "Switches between 12 and 24 hour format.")
        @ConfigEditorBoolean
        public boolean twelveHourFormat = true;

        @Expose
        @ConfigOption(name = "Show Timezone", desc = "Shows your computer's timezone.")
        @ConfigEditorBoolean
        public boolean showTimezone = true;
    }

    public static class NetworkTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(true, 5, 116, 0);

        @Expose
        @ConfigOption(name = "Show Ping", desc = "Show your latency to the server, as the server reports it in the player list.")
        @ConfigEditorBoolean
        public boolean showPing = true;

        @Expose
        @ConfigOption(name = "Update Ping Tick Interval", desc = "How often the ping is sampled, in ticks.")
        @ConfigEditorSlider(minValue = 1, maxValue = 20, minStep = 1)
        public int updatePingTickInterval = 10;

        @Expose
        @ConfigOption(name = "Ping Intervals Tracked", desc = "How many ping samples the ping is averaged over.")
        @ConfigEditorSlider(minValue = 1, maxValue = 30, minStep = 1)
        public int pingIntervalsTracked = 3;

        @Expose
        @ConfigOption(name = "Show TPS", desc = "Show how many ticks per second the server is running at.")
        @ConfigEditorBoolean
        public boolean showTps = true;

        @Expose
        @ConfigOption(name = "Colour Values", desc = "Colour ping and TPS from dark green (good) to dark red (bad).")
        @ConfigEditorBoolean
        public boolean colourValues = true;

        @Expose
        @ConfigOption(name = "TPS Intervals Tracked", desc = "How many of the server's game time reports (sent about once a second) the TPS is averaged over.")
        @ConfigEditorSlider(minValue = 1, maxValue = 30, minStep = 1)
        public int tpsIntervalsTracked = 3;

        @Expose
        @ConfigOption(name = "TPS Decimal Places", desc = "The number of decimal places displayed.")
        @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int tpsDecimalPlaces = 1;
    }

    public static class EntityTracker {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public CoreSettings coreSettings = new CoreSettings(false, 5, 125, 1);

        @Expose
        @ConfigOption(name = "Show Creatures", desc = "Show non-hostile land mobs (animals) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showCreatures = true;

        @Expose
        @ConfigOption(name = "Show Water Creatures", desc = "Show non-hostile water mobs (marine life) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showWaterCreatures = true;

        @Expose
        @ConfigOption(name = "Show Ambient", desc = "Show ambient mobs (bats) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showAmbient = true;

        @Expose
        @ConfigOption(name = "Show Monsters", desc = "Show hostile mobs (monsters) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showMonsters = true;

        @Expose
        @ConfigOption(name = "Show Others", desc = "Show the count of non-categorised mobs (armour stands, items, etc.) in the list of loaded entities.")
        @ConfigEditorBoolean
        public boolean showOthers = true;
    }

    public static class CoreSettings {
        @Expose
        @ConfigOption(name = "Enabled", desc = "Enables rendering for the element.")
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
        @ConfigOption(name = "Element Anchor", desc = "Aligns text to a corner of the screen.")
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
