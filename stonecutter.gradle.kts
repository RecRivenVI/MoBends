import dev.kikugie.stonecutter.data.tree.ProjectNode
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

plugins {
    id("dev.kikugie.stonecutter")
}

repositories {
    mavenCentral()
}

stonecutter flags {
    this["line_separator"] = "\n"
}

stonecutter active file("versions/current")

stonecutter parameters {
    val loader = current.project.substringBefore('-')
    val version = current.project.substringAfter('-')

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "forge", "neoforge")
    }
}

abstract class VerifySourceLayoutTask : DefaultTask() {
    @get:Input abstract val repositoryPath: Property<String>

    @get:Input abstract val activeTarget: Property<String>

    @get:Input abstract val expectedTargets: ListProperty<String>

    @get:Input abstract val expectedTargetStatuses: ListProperty<String>

    @get:Input abstract val expectedSourceRoots: ListProperty<String>

    @get:Input abstract val targetSourceRoots: ListProperty<String>

    @get:Input abstract val targetRuntimeScripts: ListProperty<String>

    @TaskAction
    fun verify() {
        val repository = File(repositoryPath.get())
        val actualTargets =
            repository
                .resolve("versions")
                .listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name }
                ?.sorted()
                .orEmpty()
        val expectedTargetNames = expectedTargets.get().sorted()
        check(actualTargets == expectedTargetNames) {
            "Stonecutter targets must be exactly $expectedTargetNames, found $actualTargets."
        }

        val propertiesFile = repository.resolve("stonecutter.properties.toml")
        val defaultStatus =
            propertiesFile
                .readLines()
                .asSequence()
                .map { it.substringBefore('#').trim() }
                .takeWhile { !it.startsWith('[') }
                .firstOrNull { it.startsWith("target.status") }
                ?.substringAfter('=')
                ?.trim()
                ?.removeSurrounding("\"")
                ?: error("stonecutter.properties.toml must declare a default target.status.")
        val expectedStatuses =
            expectedTargetStatuses.get().associate { specification ->
                val (target, status) = specification.split('=', limit = 2)
                target to status
            }
        val actualStatuses =
            expectedTargetNames.associateWith { target ->
                val loader = target.substringBefore('-')
                val minecraftVersion = target.substringAfter('-')
                val section = "[$loader.\"$minecraftVersion\"]"
                var activeSection: String? = null
                var targetStatus: String? = null
                propertiesFile.forEachLine { sourceLine ->
                    val line = sourceLine.substringBefore('#').trim()
                    when {
                        line.startsWith('[') && line.endsWith(']') -> {
                            activeSection = line
                        }

                        activeSection == section && line.startsWith("target.status") -> {
                            targetStatus = line.substringAfter('=').trim().removeSurrounding("\"")
                        }
                    }
                }
                targetStatus ?: defaultStatus
            }
        check(actualStatuses == expectedStatuses) {
            "Target statuses must be exactly $expectedStatuses, found $actualStatuses."
        }
        check(repository.resolve("build.planned.gradle.kts").isFile) {
            "Planned targets require build.planned.gradle.kts for lazy, loader-free configuration."
        }

        val runtimeScripts =
            targetRuntimeScripts.get().associate { specification ->
                val (target, script) = specification.split('=', limit = 2)
                target to script
            }
        check(runtimeScripts.keys.sorted() == expectedTargetNames) {
            "Runtime script mappings must cover exactly $expectedTargetNames, found ${runtimeScripts.keys.sorted()}."
        }
        runtimeScripts.forEach { (target, scriptPath) ->
            val script = repository.resolve(scriptPath)
            check(script.isFile) { "Target '$target' runtime script '$scriptPath' does not exist." }
            val scriptText = script.readText()
            listOf("client", "server").forEach { side ->
                val expectedPathTemplate = "run/\$targetName/$side"
                check(scriptText.contains(expectedPathTemplate)) {
                    "Target '$target' runtime script '$scriptPath' must map $side to '$expectedPathTemplate'."
                }
            }
        }

        val selectedTarget = repository.resolve("versions/current").readText().trim()
        check(selectedTarget == activeTarget.get()) {
            "versions/current must contain '${activeTarget.get()}', found '$selectedTarget'."
        }

        val sourceDirectory = repository.resolve("src")
        val actualRoots =
            sourceDirectory
                .listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name }
                ?.sorted()
                .orEmpty()
        val expectedRoots = expectedSourceRoots.get().sorted()
        check(actualRoots == expectedRoots) {
            "Source roots must be exactly $expectedRoots, found $actualRoots."
        }

        targetSourceRoots.get().forEach { specification ->
            val (target, encodedRoots) = specification.split('=', limit = 2)
            val roots = encodedRoots.split(',').map(repository::resolve)
            val owners = mutableMapOf<String, MutableList<String>>()
            roots.filter(File::isDirectory).forEach { root ->
                root
                    .walkTopDown()
                    .filter { it.isFile && it.extension == "java" }
                    .forEach { source ->
                        val relative = source.relativeTo(root).invariantSeparatorsPath
                        owners.getOrPut(relative) { mutableListOf() }.add(root.relativeTo(repository).invariantSeparatorsPath)
                    }
            }
            val duplicates = owners.filterValues { it.size > 1 }
            check(duplicates.isEmpty()) {
                "Target '$target' contains duplicate Java source paths: $duplicates"
            }
        }
    }
}

