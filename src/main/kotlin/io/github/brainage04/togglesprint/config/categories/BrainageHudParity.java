package io.github.brainage04.togglesprint.config.categories;

import com.google.gson.annotations.Expose;
import io.github.moulberry.moulconfig.annotations.Accordion;
import io.github.moulberry.moulconfig.annotations.ConfigEditorBoolean;
import io.github.moulberry.moulconfig.annotations.ConfigEditorDropdown;
import io.github.moulberry.moulconfig.annotations.ConfigEditorSlider;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import io.github.moulberry.moulconfig.annotations.ConfigEditorColour;
import io.github.moulberry.moulconfig.annotations.ConfigEditorText;

import java.util.ArrayList;
import java.util.List;

public class BrainageHudParity {
    @Expose
    @ConfigOption(name = "Fishing HUD", desc = "Shows whether the active cast can catch treasure.")
    @Accordion
    public Fishing fishing = new Fishing();

    @Expose
    @ConfigOption(name = "Performance HUD", desc = "Shows client performance measurements.")
    @Accordion
    public Performance performance = new Performance();

    @Expose
    @ConfigOption(name = "Reach HUD", desc = "Shows information about the targeted block or entity.")
    @Accordion
    public Reach reach = new Reach();

    @Expose
    @ConfigOption(name = "Keystrokes HUD", desc = "Shows movement, mouse, and clicks-per-second state.")
    @Accordion
    public Keystrokes keystrokes = new Keystrokes();

    @Expose
    @ConfigOption(name = "Fullbright", desc = "Overrides the dimension's ambient light. Set to 0 to disable.")
    @ConfigEditorSlider(minValue = -1, maxValue = 1, minStep = 0.01F)
    public float fullbright = 0.0F;

    @Expose
    @ConfigOption(name = "Waypoints", desc = "Draws your waypoints in the world. Manage them with the Manage Waypoints key or /waypoints.")
    @Accordion
    public Waypoints waypoints = new Waypoints();

    @Expose
    @ConfigOption(name = "Enchant Info HUD", desc = "Shows the held item's enchantments and the enchantments it could still get.")
    @Accordion
    public EnchantInfoHud enchantInfoHud = new EnchantInfoHud();

    @Expose
    @ConfigOption(name = "Enchant Info", desc = "Tooltip highlighting and the enchantment blacklist used by the Enchant Info HUD and /getenchants.")
    @Accordion
    public EnchantInfo enchantInfo = new EnchantInfo();

    @Expose
    @ConfigOption(name = "Status Effect HUD", desc = "Shows your active status effects.")
    @Accordion
    public StatusEffectHud statusEffectHud = new StatusEffectHud();

    public static class Fishing {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(false, 0, -120, 8);
    }

    public static class EnchantInfoHud {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 150, 5, 0);

