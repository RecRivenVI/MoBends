pluginManagement {
    includeBuild("gradle/conventions")
    repositories {
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
    }
}

plugins { id("io.github.recrivenvi.conventions") }

rootProject.name = "MoBends"

targets {
    register("1.20.1-forge")
    register("1.21.1-neoforge")
    register("26.1.2-neoforge")
    register("26.3-neoforge")
    register("26.3-fabric")
}

components {
    product("animation_core")
    tool("acceptance_checks")
}

compliance {
    strict = true
    sourceSets("productRuntime")
}
