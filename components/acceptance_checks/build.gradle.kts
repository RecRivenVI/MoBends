plugins { java }

dependencies {
    implementation(libs.gson)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

val verifyStartup by
    tasks.registering(JavaExec::class) {
        group = "verification"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass = "io.github.recrivenvi.mobends.validation.StartupCheck"
        args(layout.projectDirectory.dir("../..").asFile.absolutePath, "smoke-reference_client")
    }

val verifyPreservation by
    tasks.registering(JavaExec::class) {
        group = "verification"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass = "io.github.recrivenvi.mobends.validation.PreservationCheck"
        args(
            layout.projectDirectory.dir("../..").asFile.absolutePath,
            "validations/migration-template/task/preservation.json",
            "validations/migration-template/result/preservation.json",
        )
    }

val verifyManifest by
    tasks.registering(JavaExec::class) {
        group = "verification"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass = "io.github.recrivenvi.mobends.validation.PreservationCheck"
        args(
            layout.projectDirectory.dir("../..").asFile.absolutePath,
            "validations/migration-template/task/preservation.json",
            "validations/migration-template/result/preservation.json",
            "structure",
        )
    }

tasks.check { dependsOn(verifyManifest) }

fun clientOptions(taskName: String, instance: String, mode: String) {
    tasks.register<JavaExec>(taskName) {
        group = "verification"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass = "io.github.recrivenvi.mobends.validation.PrepareClientOptions"
        args(layout.projectDirectory.dir("../..").asFile.absolutePath, instance, mode)
    }
}

clientOptions(
    "prepareMcpBootstrap",
    "validations/smoke-mcp_bootstrap/instance/26.3-neoforge/client",
    "automated",
)

clientOptions(
    "prepareMcpAnimation",
    "validations/smoke-mcp_animation/instance/26.3-neoforge/client",
    "automated",
)

clientOptions("prepareUserAcceptance", "instances/26.3-neoforge/client", "handoff")

tasks.register<JavaExec>("verifyMcpSmoke") {
    group = "verification"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.recrivenvi.mobends.validation.McpSmokeCheck"
    args(layout.projectDirectory.dir("../..").asFile.absolutePath)
}
