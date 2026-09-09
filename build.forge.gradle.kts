import groovy.json.JsonSlurper
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.140"
    id("forge-mutex")
}

group = project.property("mod.group") as String
version = "${project.property("mod.version")}+${sc.current.version}"
base.archivesName = "mobends-forge"

val requiredJava = JavaVersion.VERSION_17
val nodeName = sc.current.project
val primaryProfile =
    providers
        .gradleProperty("runtime.profile")
        .orElse("s2")
        .get()
        .lowercase()
val multiplayerProfile =
    providers
        .gradleProperty("runtime.multiplayerProfile")
        .orElse("m1")
        .get()
        .lowercase()
val runtimeWorld = providers.gradleProperty("runtime.world").orNull

data class RuntimeProfile(
    val playerName: String,
    val gameDirectory: File,
    val modsDirectory: File,
    val requiredJvmArgs: List<String>,
    val recordFile: File,
)

fun loadRuntimeProfile(
    role: String,
    profile: String,
): RuntimeProfile {
    val record = rootProject.layout.projectDirectory.file("run/profiles/$nodeName/client/$role/$profile.json")
    val values = JsonSlurper().parse(record.asFile) as? Map<*, *> ?: error("Invalid profile: ${record.asFile}")

    fun field(name: String): String = values[name] as? String ?: error("Missing $name in ${record.asFile}")

    fun repositoryFile(name: String): File {
        val relative = field(name)
        check(!File(relative).isAbsolute) { "Profile $name must be repository-relative: ${record.asFile}" }
        val resolved = rootProject.file(relative).canonicalFile
        check(resolved.toPath().startsWith(rootProject.projectDir.canonicalFile.toPath())) {
            "Profile $name escaped the repository: ${record.asFile}"
        }
        return resolved
    }

    val gameDirectory = repositoryFile("gameDirectory")
    val modsDirectory = repositoryFile("modsDirectory")
    check(modsDirectory == gameDirectory.resolve("mods")) {
        "Profile modsDirectory must equal gameDirectory/mods."
    }
    check(gameDirectory.toPath().startsWith(rootProject.file("run/instances").canonicalFile.toPath())) {
        "Profile gameDirectory must be under run/instances: ${record.asFile}"
    }
    val requiredJvmArgs =
        (values["requiredJvmArgs"] as? List<*>)
            ?.map { it as? String ?: error("Invalid requiredJvmArgs in ${record.asFile}") }
            ?: error("Missing requiredJvmArgs in ${record.asFile}")
    return RuntimeProfile(field("playerName"), gameDirectory, modsDirectory, requiredJvmArgs, record.asFile)
}

val primary = loadRuntimeProfile("primary", primaryProfile)
val secondary = loadRuntimeProfile("secondary", multiplayerProfile)

fun registerProfileVerification(
    label: String,
    runtimeProfile: RuntimeProfile,
): TaskProvider<Exec> {
    val stage =
        tasks.register<Exec>("stage${label}RuntimeProfile") {
            group = "verification"
            commandLine(
                "pwsh.exe",
                "-NoLogo",
                "-NoProfile",
                "-NonInteractive",
                "-ExecutionPolicy",
                "Bypass",
                "-File",
                rootProject.file("eng/runtime/Stage-Profile.ps1").absolutePath,
                "-ProfileRecord",
                runtimeProfile.recordFile.absolutePath,
            )
            outputs.upToDateWhen { false }
        }
    return tasks.register<Exec>("verify${label}RuntimeProfile") {
        group = "verification"
        dependsOn(stage)
        commandLine(
            "pwsh.exe",
            "-NoLogo",
            "-NoProfile",
            "-NonInteractive",
            "-ExecutionPolicy",
            "Bypass",
            "-File",
            rootProject.file("eng/runtime/Verify-Profiles.ps1").absolutePath,
            "-ProfileRecord",
            runtimeProfile.recordFile.absolutePath,
        )
        outputs.upToDateWhen { false }
    }
}

