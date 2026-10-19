plugins {
    base
    id("com.diffplug.spotless") version "8.9.0"
}

apply(from = "gradle/formatting.gradle")

repositories {
    mavenCentral()
}

val targets = listOf("1.20.1-forge", "1.21.1-neoforge", "26.1.2-neoforge", "26.2-fabric", "26.2-neoforge")
val verifyRepositoryProfiles =
    tasks.register<Exec>("verifyRepositoryProfiles") {
        group = "verification"
        commandLine(
            "pwsh.exe",
            "-NoProfile",
            "-File",
            layout.projectDirectory
                .file("eng/runtime/Verify-Profiles.ps1")
                .asFile.absolutePath,
        )
    }
tasks.register<Exec>("stageRepositoryProfiles") {
    group = "verification"
    commandLine(
        "pwsh.exe",
        "-NoProfile",
        "-File",
        layout.projectDirectory
            .file("eng/runtime/Stage-Profile.ps1")
            .asFile.absolutePath,
    )
}
val verifyTargets =
    tasks.register<Exec>("verifyTargets") {
        group = "verification"
        dependsOn(verifyRepositoryProfiles)
        commandLine(
            "pwsh.exe",
            "-NoProfile",
            "-File",
            layout.projectDirectory
                .file("eng/Verify-Layout.ps1")
                .asFile.absolutePath,
        )
    }
for ((aggregate, task) in mapOf(
    "testAll" to "test",
    "checkAll" to "check",
    "buildAll" to "build",
    "collectArtifacts" to "buildAndCollect",
)) {
    tasks.register(aggregate) {
        group = if (task == "test" || task == "check") "verification" else "build"
        dependsOn(verifyTargets, targets.map { ":version:$it:$task" })
        if (task == "check") dependsOn("spotlessCheck")
    }
}
