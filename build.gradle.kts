import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import net.neoforged.moddevgradle.dsl.NeoForgeExtension

// These formats contain LF text only; batch wrappers retain their template CRLF bytes.
allprojects {
    pluginManager.withPlugin("com.diffplug.spotless") {
        extensions.configure<SpotlessExtension> { lineEndings = LineEnding.UNIX }
    }
}

// Bootstrap is deliberately MCP-only; the ordinary validation and daily runs keep the product.
project(":version:26.3-neoforge") {
    pluginManager.withPlugin("net.neoforged.moddev") {
        extensions.configure<NeoForgeExtension> {
            runs.configureEach {
                if (name == "validationSmokeMcpBootstrapClient") loadedMods.set(emptySet())
            }
        }
    }
}
