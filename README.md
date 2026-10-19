# Mo' Bends

The current multi-target port lives in `versions/<target>/`, with Gradle tasks
addressed as `:version:<target>:<task>`. Implemented targets are Forge 1.20.1,
NeoForge 1.21.1, NeoForge 26.1.2, Fabric 26.2 and NeoForge 26.2.

Use JDK 25 to run the Gradle 9.7.1 Wrapper; each Target selects its own Java
toolchain. Start with the [current development guide](documents/development/guide-development.md),
[runtime validation](validations/regression-client-runtime/validation.md), and
[migration verification](validations/migration-project-layout/validation.md).

```powershell
.\gradlew.bat projects
.\gradlew.bat verifyTargets testAll checkAll buildAll collectArtifacts
.\gradlew.bat :version:26.2-fabric:test
```

The material below is the upstream project introduction and historical 1.12.2
development guidance, not the current build procedure. An exact pre-migration
copy is kept in [the upstream reference](documents/reference/guide-upstream-readme.md).

## Upstream project introduction
[![CurseForge Downloads](http://cf.way2muchnoise.eu/231347.svg)](https://www.curseforge.com/minecraft/mc-mods/mo-bends) [![Mod Versions](http://cf.way2muchnoise.eu/versions/231347.svg)](https://www.curseforge.com/minecraft/mc-mods/mo-bends) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/dc7fa82e8d904f65b33b948ed093c21f)](https://app.codacy.com/gh/mobends/MoBends?utm_source=github.com&utm_medium=referral&utm_content=mobends/MoBends&utm_campaign=Badge_Grade_Dashboard)


![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/mobends/MoBends.svg?style=for-the-badge)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=for-the-badge)](http://makeapullrequest.com)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/mobends/MoBends.svg?style=for-the-badge)](https://github.com/mobends/MoBends/pulls)
[![GitHub issues](https://img.shields.io/github/issues-raw/mobends/MoBends.svg?style=for-the-badge)](https://github.com/mobends/MoBends/issues)

A Minecraft mod that adds more realistic looking animations to the inhabitants of your blocky world.

## Discord
The development of version **1.0.0** is in progress right now! If you'd like to be a part of it, see the progress, or just hang out, join our Discord server!

[![Discord](https://img.shields.io/discord/386940930739011584.svg?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/JqgWRgdkvx)

Say you came from GitHub if you decide to come by! Hope to see you there

## Local Development Setup
Install a Java Development Kit (JDK) appropriate for the Minecraft version you are developing for. For Minecraft 1.12.2, use JDK 8.
I personally use the [Eclipse Temurin JDK](https://adoptium.net/temurin/releases?version=8&os=any&arch=any).

There are a few paper-cuts in developing mods (especially for older versions of Minecraft), but a sure-fire way to avoid
them is to build and run the project using IntelliJ IDEA. It seems to build everything from source, as opposed to the
`./gradlew runClient` and `./gradlew runServer` scripts, which fail at bootup.

### Troubleshooting
- IntelliJ freaks out and can't find symbols in the project, but compiles fine.
    - `File > Invalidate Caches/Restart` works like a charm <3
- Crashing on a NullPointerException inside FML.
    - Click "Download Sources" in the Gradle sidebar

## Creating addons
Addons are a way to extend the functionality of Mo' Bends, e.g. adding support for new mobs.

There's an example addon that I set up a while back, but the sources for CustomNPCs are
no longer available, which makes it impossible to use. I'm looking for a replacement soon.

[CustomNPCs Support Addon](https://github.com/mobends/mobends-addon-customnpcs)
