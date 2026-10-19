# MoBends repository guidance

This is the RecRivenVI/MoBends port repository. Do not confuse it with another
implementation sharing the MoBends brand. The repository is its own source,
runtime and Git ownership boundary.

## Layout and naming

The governing layout was adopted from Ravens-mod-template/AGENTS.md at SHA-256
5527638e1fc256cdd2c4594e1d3021de6665257673e7cc092452db1fa490c6b8.

- Each `versions/<minecraft>-<loader>/` is a complete Mod source root containing
  `src/main`, `src/test`, `build.gradle.kts` and `target.properties`.
- Gradle addresses are `:version:<target>`. The `:version` descriptor maps to the
  plural `versions/` directory and carries no product implementation.
- Non-Mod product components belong in `components/<component>/`; Gradle
  components use `:component:<component>`. Do not invent empty components.
  Gradle infrastructure remains in `gradle/`, `buildSrc/` and `eng/`.
- Ordinary documents belong directly in one of `documents/usage`, `development`,
  `design`, `project`, `reference`, or `release`. Use `<type>-<topic>.md`, with a
  single lowercase type and lowercase hyphen-separated topic. ADRs use
  `adr-<number>-<topic>.md`; phases use `phase-<number>-<topic>.md`. Do not nest
  category subdirectories or create documents just to fill a template.
- Keep existing standard root files such as README and LICENSE in place.
- Product validations belong in `validations/<type>-<topic>/`, with `task/`,
  optional `instance/`, `result/` and `validation.md`; a simple user-oriented
  `validation-guide.md` is optional. Source unit tests stay with their Target.
- Compatibility means coexistence, parity means cross-Target behavior equality,
  and conformance means compliance with an explicit schema or protocol. Separate
  Target test passes are not evidence of parity.
- Daily instances use `instances/<target>/{client,client-multiplayer,server}` only
  for genuine supported roles. S1/S2/M1/M2 and special fixtures are validation
  configurations, not additional daily variants. Current migrated instances are
  in `validations/regression-client-runtime/instance/` with their full profile
  hierarchy preserved. Historical instance subtrees remain isolated.
- Instance contents and `local.properties` are ignored. Validation instance
  directories permit traversal; whitelist only individually reviewed files.
  Preserve worlds, configs, mods, screenshots, logs and unknown data.

## Product and build boundaries

Five implemented Targets are 1.20.1-forge, 1.21.1-neoforge, 26.1.2-neoforge,
26.2-fabric and 26.2-neoforge. Product roles remain client and
client-multiplayer. Optional server-compatible code is not a server test role.

Preserve `mobends`, `io.github.recrivenvi.mobends`, existing Addon API, BendsPack,
animation timing, skin/armor/wearable behavior, trails, rendering transforms and
existing Mod Menu integration. Do not modify third-party mods to implement
compatibility or infer a compatibility promise from a test installation.

CSL 15.0.1-Universal is a required test mod on standard clients. Fabric standard
clients also use Mod Menu 20.0.1. S1 is clean; Zume is not a MoBends test mod.
Physical test JARs are staged inside the selected validation instance; the Mod
itself runs on the Loader development classpath. Packaging is not installation.

The source tree was materialized from the effective Stonecutter 0.9.7 replay of
the signed Update baseline; each Target now builds its own native sources.
Do not reactivate old conditional branches by editing a different Target.
Loom remains 1.17.19; ModDevGradle/LegacyForge remain 2.0.140; Wrapper 9.7.1.
Run Gradle with JDK 25; Target toolchains remain 17, 21 and 25 as declared.

Read README and documents/development/guide-development.md before build work.
Use `verifyTargets`, `spotlessCheck`, `testAll`, `checkAll`, `buildAll`,
`collectArtifacts`; node tasks use `:version:<target>:<task>`.
Keep configuration-cache support and existing automated checks.

## Evidence and authorization

Long-term evidence belongs in validation `result/`. Preserve original timestamps,
paths, commit bindings and failure/PASS claims inside historical evidence; express
new path mappings separately. Automated tests are not visual acceptance.

Do not operate Minecraft or other running project processes without current
authorization. Do not touch other projects or shared templates. No commits or
pushes for structural migration results unless the user separately authorizes
them. The pre-migration Update commit is not approval of migration results.
