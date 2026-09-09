pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
    id("dev.kikugie.loom-back-compat") version "0.4.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_25)) {
    "The Stonecutter build must run on JDK 25 or newer; current runtime is ${JavaVersion.current()}."
}

stonecutter {
    create(rootProject) {
        fun match(
            project: String,
            vararg loaders: String,
            version: String = project,
        ) {
            for (loader in loaders) {
                version("$project-$loader", version).buildscript("build.$loader.gradle.kts")
            }
        }

        match("1.20.1", "forge")
        match("1.21.1", "neoforge")
        match("26.1.2", "neoforge")
        match("26.2", "fabric", "neoforge")
        vcsVersion = "26.2-fabric"
    }
}

rootProject.name = "MoBends"

includeBuild("gradle/formatting") {
    name = "mobends-formatting"
}
