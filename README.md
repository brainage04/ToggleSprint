# Toggle Sprint

## Overview
Toggle Sprint is a client-side Forge mod for Minecraft 1.8.9 that adds toggle sprint and toggle sneak, useful heads-up display (HUD) elements, waypoints and enchantment tools. It is made to be allowed on Hypixel. It is the 1.8.9 counterpart of [BrainageHUD](https://github.com/brainage04/BrainageHUD), and the two have the same features where the game version allows.

## HUD elements
- **Position HUD**: block position, position within the chunk, the C and E counters, the direction you face (including +/- X/Z), rotation, light levels and biome. Rotation can include the unwrapped "true" yaw, or show only while you hold an axe, a hoe or a Hypixel SkyBlock farming tool.
- **Network HUD**: ping and TPS, each of which can be turned off, optionally coloured from green (good) to red (bad). TPS is measured from the game time the server reports each second.
- **Performance HUD**: FPS, RAM usage, CPU usage, GPU usage and GPU frame time.
- **Date/Time HUD**: date, time (12 or 24 hour) and timezone.
- **Motion HUD**: your speed on each axis and your horizontal speed in blocks per second, optionally also in blocks per tick. It measures how far you actually moved, so it stays accurate when you are blocked or riding something.
- **Entity HUD**: how many entities are loaded, grouped into creatures, water creatures, ambient mobs, monsters and others.
- **Projectile HUD**: how many arrows, snowballs, eggs and ender pearls you carry, optionally per slot.
- **Food HUD**: how much of each food you carry, optionally per slot.
- **Reach HUD**: the name, distance and coordinates of the block or entity you are looking at.
- **Toggle Sprint HUD**: whether you are walking, sneaking or sprinting, and whether that is vanilla or toggled. It can also show the toggle setting and the key state, for debugging.
- **Armour Info HUD**: durability, names or icons of your armour and the held item.
- **Keystrokes HUD**: WASD, Space and the mouse buttons as they are pressed, and your clicks per second.
- **Enchant Info HUD**: the held item's enchantments and the enchantments it could still get (see [Enchant Info](#enchant-info)).
- **Status Effect HUD**: your active status effects with their icons, levels and how long each has left.
- **Fishing HUD**: while your bobber is out, the chance of catching a fish, treasure or junk with the rod you hold.

Motion, Entity and Fishing are off by default; turn them on in the config editor. Every element can be configured to show as little or as much as you like.

## Waypoints
Waypoints are saved per world (singleplayer save or server address) in `config/togglesprint/waypoints.json`. Each visible waypoint in your current dimension is drawn in the world, in its own colour:

- A beacon beam through the whole height of the world, which widens with distance so it can be found from far away.
- A spinning gem hovering above the spot, with a pulse spreading over the ground below it when you are near.
- A label with the name and distance, readable through walls. It keeps its size on screen however far away the waypoint is. Beyond 16 blocks, only the waypoint you look towards shows its name; the others show their distance.

Every dimension also has a built-in "World Centre" waypoint at 0, 63, 0, white by default. It is listed first on the Manage Waypoints screen, where it can be hidden or shown but not edited or deleted. Its colour is World Centre Colour in the config.

Each part can be turned off, and the label resized, under Waypoints in the config editor.

Waypoint names can be up to 64 characters and can't contain formatting codes, quotes or control characters.

## Enchant Info
The Enchant Info HUD shows the held item's name, its enchantments (with the maximum level after any enchantment below it), then a "Missing:" list of the enchantments it could still get. Enchantments that cannot be combined share one line, e.g. `Fortune III / Silk Touch`. Blacklisted enchantments are never listed as missing; by default the blacklist holds the enchantments that are usually a worse choice (such as Blast Protection, Smite and Knockback). Each part can be turned off under Enchant Info HUD in the config editor.

Tooltip lines of enchantments at their maximum level are shown in bold (Enchant Info > Highlight Max Level Enchants).

## Commands
- `/waypoints add <name> [<x> <y> <z>]`, `/waypoints remove <name>` and `/waypoints list` add, remove and list waypoints; `/waypoints` opens the Manage Waypoints screen. Quote names that contain spaces.
- `/getenchants [<item>]` lists every enchantment the held item (or the given item) can get, grouping enchantments that conflict with each other.
- `/getenchantinfo <enchantment>` shows an enchantment's ID, maximum level, conflicts and the items it applies to. It accepts an ID (`minecraft:fortune` or `fortune`) or a name, ignoring case; a partial name lists every match.
- `/blacklistedenchants add <enchantment>`, `/blacklistedenchants remove <enchantment>` and `/blacklistedenchants query` edit and show the Enchant Info blacklist, which is also editable in the config editor.
- `/fullbright <amount>` sets the ambient light from -1 to 1; 0 turns it off.
- `/togglesprint` opens the config editor, and `/togglesprint gui` opens the HUD element editor.

## Controls
| Key | Default | Action |
|---|---|---|
| Toggle Sprint | Right Ctrl | Turns toggle sprint on or off. |
| Toggle Sneak | Right Shift | Turns toggle sneak on or off. |
| Open Config | Enter | Opens the Toggle Sprint config editor. |
| Open Element Editor | Numpad Plus | Opens the HUD element editor. |
| Create Waypoint | B | Creates a waypoint where you stand, named "Waypoint 1", "Waypoint 2", and so on. |
| Manage Waypoints | U | Opens a screen to add, edit, hide/show and delete waypoints. |
| Inventory Stats | Unbound | Lists every occupied inventory slot in chat with its item, count and item data (damage, enchantments, custom name and so on). |

All keys are in the Toggle Sprint category of the controls screen. Each toggle key can be turned off, and given a default state for when the game starts, in the config editor.

## Configuration and element editor
The config editor opens with the Open Config key, `/togglesprint`, or the Config button in Forge's Mods list. Global GUI Settings sets the text colour, text shadows, backdrop opacity, padding, maximum width and screen margin for every element; each element can override any of them in its Style Overrides. Screen Margin keeps elements from being moved off-screen or too close to its edge.

In the element editor, drag an element with the mouse or nudge it with the arrow keys (Shift ×10, Ctrl ×5), and press Space to cycle its alignment. Save & Close keeps the changes; Undo & Close or Escape reverts them.

## Installation
Install the Toggle Sprint JAR into the `mods` folder of a Minecraft 1.8.9 Forge client. It is client-only and isn't needed on servers.

## Requirements
- Minecraft 1.8.9 with Forge 11.15.1.2318.
- Nothing else: [MoulConfig](https://github.com/NotEnoughUpdates/MoulConfig) and the Kotlin standard library are bundled.

## Links and credits
- Source and issues: [github.com/brainage04/ToggleSprint](https://github.com/brainage04/ToggleSprint).
- Sister mod for modern Minecraft (Fabric and NeoForge): [BrainageHUD](https://github.com/brainage04/BrainageHUD).
- Credits: hannibal002, Moulberry and nea89.
- Licensed under the MIT licence.

## Upgrade notes
- **From 1.2.x:** your config is migrated automatically. The Ping and TPS elements are combined into the Network HUD, and the Rotation element into the Position HUD. The old primary colour carries over as the text colour; the secondary colour and text effects are gone, and headers are bold.
- **Waypoints:** the old waypoint HUD and its `/tswaypoint` command are gone. Waypoints saved with them had no world, so they are moved into the first world you join after updating.
- **Older versions:** updating from before 1.2 to 1.2.1 or later could crash with a MoulConfig `ContextualException` while loading the config; this is fixed.
