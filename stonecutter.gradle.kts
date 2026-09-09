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

stonecutter active "26.2-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "forge", "neoforge")
        this["forge_like"] = loader == "forge" || loader == "neoforge"
        this["target_1_20_1_forge"] = current.project == "1.20.1-forge"
        this["target_1_21_1_neoforge"] = current.project == "1.21.1-neoforge"
        this["target_26_1_2_neoforge"] = current.project == "26.1.2-neoforge"
        this["target_26_2_fabric"] = current.project == "26.2-fabric"
        this["target_26_2_neoforge"] = current.project == "26.2-neoforge"
    }
}

stonecutter.tasks.order("createMinecraftArtifacts") {
    metadata.project.endsWith("-forge") || metadata.project.endsWith("-neoforge")
}

val targetIds =
    listOf(
        "1.20.1-forge",
        "1.21.1-neoforge",
        "26.1.2-neoforge",
        "26.2-fabric",
        "26.2-neoforge",
    )

abstract class VerifySourceLayoutTask : DefaultTask() {
    @get:Input abstract val repositoryPath: Property<String>

    @get:Input abstract val expectedTargets: ListProperty<String>

    @TaskAction
    fun verify() {
        val root = File(repositoryPath.get())
        val actualTargets =
            root
                .resolve("versions")
                .listFiles()
                .orEmpty()
                .filter { it.isDirectory }
                .map { it.name }
                .sorted()
        check(actualTargets == expectedTargets.get().sorted()) {
            "Stonecutter nodes must be exactly ${expectedTargets.get().sorted()}, found $actualTargets."
        }
        check(root.resolve("versions/current").readText().trim() == "26.2-fabric") {
            "versions/current must identify 26.2-fabric."
        }
        val authoredRoots =
            root
                .resolve("src")
                .listFiles()
                .orEmpty()
                .filter { it.isDirectory }
                .map { it.name }
                .sorted()
        check(authoredRoots == listOf("main", "test")) {
            "Authored roots must be exactly src/main and src/test, found $authoredRoots."
        }
        val nodeSources =
            actualTargets.flatMap { target ->
                root
                    .resolve("versions/$target/src")
                    .takeIf(File::exists)
                    ?.walkTopDown()
                    ?.filter(File::isFile)
                    ?.toList()
                    .orEmpty()
            }
        check(nodeSources.isEmpty()) {
            "No authored source is allowed under versions/<node>/src: $nodeSources"
        }
        val productionSources =
            root
                .resolve("src/main/java")
                .walkTopDown()
                .filter { it.isFile && it.extension == "java" }
                .toList()
        check(productionSources.isNotEmpty()) { "src/main/java must contain production source." }
        check(
            productionSources.none { source ->
                source.invariantSeparatorsPath.contains("/goblinbob/mobends/") ||
                    source.readText().contains("goblinbob.mobends")
            },
        ) {
            "The old goblinbob.mobends Java identity must not remain."
        }
        check(
            productionSources.all { source ->
                source.readText().contains("package io.github.recrivenvi.mobends")
            },
        ) {
            "Every production source must use io.github.recrivenvi.mobends."
        }
        check(!root.resolve(".github/workflows").exists()) {
            "Old-port GitHub workflows must not remain."
        }
    }
}

tasks.register<Delete>("clean") {
    group = "build"
    delete(layout.buildDirectory)
}

tasks.register("spotlessApply") {
    group = "formatting"
    dependsOn(gradle.includedBuild("mobends-formatting").task(":spotlessApply"))
}

tasks.register("spotlessCheck") {
    group = "verification"
    dependsOn(gradle.includedBuild("mobends-formatting").task(":spotlessCheck"))
}

val verifySourceLayout =
    tasks.register<VerifySourceLayoutTask>("verifySourceLayout") {
        group = "verification"
        repositoryPath.set(layout.projectDirectory.asFile.absolutePath)
        expectedTargets.set(targetIds)
    }

tasks.register<Exec>("stageRepositoryProfiles") {
    group = "verification"
    description = "Stage every repository-local MoBends runtime profile."
    commandLine(
        "pwsh.exe",
        "-NoLogo",
        "-NoProfile",
        "-NonInteractive",
        "-ExecutionPolicy",
        "Bypass",
        "-File",
        layout.projectDirectory
            .file("eng/runtime/Stage-Profile.ps1")
            .asFile.absolutePath,
    )
    outputs.upToDateWhen { false }
}

val verifyRepositoryProfiles =
    tasks.register<Exec>("verifyRepositoryProfiles") {
        group = "verification"
        description = "Verify all repository-local MoBends profile definitions and existing instances."
        commandLine(
            "pwsh.exe",
            "-NoLogo",
            "-NoProfile",
            "-NonInteractive",
            "-ExecutionPolicy",
            "Bypass",
            "-File",
            layout.projectDirectory
                .file("eng/runtime/Verify-Profiles.ps1")
                .asFile.absolutePath,
        )
        outputs.upToDateWhen { false }
    }

tasks.register("verifyTargets") {
    group = "verification"
    dependsOn(verifySourceLayout, verifyRepositoryProfiles)
}

tasks.register("testAll") {
    group = "verification"
    dependsOn("verifyTargets", stonecutter.tasks.named("test"))
}

tasks.register("checkAll") {
    group = "verification"
    dependsOn("spotlessCheck", "verifyTargets", stonecutter.tasks.named("check"))
}

tasks.register("buildAll") {
    group = "build"
    dependsOn("verifyTargets", stonecutter.tasks.named("build"))
}

tasks.register("collectArtifacts") {
    group = "build"
    dependsOn("verifyTargets", stonecutter.tasks.named("buildAndCollect"))
}
