import groovy.json.JsonSlurper
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

plugins {
    id("net.neoforged.moddev") version "2.0.140"
    id("neoforge-mutex")
}

group = project.property("mod.group") as String
version = "${project.property("mod.version")}+${project.property("target.minecraft")}"
base.archivesName = "mobends-neoforge"

val requiredJava =
    when {
        project.property("target.java") == "25" -> JavaVersion.VERSION_25
        else -> JavaVersion.VERSION_21
    }
val nodeName = project.name
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
    val record =
        rootProject.layout.projectDirectory.file(
            "validations/regression-client-runtime/task/profiles/$nodeName/client/$role/$profile.json",
        )
    val values =
        JsonSlurper().parseText(providers.fileContents(record).asText.get()) as? Map<*, *> ?: error("Invalid profile: ${record.asFile}")

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
    check(gameDirectory.toPath().startsWith(rootProject.file("validations/regression-client-runtime/instance").canonicalFile.toPath())) {
        "Profile gameDirectory must be under validations/regression-client-runtime/instance: ${record.asFile}"
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
    if (nodeName == "1.21.1-neoforge") {
        val profileMods = rootProject.fileTree(profile.modsDirectory) { include("*.jar") }
        run.additionalRuntimeClasspathConfiguration.dependencies.add(rootProject.dependencies.create(profileMods))
    }
}

neoForge {
    version = project.property("deps.neo_loader") as String
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
    systemProperty("mobends.minecraftVersion", project.property("target.minecraft"))
    systemProperty("mobends.targetId", nodeName)
    systemProperty("mobends.expectedVersion", project.version.toString())
}

tasks.processResources {
    val legacy = project.name == "1.21.1-neoforge"
    val packFields =
        if (legacy) {
            "\"pack_format\": 34"
        } else {
            "\"min_format\": [84, 0],\n    \"max_format\": [84, 0]"
        }
    val clientMixins =
        if (legacy) {
            listOf(
                "AgeableListModelMixin",
                "SpiderModelMixin",
                "RenderLayerMixin",
                "PlayerRendererSwimMixin",
                "LivingEntityRendererAccessor",
                "armor.ArmorTrimLayerMixin",
                "armor.ElytraLayerMixin",
                "armor.ModelPartAccessor",
                "armor.HumanoidArmorLayerMixin",
            )
        } else {
            listOf(
                "armor.ModelPartAccessor",
                "AvatarRendererMixin",
                "ArrowRendererMixin",
                "ItemInHandLayerMixin",
                "LivingEntityRendererMixin",
                "ModelFeatureRendererMixin",
                "RenderTypeAccessor",
            )
        }.joinToString(",\n    ") { "\"$it\"" }
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
            "pack_format_fields" to packFields,
            "platform_service" to
                "io.github.recrivenvi.mobends.neoforge.platform.NeoForgePlatformServices",
            "neoforge_loader_header" to
                if (legacy) {
                    "modLoader = \"javafml\"\nloaderVersion = \"${project.property("target.loader.compatibility")}\""
                } else {
                    ""
                },
            "mod_icon_property" to if (project.name == "26.2-neoforge") "iconFile" else "logoFile",
            "mod_icon_path" to
                if (legacy) "icon.png" else "assets/mobends/textures/logo512.png",
            "neoforge_refmap_field" to
                if (legacy) "\"refmap\": \"mobends-neoforge.refmap.json\"," else "",
            "neoforge_client_mixins" to clientMixins,
        )
    inputs.properties(props)
    filesMatching(
        listOf(
            "META-INF/neoforge.mods.toml",
            "pack.mcmeta",
            "META-INF/services/io.github.recrivenvi.mobends.api.platform.IPlatformServices",
            "mobends_version.properties",
            "mobends-neoforge.mixins.json",
        ),
    ) {
        expand(props)
    }
    exclude(
        "fabric.mod.json",
        "META-INF/mods.toml",
        "mobends-fabric.mixins.json",
        "mobends-forge.mixins.json",
        "mobends-forge.accesswidener",
    )
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) {
        rename("LICENSE", "LICENSE_mobends")
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    inputs.property("version", project.property("mod.version"))
    from(
        tasks.named<Jar>("jar").flatMap { it.archiveFile },
        tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile },
    )
    into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
}
