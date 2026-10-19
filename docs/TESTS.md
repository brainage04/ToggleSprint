# Test review

## Frozen rubric

The rubric was fixed before reading flagged test bodies:

- Keep: production behavior with an independent observable result, meaningful boundary, lifecycle transition or state history. Small pure functions and generic algorithms can earn keep; a fixture is not itself a mock echo.
- Redundant: the same production behavior, input equivalence class and failure sensitivity are covered by another named retained test. Similar API calls alone do not make a test redundant.
- Tautological: expected behavior is computed by copying the production algorithm; a fixture or mock only echoes its input; the only assertion checks registration, a constant or wiring without observable behavior.

Only flagged bodies were read for pruning. No tests were removed. The inventory below is the 42 unit tests and six client scenarios present at the pruning step, before the coverage additions.

## Unit classifications

All tests below are **keep**. Each bullet names a test or a distinct group in one test class.

### ConfigUpdaterMigratorTest (5)

- `version one parity controls migrate to amount and CPS format`
- `version two chunk coordinate toggle migrates to chunk position`
- `version three primary colour dropdown migrates to the same RGB text colour`
- `version four elements merge into the Network and Position HUDs with renamed options`
- `version four with ping and TPS both hidden keeps the Network HUD hidden with both lines`

Different source versions and the hidden-elements branch protect different saved user configs.

### FullbrightHandlerTest (3)

- `zero ambient light reproduces vanilla endpoint brightness`
- `full ambient light makes every light level fully bright`
- `negative ambient light follows the same dimension formula`

The negative-light test was flagged for a possible copied formula. Its body actually uses literal endpoint expectations (-1 and 1), not an implementation copy. It protects the supported negative range, which the other tests do not exercise.

### FishingChancesTest (4)

- `an unenchanted rod uses Forge's base chances`
- `Luck of the Sea III and Lure III clamp junk at zero`
- `Luck of the Sea III trades junk for treasure`
- `half percents round by largest remainder to a total of 100`

These exercise the base distribution, clamping, enchantment bias and rounding tie separately.

### KeystrokesHudSupportTest (6)

- `transition rises and falls according to elapsed time`
- `transition ignores a clock moving backwards`
- `rgb interpolation clamps progress and interpolates channels`
- `alpha clamps without changing rgb channels`
- `CPS labels match every configured format`
- `click expiry retains the inclusive one-second boundary`

Animation history, clock reversal, channel interpolation, alpha preservation, visible labels and expiry have separate failure modes.

### PlayerPositionTrackerTest (1)

- `farming tool names match whole words only`

Protects player-facing tool recognition, including false positives.

### StatusEffectHudTest (4)

- `durations under an hour are minutes and padded seconds`
- `partial seconds round down`
- `durations from an hour gain an hours field and padded minutes`
- `level I and a hidden duration are left out`

The duration boundaries and omitted fields are distinct formatting cases.

### TickRateTrackerTest (6)

- `needs two reports before it has a rate`
- `measures how fast the server actually ticks`
- `weights irregular reports by elapsed time`
- `forgets older intervals when the window shrinks`
- `restarts when game time goes backwards`
- `reset discards the previous report`

These protect different sample histories and lifecycle transitions, not just repeated examples of the same average.

### ElementPlacementTest (3)

- `offset puts the element back where the editor dropped it on every axis`
- `offsets are measured inwards from the anchored edge`
- `anchor cycle visits all nine anchors once`

The round-trip invariant complements independent position expectations; anchor traversal tests a separate editor operation.

### EnchantmentConflictsTest (7)

- `enchantments conflicting with present ones and blacklisted ones are not missing`
- `an item with every available enchantment is missing nothing`
- `mutually exclusive enchantments share one group in input order`
- `groups are connected by conflicts even when not every pair conflicts`
- `names match regardless of case`
- `an exact match wins over partial matches`
- `every partial match is listed in input order`

All seven were flagged because of the test enchantment fixture. Body review confirmed that the fixture supplies data and a predicate to the real production filtering, connected-component grouping and name matching. Expected lists are independent. None merely tests the fixture predicate or copies the implementation.

### WaypointStoreTest (3)

- `quick names fill the first gap ignoring case`
- `distances are metres below a kilometre then kilometres`
- `quoted names with spaces are one word`

Naming, distance-unit transitions and malformed quoting protect different behavior.

## Real-client classifications

All six original `ClientChecks` scenarios are **keep**:

- Mods-list config returns to its parent: real screen transition and Escape destination.
- Forge input toggles and releases sprint and sneak: key-press events change the live movement bindings.
- Fullbright command changes config and rejects NaN: valid mutation and rejected invalid input.
- Waypoint commands add and remove a quoted name: command dispatch changes the current world's waypoints.
- Forge overlay draws an enabled HUD element: actual overlay rendering produces nonempty bounds.
- Config command opens and closes in-world: deferred opening and return to the game.

The mod is client-only Forge 1.8.9, which predates Minecraft's GameTest framework. There are no Fabric, NeoForge or server GameTest suites to prune. See [DEVELOPMENT.md](DEVELOPMENT.md) for the real-client runner.

## Coverage assessment and additions

Before coverage work, gaps included disabled-key behavior, command persistence and actual enchantment registry output, live light-table restoration, HUD disable behavior, element-editor undo, waypoint validation and world isolation. These matter because they affect movement, saved settings and data, screen navigation and what the player sees.

Added three unit tests for waypoint name boundaries, forbidden characters/Unicode and nearest chat colours. Extended the real-client suite to exercise disabled toggle input, command config persistence, all three enchant command families with chat assertions, applying/disabling fullbright and unloading its world, disabling a HUD element, persisted editor undo, and joining a second world without leaking the first world's waypoints. Each run resets only its own test config and saves under `build/client-checks`.

No gamerules or server installation are involved. There is one supported loader, Forge 1.8.9; Fabric and NeoForge parity is not applicable.

Remaining coverage limits: no pixel-perfect assertions for each HUD or waypoint effect, no GPU-vendor matrix, no real remote-server packet jitter/disconnect test, and no full mouse-driven walkthrough of every MoulConfig or waypoint form. Inventory Stats and input edge cases are reviewed separately with regression scenarios when bugs are found. These limits are not represented as passing tests.

## Code-review regressions

- Disabled toggle keys left queued presses unread, which could replay after re-enabling the key. Input handling now consumes the press before checking the enable flag; the client scenario re-enables the key and posts another input event.
- Toggling off a sprint/sneak binding assigned to a mouse button used a keyboard-only lookup with a negative key code. It now uses vanilla's keyboard/mouse-aware lookup; a scenario remaps both movement bindings to mouse buttons and releases them.
- Inventory Stats ignored armour and could call an armour-only inventory empty. It now visits all player inventory slots; the regression equips boots with an empty main inventory and checks the resulting chat.
- Config loading did not close its reader. Loading now uses a scoped reader; a Linux client scenario inspects the actual config file descriptors after a load.
- Autosave and the JVM shutdown hook could write the same temporary config file concurrently. Saving is synchronized; four test workers exercise concurrent saves and parse the final file.
- Reach HUD's attack-click cache survived world changes. The HUD now clears its state on client-world unload; a scenario seeds an old target, changes worlds and checks that it is not reused.

Review judgement calls left unchanged: the main config fails explicitly when unreadable, whereas waypoints back up unreadable JSON; choosing a common recovery policy needs an owner decision. Waypoint write failures are logged while edits remain in memory; rolling edits back or retaining them for a later save is also a policy choice. The UI and feedback are English literals rather than a localisation system.
