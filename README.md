# Toggle Sprint
A simple toggle sprint/sneak mod that is allowed on Hypixel. Made with MoulConfig.

# Status
The previously listed TPS, ping, and vertical center-alignment issues are resolved.

# Useful Classes
net/minecraft:
client/gui/GuiOverlayDebug.class

# Future Fixes
- Fixed `io.github.moulberry.moulconfig.internal.ContextAware$ContextualException` crash which occured when users updated their mod to a version with new `List<Integer>` fields (e.g. pre 1.2 to 1.2.1+)