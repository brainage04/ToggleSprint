# Development

Toggle Sprint targets Forge 1.8.9 and Java 8, not Fabric or NeoForge. Gradle uses a Java 8 toolchain for the game and compilation.

## Build and unit tests

```sh
heavy --quick -- env JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=60.0 ./gradlew --no-daemon build
```

Java 8 requires a decimal value for `MaxRAMPercentage`; the explicit option overrides `heavy`'s integer default.

## Real client checks

Minecraft 1.8.9 has no GameTest framework. `runClientChecks` instead loads a separate test-only Forge mod in a real client, creates two flat singleplayer worlds, and exits after assertions on config navigation, input, commands, HUD rendering and world changes. The test mod is not included in the distribution jar. Its worlds, config and muted options stay under `build/client-checks/`; results are written to `build/client-checks/result.txt`. A failed or missing result fails Gradle; the client task times out after five minutes.

```sh
heavy --mem 7G -- env JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=60.0 ALSOFT_DRIVERS=null xvfb-run -a ./gradlew --no-daemon build runClientChecks
```

Essential Loom's built-in Xvfb wrapper loses the classpath, so the build disables that wrapper for every run task. Use the outer `xvfb-run` command above for headless runs; no temporary Gradle init script is needed.

Headless Linux runs need Xvfb and the `xrandr` command (`x11-xserver-utils` on Ubuntu). LWJGL 2 queries display modes through `xrandr`, even when the game is windowed.

For an interactive development client, use `runClient`. Never copy a personal `options.txt` into an automated run.
