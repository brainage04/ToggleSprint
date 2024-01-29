# Toggle Sprint
A simple toggle sprint/sneak mod that is allowed on Hypixel. Made with MoulConfig.

# Current Bugs
- TPS not working
- Ping not working
- Vertical center alignment is bugged (a 2 line element is centered on the middle of the bottom line instead of the middle of the element)

# Useful Classes
net/minecraft:
client/gui/GuiOverlayDebug.class

# Future Fixes
- Fixed `io.github.moulberry.moulconfig.internal.ContextAware$ContextualException` crash which occured when users updated their mod to a version with new `List<Integer>` fields (e.g. pre 1.2 to 1.2.1+)