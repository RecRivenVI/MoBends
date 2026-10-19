# Development

Run commands from the repository root with JDK 25. The Wrapper remains Gradle
9.7.1. Target toolchains are Java 17 for Forge 1.20.1, Java 21 for NeoForge 1.21.1
and Java 25 for the 26.x Targets. Target facts and dependency versions live in
each Target's `target.properties`; project identity lives in `gradle.properties`.

```powershell
.\gradlew.bat projects tasks
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts --configuration-cache --configuration-cache-problems=fail --no-daemon --console=plain
.\gradlew.bat :version:26.2-fabric:test
```

All five Targets have complete native source/resource/test trees in `versions/`.
They were produced by a byte-verified replay of the previous shared Stonecutter
sources. The canonical Fabric source and four generated variants are now owned
by their respective Targets; there is no root authored source or hidden shared
product component. Existing inactive conditional comments are historical context,
not a source selection mechanism. Loader plugins and runtime dependencies keep
their versions; 26.2 uses Loom's native unobfuscated implementation/compileOnly
configurations. Forge keeps its reobfuscation step.

Artifacts remain under `build/libs/5.1.7/`; `buildAndCollect` does not install them.
Root aggregate tasks coordinate all five `:version:<target>` projects. There are
no non-Mod product components in this repository. Build infrastructure is not a
product component or a gameplay validation.

The current standard test instances and profile manifests belong to
[client runtime regression](../../validations/regression-client-runtime/validation.md).
Profile overrides preserve the earlier `-Pruntime.profile=s1|s2|m1|m2` and
`-Pruntime.multiplayerProfile=m1|m2` semantics. PRIMARY is RecRivenVI, SECONDARY is
CNN_MOYAN; dimensions remain 2560x1440. Only an explicit runtime request should
execute a client task. `--dry-run` checks its task graph without starting a game.

For user-approved startup:

```powershell
.\gradlew.bat :version:26.2-fabric:runClient
.\gradlew.bat :version:26.2-fabric:runClientMultiplayer
```

Source unit tests remain in `versions/<target>/src/test`; migration checks and
runtime profile checks do not substitute for animation or multiplayer acceptance.
