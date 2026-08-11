plugins {
    java
    `maven-publish`
    idea
    id("net.neoforged.moddev.legacyforge") version "2.0.143"
}

apply(from = rootProject.file("gradle/mobends-common-target.gradle"))

val targetName = sc.current.project
val runtimeUsername = providers.gradleProperty("runtime.username").orNull
val runtimeWorld = providers.gradleProperty("runtime.world").orNull

validateRuntimeProperties(runtimeUsername, runtimeWorld)

repositories {
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

legacyForge {
    version = sc.properties.get<String>("deps.forge.loader")
    validateAccessTransformers.set(true)
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

mixin {
    add(sourceSets["main"], "mobends-forge.refmap.json")
    config("mobends-forge.mixins.json")
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
