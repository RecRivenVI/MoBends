plugins {
    java
    `maven-publish`
    idea
    id("net.fabricmc.fabric-loom") version "1.17.19"
}

apply(from = rootProject.file("gradle/mobends-common-target.gradle"))

val targetName = sc.current.project
val runtimeUsername = providers.gradleProperty("runtime.username").orNull
val runtimeWorld = providers.gradleProperty("runtime.world").orNull

validateRuntimeProperties(runtimeUsername, runtimeWorld)

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    add("minecraft", "com.mojang:minecraft:${sc.current.version}")
    implementation("net.fabricmc:fabric-loader:${sc.properties.get<String>("deps.fabric.loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${sc.properties.get<String>("deps.fabric.api")}")
    implementation("com.terraformersmc:modmenu:${sc.properties.get<String>("deps.modmenu")}")
}

loom {
    mods {
        maybeCreate("mobends").sourceSet(sourceSets["main"])
    }
    runs {
        maybeCreate("client").apply {
            client()
            runDirectory.set(rootProject.layout.projectDirectory.dir("run/$targetName/client"))
            if (runtimeUsername != null) {
                programArguments.addAll("--username", runtimeUsername)
            }
            programArguments.addAll("--width", "2560", "--height", "1440")
            if (runtimeWorld != null) {
                programArguments.addAll("--quickPlaySingleplayer", runtimeWorld)
            }
        }
        maybeCreate("server").apply {
            server()
            runDirectory.set(rootProject.layout.projectDirectory.dir("run/$targetName/server"))
            programArguments.add("--nogui")
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
