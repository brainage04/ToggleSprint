# Development

Toggle Sprint targets Forge 1.8.9 and Java 8, not Fabric or NeoForge. Gradle uses a Java 8 toolchain for the game and compilation.

## Build and unit tests

```sh
heavy --quick -- env JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=60.0 ./gradlew --no-daemon build -Pfabricmoddingconventions.devClientOptions=/nonexistent
```

Java 8 requires a decimal value for `MaxRAMPercentage`; the explicit option overrides `heavy`'s integer default.

## Real client checks

Minecraft 1.8.9 has no GameTest framework. `runClientChecks` instead loads a separate test-only Forge mod in a real client, creates a flat singleplayer world, and exits after assertions on config navigation, input, commands and HUD rendering. The test mod is not included in the distribution jar. Its world, config and muted options stay under `build/client-checks/`; results are written to `build/client-checks/result.txt`. A failed or missing result fails Gradle.

```sh
heavy --mem 7G -- env JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=60.0 ALSOFT_DRIVERS=null xvfb-run -a ./gradlew --no-daemon build runClientChecks -Pfabricmoddingconventions.devClientOptions=/nonexistent
```

Essential Loom's built-in Xvfb wrapper loses the classpath, so the build disables that wrapper for every run task. Use the outer `xvfb-run` command above for headless runs; no temporary Gradle init script is needed.

For an interactive development client, use `runClient`. Never copy a personal `options.txt` into an automated run.
