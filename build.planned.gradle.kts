apply(from = rootProject.file("gradle/stonecutter-target-conventions.gradle.kts"))

val targetId = sc.current.project
val blockedTaskNames =
    setOf(
        "assemble",
        "build",
        "buildDependents",
        "buildNeeded",
        "check",
        "classes",
        "collectArtifact",
        "compileJava",
        "compileTestJava",
        "jar",
        "javadoc",
        "processResources",
        "processTestResources",
        "publish",
        "publishToMavenLocal",
        "remapJar",
        "reobfJar",
        "runClient",
        "runServer",
        "sourcesJar",
        "stonecutterGenerate",
        "stonecutterGenerateTest",
        "stonecutterMerge",
        "stonecutterMergeTest",
        "stonecutterSaveNodeModel",
        "test",
        "testClasses",
    )

fun plannedTargetFailure(taskName: String): GradleException =
    GradleException(
        "Target '$targetId' is 'planned'; task '$taskName' is unavailable. " +
            "Set target.status to 'implemented' in stonecutter.properties.toml to reactivate " +
            "its Minecraft/loader build and runtime tasks.",
    )

blockedTaskNames.forEach { taskName ->
    if (tasks.findByName(taskName) == null) {
        tasks.register(taskName) {
            group = "planned target"
            description = "Fails because $targetId is planned."
        }
    }
}

tasks.matching { it.name in blockedTaskNames }.configureEach {
    group = "planned target"
    description = "Fails because $targetId is planned."
    setDependsOn(emptyList<Any>())
    doFirst { throw plannedTargetFailure(name) }
}

tasks.named("clean") {
    group = "build"
    description = "Deletes the generated output for planned Target $targetId."
}