        @Expose @ConfigOption(name = "Show Item Name", desc = "") @ConfigEditorBoolean
        public boolean showItemName = true;
        @Expose @ConfigOption(name = "Show Enchantments", desc = "Lists the enchantments already on the item.") @ConfigEditorBoolean
        public boolean showEnchantments = true;
        @Expose @ConfigOption(name = "Show Max Levels", desc = "Shows the maximum level after enchantments below it.") @ConfigEditorBoolean
        public boolean showMaxLevels = true;
        @Expose @ConfigOption(name = "Show Missing Enchantments", desc = "Lists the enchantments the item could still get.") @ConfigEditorBoolean
        public boolean showMissingEnchantments = true;
        @Expose @ConfigOption(name = "Show Missing Header", desc = "Shows a \"Missing:\" line above the missing enchantments.") @ConfigEditorBoolean
        public boolean showMissingHeader = true;
    }

    public static class EnchantInfo {
        @Expose @ConfigOption(name = "Highlight Max Level Enchants", desc = "Makes enchantments at their maximum level bold in item tooltips.") @ConfigEditorBoolean
        public boolean highlightMaxLevelEnchants = true;

        /** Comma-separated enchantment IDs (e.g. {@code minecraft:smite}) never listed as missing; also edited with /blacklistedenchants. */
        @Expose
        @ConfigOption(name = "Blacklisted Enchantment IDs", desc = "Comma-separated enchantment IDs (e.g. minecraft:smite) that are never listed as missing. Also edited with /blacklistedenchants.")
        @ConfigEditorText
        public String blacklistedEnchantmentIds = "minecraft:blast_protection, minecraft:projectile_protection, minecraft:fire_protection, "
                + "minecraft:thorns, minecraft:bane_of_arthropods, minecraft:smite, minecraft:knockback";
    }

    public static class StatusEffectHud {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 0, 4);

        @Expose @ConfigOption(name = "Show Durations", desc = "Also show how long each effect has left.") @ConfigEditorBoolean
        public boolean showDurations = true;

        @Expose @ConfigOption(name = "Show Icons", desc = "Draw each effect's icon, with its name and duration on two lines beside it.") @ConfigEditorBoolean
        public boolean showIcons = true;
    }

    public static class Performance {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 99, 0);

        @Expose @ConfigOption(name = "Show FPS", desc = "") @ConfigEditorBoolean
        public boolean showFps = true;
        @Expose @ConfigOption(name = "Show RAM Usage", desc = "") @ConfigEditorBoolean
        public boolean showRamUsage = false;
        @Expose @ConfigOption(name = "Show CPU Usage", desc = "") @ConfigEditorBoolean
        public boolean showCpuUsage = false;
        @Expose @ConfigOption(name = "Show GPU Usage", desc = "The share of each frame's time that the GPU spends drawing it.") @ConfigEditorBoolean
        public boolean showGpuUsage = false;
        @Expose @ConfigOption(name = "Show GPU Frame Time", desc = "How long the GPU takes to draw each frame, in milliseconds.") @ConfigEditorBoolean
        public boolean showGpuFrameTime = false;
    }

    public static class Reach {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 0, -60, 8);

        @Expose @ConfigOption(name = "Decimal Places", desc = "The number of decimal places displayed.") @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
        public int decimalPlaces = 2;
        @Expose @ConfigOption(name = "Show Name", desc = "") @ConfigEditorBoolean
        public boolean showName = true;
        @Expose @ConfigOption(name = "Show Coordinates", desc = "") @ConfigEditorBoolean
        public boolean showCoordinates = true;
        @Expose @ConfigOption(name = "Update On Attack Click", desc = "Refresh only when attack changes from released to pressed.") @ConfigEditorBoolean
        public boolean updateOnAttackClick = false;
    }

    public static class Keystrokes {
        @Expose
        @ConfigOption(name = "Core Settings", desc = "")
        @Accordion
        public GUIElements.CoreSettings coreSettings = new GUIElements.CoreSettings(true, 5, 5, 1);

        @Expose @ConfigOption(name = "Key Backdrop Opacity", desc = "") @ConfigEditorSlider(minValue = 0, maxValue = 255, minStep = 1)
        public int keyBackdropOpacity = 100;
        @Expose @ConfigOption(name = "Show WASD", desc = "") @ConfigEditorBoolean
        public boolean showWasd = true;
        @Expose @ConfigOption(name = "Show Space", desc = "") @ConfigEditorBoolean
        public boolean showSpace = true;
        @Expose @ConfigOption(name = "Show Mouse Buttons", desc = "") @ConfigEditorBoolean
        public boolean showMouseButtons = true;
        @Expose @ConfigOption(name = "Clicks Per Second Format", desc = "") @ConfigEditorDropdown(values = {"None", "Left Click", "Right Click", "Both"})
        public int clicksPerSecondFormat = 1;
    }

    public static class Waypoints {
        @Expose @ConfigOption(name = "Show In World", desc = "Draws the current dimension's visible waypoints in the world.") @ConfigEditorBoolean
        public boolean showInWorld = true;
        @Expose @ConfigOption(name = "Show World Centre", desc = "A built-in waypoint at 0, 63, 0 in every dimension.") @ConfigEditorBoolean
        public boolean showWorldCentre = true;
        @Expose @ConfigOption(name = "World Centre Colour", desc = "") @ConfigEditorColour
        public String worldCentreColour = "0:255:255:255:255";
        @Expose @ConfigOption(name = "Show Beams", desc = "A beam through the whole height of the world, visible from far away.") @ConfigEditorBoolean
        public boolean showBeams = true;
        @Expose @ConfigOption(name = "Show Markers", desc = "A floating, spinning gem above the waypoint, with a pulse on the ground below it when near.") @ConfigEditorBoolean
        public boolean showMarkers = true;
        @Expose @ConfigOption(name = "Show Labels", desc = "The name and distance above the waypoint, readable through walls.") @ConfigEditorBoolean
        public boolean showLabels = true;
        @Expose @ConfigOption(name = "Always Show Names", desc = "Shows every waypoint's name. Otherwise only the waypoint you look towards and waypoints nearby show their names; the rest show just their distance.") @ConfigEditorBoolean
        public boolean alwaysShowNames = false;
        @Expose @ConfigOption(name = "Label Scale Percent", desc = "In percent.") @ConfigEditorSlider(minValue = 50, maxValue = 200, minStep = 1)
        public int labelScalePercent = 100;

        /** The old waypoint list, which had no world. Moved into the first world joined, then emptied. */
        @Expose
        public List<Waypoint> entries = new ArrayList<>();
    }

    /** A waypoint of the old list; see {@link Waypoints#entries}. */
    public static class Waypoint {
        @Expose public String name = "Waypoint";
        @Expose public double x;
        @Expose public double y;
        @Expose public double z;
        @Expose public int dimension;
    }
}
