plugins { id("net.neoforged.moddev.legacyforge") }

mixin {
    add(sourceSets.main.get(), "mobends-forge.refmap.json")
    config("mobends-forge.mixins.json")
}

tasks.jar { manifest.attributes["MixinConfigs"] = "mobends-forge.mixins.json" }

sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

dependencies {
    annotationProcessor(variantOf(libs.mixin.processor) { classifier("processor") })
    compileOnly(libs.jsr305)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

java { withSourcesJar() }

// FML owns packages per Mod: component classes must share the development Mod's module.
val productRuntime by sourceSets.creating {
    java.setSrcDirs(emptyList<String>())
    resources.setSrcDirs(emptyList<String>())
}
val prepareProductRuntime by
    tasks.registering(Sync::class) {
        val products = configurations.getByName("productComponents")
        dependsOn(products)
        from(products.map { zipTree(it) })
        exclude("META-INF/MANIFEST.MF", "**/module-info.class")
        into(layout.buildDirectory.dir("product-runtime"))
    }

productRuntime.output.dir(
    mapOf("builtBy" to prepareProductRuntime),
    prepareProductRuntime.map { it.destinationDir },
)

legacyForge.mods.configureEach {
    if (name == target.fact("mod_id")) sourceSet(productRuntime)
}

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
