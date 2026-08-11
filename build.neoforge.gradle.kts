plugins {
    java
    `maven-publish`
    idea
    id("net.neoforged.moddev") version "2.0.143"
}

apply(from = rootProject.file("gradle/mobends-common-target.gradle"))

val targetName = sc.current.project
val runtimeUsername = providers.gradleProperty("runtime.username").orNull
val runtimeWorld = providers.gradleProperty("runtime.world").orNull

validateRuntimeProperties(runtimeUsername, runtimeWorld)

repositories {
    maven("https://maven.neoforged.net/releases/")
}

neoForge {
    version = sc.properties.get<String>("deps.neoforge.loader")
    runs {
        register("client") {
            client()
            gameDirectory.set(rootProject.file("run/$targetName/client"))
            if (runtimeUsername != null) {
                programArgument("--username")
                programArgument(runtimeUsername)
            }
            programArgument("--width")
            programArgument("2560")
            programArgument("--height")
            programArgument("1440")
            if (runtimeWorld != null) {
                programArgument("--quickPlaySingleplayer")
                programArgument(runtimeWorld)
            }
        }
        register("server") {
            server()
            gameDirectory.set(rootProject.file("run/$targetName/server"))
            programArgument("--nogui")
        }
    }
    mods {
        register("mobends") {
            sourceSet(sourceSets["main"])
        }
    }
}

fun validateRuntimeProperties(
    username: String?,
    world: String?,
) {
    if (username != null && !username.matches(Regex("[A-Za-z0-9_]{3,16}"))) {
        throw GradleException("runtime.username must be a valid Minecraft profile name")
    }
    if (world != null && (world.isBlank() || world.contains('\r') || world.contains('\n'))) {
        throw GradleException("runtime.world must be a non-empty single-line world directory name")
    }
}
