pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikugieSnapshots"
            content { includeGroupByRegex("dev\\.kikugie.*") }
        }
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10-alpha.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_25)) {
    "The Stonecutter build must run on JDK 25 or newer; current runtime is ${JavaVersion.current()}."
}

rootProject.name = "MoBends"

data class TargetDefinition(
    val loader: String,
    val minecraftVersion: String,
) {
    val id = "$loader-$minecraftVersion"
}

fun readTargetStatus(
    propertiesFile: File,
    target: TargetDefinition,
): String {
    var section: String? = null
    var defaultStatus: String? = null
    var targetStatus: String? = null
    val targetSection = "[${target.loader}.\"${target.minecraftVersion}\"]"

    propertiesFile.forEachLine { sourceLine ->
        val line = sourceLine.substringBefore('#').trim()
        when {
            line.startsWith('[') && line.endsWith(']') -> {
                section = line
            }

            line.startsWith("target.status") -> {
                val status = line.substringAfter('=').trim().removeSurrounding("\"")
                when (section) {
                    null -> defaultStatus = status
                    targetSection -> targetStatus = status
                }
            }
        }
    }

    return targetStatus ?: defaultStatus
        ?: throw GradleException("Missing target.status for Target '${target.id}'.")
}

val targetDefinitions =
    listOf(
        TargetDefinition("forge", "1.20.1"),
        TargetDefinition("neoforge", "1.21.1"),
        TargetDefinition("neoforge", "26.1.2"),
        TargetDefinition("fabric", "26.2"),
        TargetDefinition("neoforge", "26.2"),
    )
val targetPropertiesFile = file("stonecutter.properties.toml")
val targetStatuses = targetDefinitions.associate { it.id to readTargetStatus(targetPropertiesFile, it) }
val allowedPlannedTargetTasks =
    setOf(
        "clean",
        "help",
        "projects",
        "properties",
        "tasks",
        "verifyTargetContract",
    )

gradle.startParameter.taskNames.forEach { requestedTask ->
    val segments = requestedTask.trimStart(':').split(':')
    if (segments.size >= 2) {
        val targetId = segments.first()
        val taskName = segments.last()
        if (targetStatuses[targetId] == "planned" && taskName !in allowedPlannedTargetTasks) {
            throw GradleException(
                "Target '$targetId' is 'planned'; task '$taskName' is unavailable. " +
                    "Set target.status to 'implemented' in stonecutter.properties.toml to reactivate " +
                    "its Minecraft/loader build and runtime tasks.",
            )
        }
    }
}

stonecutter {
    create(rootProject) {
        fun target(target: TargetDefinition) {
            val buildscript =
                if (targetStatuses.getValue(target.id) == "implemented") {
                    "build.${target.loader}.gradle.kts"
                } else {
                    "build.planned.gradle.kts"
                }
            version(target.id, target.minecraftVersion).buildscript(buildscript)
        }

        vcsVersion = "fabric-26.2"
        targetDefinitions.forEach(::target)
    }
}

includeBuild("gradle/formatting") {
    name = "mobends-formatting"
}
