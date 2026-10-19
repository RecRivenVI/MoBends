import java.util.Properties

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
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_25)) {
    "The Stonecutter build must run on JDK 25 or newer; current runtime is ${JavaVersion.current()}."
}

rootProject.name = "MoBends"

include(":version")
project(":version").projectDir = file("versions")
val targets = listOf("1.20.1-forge", "1.21.1-neoforge", "26.1.2-neoforge", "26.2-fabric", "26.2-neoforge")
for (target in targets) {
    include(":version:$target")
    project(":version:$target").projectDir = file("versions/$target")
}
gradle.beforeProject {
    if (path.startsWith(":version:")) {
        val facts = Properties()
        file("target.properties").inputStream().use(facts::load)
        facts.forEach { key, value -> extensions.extraProperties.set(key.toString(), value.toString()) }
    }
}
