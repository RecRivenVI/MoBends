# Project layout migration verification

Baseline preservation: signed `Update` commit
`01d5cff03a188ee3bf0366d4e2c17ed0ff1e85a3`, parent
`92c5960f9ab4749f9a243d8f4708059c2b706a7a`, pushed normally before migration.
The governing template AGENTS SHA-256 is
`5527638e1fc256cdd2c4594e1d3021de6665257673e7cc092452db1fa490c6b8`.

The one-time `task/Migrate-Layout.ps1` records the source replay and moves only
repository-owned files. It must not be rerun on a migrated checkout. Source and
runtime maps in `result/` preserve the original paths and hashes. Temporary
baseline source/build snapshots remain ignored under `instance/`.

| Before | Current location |
| --- | --- |
| Root src/main and src/test, plus four effective Stonecutter outputs | versions/<target>/src/main and src/test |
| Root Loader build scripts | versions/<target>/build.gradle.kts |
| Central Stonecutter properties | gradle.properties plus versions/<target>/target.properties |
| Root Stonecutter coordinator | Root settings.gradle.kts and build.gradle.kts |
| run/profiles | validations/regression-client-runtime/task/profiles |
| run/instances | validations/regression-client-runtime/instance |
| run/cache | validations/regression-client-runtime/instance/artifact-cache |
| Upstream README content | documents/reference/guide-upstream-readme.md |

`verifyTargets` checks build/source layout through `eng/Verify-Layout.ps1`; these
are engineering checks. The product tests remain in their native JUnit source
trees. Verify migration from the root:

```powershell
.\gradlew.bat projects tasks
.\gradlew.bat spotlessCheck verifyTargets testAll checkAll buildAll collectArtifacts --configuration-cache --configuration-cache-problems=fail --no-daemon --console=plain
git diff --check
pwsh -NoProfile -File validations/migration-project-layout/task/Verify-Preservation.ps1
pwsh -NoProfile -File validations/migration-project-layout/task/Verify-SourceReplay.ps1
```

The two preservation scripts are historical migration comparisons, not generic
checks for every future version. `Verify-SourceReplay.ps1` requires the exact
ignored `instance/original-authored-src` and `instance/replay` snapshots bound
to `result/source-replay.json`; a clean checkout does not contain those snapshots.
It checks the materialized migration output plus formatting/import changes. A
later intentional product edit may correctly fail that historical comparison.
Do not use it as a current release gate or regenerate old evidence to make it pass.

`Verify-Preservation.ps1` requires the preserved runtime files. It uses the
current explicit relocation map in
`../regression-client-runtime/task/runtime-relocations.json`, when present, to
find the same bytes after an authorized move. Starting the game may legitimately
change worlds, logs and configs; that is not a reason to overwrite the migration
manifest. Ordinary current entry points remain the Gradle lifecycle above and
`pwsh -NoProfile -File eng/Verify-Layout.ps1`.

Do not execute `task/Migrate-Layout.ps1` or archived build/formatting scripts on
this checkout. They document a one-time transformation of the Update tree, not
a repair, reset, or cleanup procedure. No current Gradle or IDE entry invokes them.

Acceptance requires identical preserved runtime files, all five native builds and
existing unit tests, unchanged project identity/dependency facts, valid profile
paths and no runtime files leaking into Git. Automated success does not establish
visual correctness or cross-Loader parity. This task explicitly does not launch
games or perform manual acceptance.
