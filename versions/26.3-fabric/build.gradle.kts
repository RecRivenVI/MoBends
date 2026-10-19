plugins { id("net.fabricmc.fabric-loom") }

repositories { maven("https://maven.terraformersmc.com/releases/") }

dependencies {
    implementation(target.module("net.fabricmc.fabric-api:fabric-api", "fabric_api_version"))
    clientCompileOnly(target.module("com.terraformersmc:modmenu", "modmenu_version"))
    testRuntimeOnly(target.module("com.terraformersmc:modmenu", "modmenu_version"))
}

val client = sourceSets.named("client")

sourceSets.test {
    compileClasspath += client.get().output + client.get().compileClasspath
    runtimeClasspath += client.get().output + client.get().runtimeClasspath
}

dependencies {
    compileOnly(libs.jsr305)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

java { withSourcesJar() }

tasks.withType<Test>().configureEach {
    val testWorkingDirectory = layout.buildDirectory.dir("test-work")
    workingDir(testWorkingDirectory)
    doFirst { testWorkingDirectory.get().asFile.mkdirs() }
    useJUnitPlatform()
    systemProperty("mobends.minecraftVersion", target.fact("minecraft_version"))
    systemProperty("mobends.targetId", target.name)
    systemProperty("mobends.expectedVersion", target.fact("mod_version"))
}

tasks.processResources {
    val version = target.fact("mod_version")
    inputs.property("mod_version", version)
    filesMatching("mobends_version.properties") { expand("mod_version" to version) }
}
