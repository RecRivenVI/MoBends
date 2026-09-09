import com.diffplug.spotless.LineEnding

plugins {
    id("com.diffplug.spotless") version "8.9.0"
}

repositories {
    mavenCentral()
}

layout.buildDirectory = file("build/formatting")

spotless {
    lineEndings = LineEnding.UNIX
    encoding = Charsets.UTF_8

    java {
        target(rootProject.fileTree("src") { include("**/*.java") })
        googleJavaFormat().aosp()
        formatAnnotations()
        removeUnusedImports()
        replaceRegex("tabs", "\t", "    ")
        replaceRegex("stonecutter-directives", "(?m)^(\\s*)// \\?", "\$1//?")
        repeat(4) { pass ->
            replaceRegex(
                "stonecutter-condition-wrap-$pass",
                "(?m)^(\\s*//\\?[^\\r\\n]*)\\r?\\n\\s*//\\s+(target_[^\\r\\n]*)$",
                "\$1 \$2",
            )
        }
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target(
            rootProject.file("settings.gradle.kts"),
            rootProject.file("stonecutter.gradle.kts"),
            rootProject.file("build.fabric.gradle.kts"),
            rootProject.file("build.forge.gradle.kts"),
            rootProject.file("build.neoforge.gradle.kts"),
            rootProject.fileTree("gradle") { include("**/*.gradle.kts") },
        )
        ktlint()
        replaceRegex("tabs", "\t", "    ")
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("structuredText") {
        target(
            rootProject.file("gradle.properties"),
            rootProject.file("stonecutter.properties.toml"),
            rootProject.fileTree(".github") {
                include("**/*.json", "**/*.yml", "**/*.yaml")
            },
            rootProject.fileTree("gradle") {
                include("**/*.gradle", "**/*.properties")
                exclude("wrapper/gradle-wrapper.properties")
            },
            rootProject.fileTree("src") {
                include(
                    "**/*.json",
                    "**/*.json5",
                    "**/*.mcmeta",
                    "**/*.toml",
                    "**/*.yml",
                    "**/*.yaml",
                    "**/*.md",
                    "**/*.gradle",
                    "**/*.properties",
                    "**/*.accesswidener",
                )
            },
        )
        replaceRegex("tabs", "\t", "    ")
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("repositoryFiles") {
        target(
            rootProject.file(".editorconfig"),
            rootProject.file(".gitattributes"),
            rootProject.file(".gitignore"),
        )
        replaceRegex("tabs", "\t", "    ")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
