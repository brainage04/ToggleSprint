# Toggle Sprint
A simple toggle sprint/sneak mod that is allowed on Hypixel. Made with MoulConfig.

# Status
The previously listed TPS, ping, and vertical center-alignment issues are resolved.

# Useful Classes
net/minecraft:
client/gui/GuiOverlayDebug.class

# Future Fixes
- Fixed `io.github.moulberry.moulconfig.internal.ContextAware$ContextualException` crash which occured when users updated their mod to a version with new `List<Integer>` fields (e.g. pre 1.2 to 1.2.1+)

# HUD elements
Every element's position and alignment can be changed in the element editor, opened with Numpad Plus or `/togglesprint gui`: drag an element with the mouse, nudge it with the arrow keys (Shift ×10, Ctrl ×5), and press Space to cycle its alignment.

Global GUI Settings sets the text colour, text shadows, backdrop opacity, padding, maximum element width and screen margin for every element, and each element can override any of them in its Core Settings. Headers are bold; the old secondary colour and text effects are gone, and an existing primary colour carries over as the text colour.

The config editor opens with Enter, `/togglesprint`, or the Config button in Forge's Mods list.

TPS is measured from the game time the server reports each second, rather than from how many packets arrive.

# Waypoints
Waypoints are saved per world (singleplayer save or server address) in `config/togglesprint/waypoints.json`. Each visible waypoint in your current dimension is drawn in the world, in its own colour:

- A beacon beam through the whole height of the world, which widens with distance so it can be found from far away.
- A spinning gem hovering above the spot, with a pulse spreading over the ground below it when you are near.
- A label with the name and distance, readable through walls. It keeps its size on screen however far away the waypoint is. Beyond 16 blocks, only the waypoint you look towards shows its name; the others show their distance.

Every dimension also has a built-in "World Centre" waypoint at 0, 64, 0, white by default. It can be hidden or recoloured with Show World Centre and World Centre Colour.

Each part can be turned off, and the label resized, in the config under BrainageHUD Parity > Waypoints.

- Create Waypoint (default B) creates a waypoint where you stand, named "Waypoint 1", "Waypoint 2", and so on.
- Manage Waypoints (default U) opens a screen to add, edit, hide/show and delete them.
- `/waypoints add <name> [<x> <y> <z>]`, `/waypoints remove <name>`, `/waypoints list`, and `/waypoints` to open the screen. Quote names that contain spaces.

Both keys are in the Toggle Sprint category of the controls screen.

The old waypoint HUD and its `/tswaypoint` command are gone. Waypoints saved with them had no world, so they are moved into the first world you join after updating, and removed from the old list in `config.json`.

# Enchant Info
The **Enchant Info HUD** shows the held item's name, its enchantments (with the maximum level after any enchantment below it), then a "Missing:" list of the enchantments it could still get. Enchantments that cannot be combined share one line, e.g. `Fortune III / Silk Touch`. Blacklisted enchantments are never listed as missing. Each part can be turned off in the config under BrainageHUD Parity > Enchant Info HUD.

Tooltip lines of enchantments at their maximum level are shown in bold (Enchant Info > Highlight Max Level Enchants).

Commands:
- `/getenchants [item]` lists every enchantment the held item (or the given item ID) can get, grouping enchantments that conflict with each other.
- `/getenchantinfo <enchantment>` shows an enchantment's ID, maximum level, conflicts and the items it applies to. Accepts an ID (`minecraft:fortune` or `fortune`) or a name, ignoring case; a partial name lists every match.
- `/blacklistedenchants add <enchantment>`, `/blacklistedenchants remove <enchantment>` and `/blacklistedenchants query` edit and show the blacklist, which is saved in the config as enchantment IDs.