val primaryProfileVerification = registerProfileVerification("Primary", primary)
val multiplayerProfileVerification = registerProfileVerification("Multiplayer", secondary)

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation(platform("org.junit:junit-bom:${project.property("deps.junit")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

fun configureClient(
    run: net.neoforged.moddevgradle.dsl.RunModel,
    profile: RuntimeProfile,
) {
    run.gameDirectory = profile.gameDirectory
    run.programArgument("--username")
    run.programArgument(profile.playerName)
    run.programArgument("--width")
    run.programArgument("2560")
    run.programArgument("--height")
    run.programArgument("1440")
    if (profile.modsDirectory
            .listFiles()
            .orEmpty()
            .any { it.name.startsWith("CustomSkinLoader_") }
    ) {
        run.systemProperty("customskinloader.ignorePatchFailure", "true")
    }
    profile.requiredJvmArgs.forEach { run.jvmArgument(it) }
    run.client()
    val profileMods = rootProject.fileTree(profile.modsDirectory) { include("*.jar") }
    run.additionalRuntimeClasspathConfiguration.dependencies.add(rootProject.dependencies.create(profileMods))
}

legacyForge {
    version = project.property("deps.forge_loader") as String
    validateAccessTransformers = true
    mods {
        register("mobends") {
            sourceSet(sourceSets.main.get())
        }
    }
    runs {
        register("client") {
            configureClient(this, primary)
            if (runtimeWorld != null) {
                programArgument("--quickPlaySingleplayer")
                programArgument(runtimeWorld)
            }
        }
        register("clientMultiplayer") {
            configureClient(this, secondary)
        }
    }
}

mixin {
    add(sourceSets.main.get(), "mobends-forge.refmap.json")
    config("mobends-forge.mixins.json")
}

afterEvaluate {
    tasks.findByName("runClient")?.dependsOn(primaryProfileVerification)
    tasks.findByName("runClientMultiplayer")?.dependsOn(multiplayerProfileVerification)
    tasks.findByName("runServer")?.apply {
        enabled = false
        group = "unsupported runtime"
        description = "Disabled because MoBends only has client runtime roles."
    }
}

java {
    withSourcesJar()
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    toolchain.languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
}

sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(requiredJava.majorVersion.toInt())
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "5000"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("mobends.minecraftVersion", sc.current.version)
    systemProperty("mobends.targetId", nodeName)
    systemProperty("mobends.expectedVersion", project.version.toString())
}

tasks.processResources {
    val props =
        mapOf(
            "mod_id" to project.property("mod.id"),
            "mod_name" to project.property("mod.name"),
            "mod_version" to project.version,
            "mod_license" to project.property("mod.license"),
            "mod_authors" to project.property("mod.authors"),
            "mod_description" to project.property("mod.description"),
            "loader_version_range" to project.property("target.loader.compatibility"),
            "minecraft_version_range" to project.property("target.minecraft.compatibility"),
            "java_version" to requiredJava.majorVersion,
            "pack_format_fields" to "\"pack_format\": 15",
            "platform_service" to
                "io.github.recrivenvi.mobends.forge.platform.ForgePlatformServices",
        )
    inputs.properties(props)
    filesMatching(
        listOf(
            "META-INF/mods.toml",
            "pack.mcmeta",
            "META-INF/services/io.github.recrivenvi.mobends.api.platform.IPlatformServices",
            "mobends_version.properties",
            "mobends-forge.mixins.json",
        ),
    ) {
        expand(props)
    }
    exclude(
        "fabric.mod.json",
        "META-INF/neoforge.mods.toml",
        "mobends-fabric.mixins.json",
        "mobends-neoforge.mixins.json",
    )
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.named<Jar>("jar") {
    manifest.attributes["MixinConfigs"] = "mobends-forge.mixins.json"
    from(rootProject.file("LICENSE")) {
        rename("LICENSE", "LICENSE_mobends")
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    inputs.property("version", project.property("mod.version"))
    dependsOn("reobfJar")
    from(
        layout.buildDirectory.file("libs/${base.archivesName.get()}-${project.version}.jar"),
        tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile },
    )
    into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
}
