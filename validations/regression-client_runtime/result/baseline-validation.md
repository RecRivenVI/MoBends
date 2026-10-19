# Client runtime regression environment

This validation preserves the existing MoBends animation/render/configuration
test environments. It does not claim new visual PASS, compatibility certification,
or cross-Target parity. No game was started during the structural migration.

`task/profiles/<target>/client/<primary|secondary>/<profile>.json` contains the
reviewable instance configuration. `instance/<target>/client/<role>/<profile>`
contains its ignored runtime data. S1 is clean, S2 is standard single-client, M1
uses two clients and an integrated server, and M2 represents the two client sides
of a dedicated-server scenario. MoBends has no dedicated-server product role or
server Profile. Primary/secondary names and resolution remain unchanged.

Legacy and legacy-runtime subtrees are preserved history. They must not be merged
into standard instances or treated as freshly accepted fixtures. Existing BendsPack,
skin, DebugBridge and visual capture files are user-owned. No GUI automation is
performed by the staging tools.

The historical continuously shared Fabric client has been classified B and moved
intact from `instance/legacy/port-92c5960/26.2-fabric/client/primary/s2` to
`instances/26.2-fabric/client`. Evidence is this task's repeated user handoffs
("修完不要推送，实例留给我实测", "继续测试披风") plus the 20260824
`runtime-source-identification.json` and destination manifests linking the original
`run/fabric-26.2/client` to that preserved instance. Both worlds and all captures,
mods, config and logs stay together. The current relocation input is
`task/runtime-relocations.json`; historical manifests retain their original paths.

Other `legacy-runtime` instances remain category C: the retained identification
record establishes Target/side, but labels profile unknown and does not establish
fixed-scenario versus continuously shared usage. Do not infer that from names or
timestamps. The 30 existing standard scenario profiles stay at their current
validation locations. Default Gradle client tasks still select those profiles;
moving the archived shared client does not automatically activate it or change
which mods are loaded by the normal validation task.

From the repository root, validate the schema, selected paths and pinned known
test mods without launching a game:

```powershell
pwsh -NoProfile -File eng/runtime/Verify-Profiles.ps1
.\gradlew.bat verifyRepositoryProfiles
.\gradlew.bat :version:26.2-fabric:runClient --dry-run
```

Stage a specific profile when test mod installation is intended:

```powershell
pwsh -NoProfile -File eng/runtime/Stage-Profile.ps1 -ProfileRecord validations/regression-client-runtime/task/profiles/26.2-fabric/client/primary/s2.json
```

The shared repository tooling in `eng/runtime/` serves every Target and keeps
public download URIs plus fixed SHA-512 values. It only manages known test mods;
it preserves other mods and user data. S1 omits CSL/Mod Menu/Zume. Other profiles
use CSL 15.0.1-Universal; Fabric also uses Mod Menu 20.0.1. Zume is excluded.
These runtime JARs do not become required product metadata dependencies.

When visual testing is separately authorized, inspect standing, walking, sprinting,
sneaking, swimming entry/exit/pitch, equipment/cape behavior, trails, configuration
and local BendsPack with the existing worlds. Record results per Target. Restore
temporary test state and save normally. Until this is performed, visual status
for the migrated environment is NOT EXECUTED.
