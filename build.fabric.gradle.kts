import groovy.json.JsonSlurper
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

plugins {
    id("dev.kikugie.loom-back-compat")
}

group = project.property("mod.group") as String
version = "${project.property("mod.version")}+${sc.current.version}"
base.archivesName = "mobends-fabric"

val requiredJava = JavaVersion.VERSION_25
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
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${project.property("deps.fabric_loader")}")
    modImplementation(
        "net.fabricmc.fabric-api:fabric-api:${project.property("deps.fabric_api")}",
    )
    modCompileOnly("com.terraformersmc:modmenu:${project.property("deps.modmenu")}")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation(platform("org.junit:junit-bom:${project.property("deps.junit")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("com.terraformersmc:modmenu:${project.property("deps.modmenu")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

loom {
    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
    }
    runConfigs.remove(runConfigs.getByName("server"))
    runConfigs {
        named("client") {
            runDirectory = primary.gameDirectory
            programArgs(
                "--username",
                primary.playerName,
                "--width",
                "2560",
                "--height",
                "1440",
            )
            if (primaryProfile != "s1") {
                vmArg("-Dfabric.addMods=${primary.modsDirectory.absolutePath}")
            }
            primary.requiredJvmArgs.forEach(::vmArg)
            if (runtimeWorld != null) {
                programArgs("--quickPlaySingleplayer", runtimeWorld)
            }
        }
        create("clientMultiplayer") {
            client()
            runDirectory = secondary.gameDirectory
            programArgs(
                "--username",
                secondary.playerName,
                "--width",
                "2560",
                "--height",
                "1440",
            )
            vmArg("-Dfabric.addMods=${secondary.modsDirectory.absolutePath}")
            secondary.requiredJvmArgs.forEach(::vmArg)
        }
    }
}

afterEvaluate {
    tasks.findByName("runClient")?.dependsOn(primaryProfileVerification)
    tasks.findByName("runClientMultiplayer")?.dependsOn(multiplayerProfileVerification)
    listOf("runServer", "runClientRenderDoc").forEach { taskName ->
        tasks.findByName(taskName)?.apply {
            enabled = false
            group = "unsupported runtime"
            description = "Disabled because MoBends only has client runtime roles."
        }
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
            "pack_format_fields" to
                "\"min_format\": [84, 0],\n    \"max_format\": [84, 0]",
            "platform_service" to
                "io.github.recrivenvi.mobends.fabric.platform.FabricPlatformServices",
        )
    inputs.properties(props)
    filesMatching(
        listOf(
            "fabric.mod.json",
            "pack.mcmeta",
            "META-INF/services/io.github.recrivenvi.mobends.api.platform.IPlatformServices",
            "mobends_version.properties",
            "mobends-fabric.mixins.json",
        ),
    ) {
        expand(props)
    }
    exclude(
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "mobends-forge.mixins.json",
        "mobends-forge.accesswidener",
        "mobends-neoforge.mixins.json",
    )
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename("LICENSE", "LICENSE_mobends")
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    inputs.property("version", project.property("mod.version"))
    from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
}