val targetIds =
    listOf(
        "forge-1.20.1",
        "neoforge-1.21.1",
        "neoforge-26.1.2",
        "fabric-26.2",
        "neoforge-26.2",
    )

tasks.register<Delete>("clean") {
    group = "build"
    description = "Deletes reproducible root build output without touching runtime instances."
    delete(layout.buildDirectory)
}

tasks.register("spotlessApply") {
    group = "formatting"
    description = "Applies repository formatting through the non-buildable formatting project."
    dependsOn(gradle.includedBuild("mobends-formatting").task(":spotlessApply"))
}

tasks.register("spotlessCheck") {
    group = "verification"
    description = "Checks repository formatting through the non-buildable formatting project."
    dependsOn(gradle.includedBuild("mobends-formatting").task(":spotlessCheck"))
}

val verifySourceLayout =
    tasks.register<VerifySourceLayoutTask>("verifySourceLayout") {
        group = "verification"
        description = "Verifies Loader-first targets, family source roots, and per-target Java source uniqueness."
        repositoryPath.set(layout.projectDirectory.asFile.absolutePath)
        activeTarget.set("fabric-26.2")
        expectedTargets.set(targetIds)
        expectedTargetStatuses.set(
            listOf(
                "forge-1.20.1=planned",
                "neoforge-1.21.1=planned",
                "neoforge-26.1.2=planned",
                "fabric-26.2=implemented",
                "neoforge-26.2=planned",
            ),
        )
        expectedSourceRoots.set(
            listOf(
                "fabric",
                "forge",
                "main",
                "minecraft-1x",
                "minecraft-1x-test",
                "minecraft-26x",
                "minecraft-26x-test",
                "neoforge-1x",
                "neoforge-26x",
                "neoforge-common",
                "test",
            ),
        )
        targetSourceRoots.set(
            listOf(
                "forge-1.20.1=src/main/java,src/minecraft-1x/java,src/forge/java,versions/forge-1.20.1/src/main/java",
                "neoforge-1.21.1=src/main/java,src/minecraft-1x/java,src/neoforge-common/java,src/neoforge-1x/java,versions/neoforge-1.21.1/src/main/java",
                "neoforge-26.1.2=src/main/java,src/minecraft-26x/java,src/neoforge-common/java,src/neoforge-26x/java,versions/neoforge-26.1.2/src/main/java",
                "fabric-26.2=src/main/java,src/minecraft-26x/java,src/fabric/java,versions/fabric-26.2/src/main/java",
                "neoforge-26.2=src/main/java,src/minecraft-26x/java,src/neoforge-common/java,src/neoforge-26x/java,versions/neoforge-26.2/src/main/java",
            ),
        )
        targetRuntimeScripts.set(
            listOf(
                "forge-1.20.1=build.forge.gradle.kts",
                "neoforge-1.21.1=build.neoforge.gradle.kts",
                "neoforge-26.1.2=build.neoforge.gradle.kts",
                "fabric-26.2=build.fabric.gradle.kts",
                "neoforge-26.2=build.neoforge.gradle.kts",
            ),
        )
    }

val implementedTargets: ProjectNode.() -> Boolean = {
    project.property("target.status").toString() == "implemented"
}
val targetContracts = stonecutter.tasks.named("verifyTargetContract")
val targetTests = stonecutter.tasks.named("test", implementedTargets)
val targetChecks = stonecutter.tasks.named("check", implementedTargets)
val targetBuilds = stonecutter.tasks.named("build", implementedTargets)
val targetArtifacts = stonecutter.tasks.named("collectArtifact", implementedTargets)

tasks.register("verifyTargets") {
    group = "verification"
    description = "Verifies the naming, source layout, status, and runtime capabilities of every target."
    dependsOn(verifySourceLayout, targetContracts)
}

tasks.register("testAll") {
    group = "verification"
    description = "Runs tests for every implemented Stonecutter target."
    dependsOn("verifyTargets", targetTests)
}

tasks.register("checkAll") {
    group = "verification"
    description = "Runs checks for every implemented Stonecutter target."
    dependsOn("spotlessCheck", "verifyTargets", targetChecks)
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds every implemented Stonecutter target."
    dependsOn("verifyTargets", targetBuilds)
}

tasks.register("collectArtifacts") {
    group = "build"
    description = "Collects production artifacts from every implemented Stonecutter target."
    dependsOn("verifyTargets", targetArtifacts)
}
