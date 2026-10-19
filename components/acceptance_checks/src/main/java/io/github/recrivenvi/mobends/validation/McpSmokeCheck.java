package io.github.recrivenvi.mobends.validation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;

/** Checks saved observations without granting MCP authority or making game mutations. */
public final class McpSmokeCheck {
    private McpSmokeCheck() {}

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments[0]).toAbsolutePath().normalize();
        Path bootstrap = root.resolve("validations/smoke-mcp_bootstrap");
        Path animation = root.resolve("validations/smoke-mcp_animation");
        JsonObject session = json(bootstrap.resolve("result/read-session.json"));
        require(session.get("mode").getAsString().equals("READ"), "Bootstrap READ");
        require(session.get("minecraft").getAsString().equals("26.3"), "Minecraft identity");
        require(session.get("loader").getAsString().equals("neoforge"), "Loader identity");
        Path bootstrapGame = bootstrap.resolve("instance/26.3-neoforge/client");
        String bootstrapLog = Files.readString(bootstrapGame.resolve("logs/latest.log"));
        require(bootstrapLog.contains("mod/minecraft_protocol_probe"), "MCP-only resources loaded");
        require(!bootstrapLog.contains("mod/mobends"), "Product absent in bootstrap");
        require(bootstrapLog.contains("Stopping!"), "Bootstrap stopped normally");
        require(
                !Files.exists(bootstrapGame.resolve("minecraft-protocol/endpoint.json")),
                "Bootstrap endpoint removed");
        Path result = animation.resolve("result");
        JsonObject before = json(result.resolve("player-before.json"));
        JsonObject moving = json(result.resolve("player-moving.json"));
        JsonObject after = json(result.resolve("player-final.json"));
        double displacement = distance(before, moving);
        require(displacement > 0.25, "Actual player movement");
        require(
                after.get("onGround").getAsBoolean() && !after.get("flying").getAsBoolean(),
                "Normal final player state");
        JsonObject clean = json(result.resolve("final-input-clean.json"));
        require(clean.get("passed").getAsBoolean(), "MCP input cleanup");
        JsonObject state = json(result.resolve("mcp-state.json"));
        require(
                state.get("registeredBenders").getAsInt() == 7
                        && state.get("playerAnimated").getAsBoolean(),
                "Product animation registration");
        require(
                !state.get("noGravity").getAsBoolean()
                        && !state.get("noPhysics").getAsBoolean()
                        && !state.get("flying").getAsBoolean()
                        && state.get("onGround").getAsBoolean(),
                "Internal physics state");
        require(state.get("masterVolume").getAsDouble() == 0.0, "Automated master mute");
        JsonObject active = json(result.resolve("mcp-walking-active-probe.json"));
        double activeKnee =
                active.getAsJsonObject("pose")
                        .getAsJsonArray("leftKneeQuaternion")
                        .get(0)
                        .getAsDouble();
        double idleKnee =
                state.getAsJsonObject("pose")
                        .getAsJsonArray("leftKneeQuaternion")
                        .get(0)
                        .getAsDouble();
        require(
                Double.isFinite(activeKnee) && Math.abs(activeKnee - idleKnee) > 0.001,
                "Joint animation progressed");
        for (String name : new String[] {"mcp-idle.png", "mcp-walking-active.png"}) {
            var frame = ImageIO.read(result.resolve(name).toFile());
            require(
                    frame != null && frame.getWidth() == 1920 && frame.getHeight() == 1080,
                    "Capture resolution");
        }
        Path game = animation.resolve("instance/26.3-neoforge/client");
        String log = Files.readString(game.resolve("logs/latest.log"));
        require(
                log.contains("Saving worlds") && log.contains("Stopping!"),
                "Normal save and shutdown");
        require(
                !log.contains("Mixin apply failed") && !log.contains("NoClassDefFoundError"),
                "No new fatal loading failure");
        require(
                !Files.exists(game.resolve("minecraft-protocol/endpoint.json")),
                "Product endpoint removed");
        long pid = state.get("pid").getAsLong();
        require(
                ProcessHandle.of(pid).map(process -> !process.isAlive()).orElse(true),
                "Owned game exited");
        Files.copy(
                game.resolve("logs/latest.log"),
                result.resolve("runtime.log"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.copy(
                bootstrapGame.resolve("logs/latest.log"),
                bootstrap.resolve("result/runtime.log"),
                StandardCopyOption.REPLACE_EXISTING);
        var report = new LinkedHashMap<String, Object>();
        report.put("status", "PASS: MCP bootstrap and bounded product smoke");
        report.put("snapshot", "20261019-1245-bfdda69c");
        report.put("jarSha256", "8b443fa8925a1305360fb907b4e4ed7a95bc536ef4414f3cc16f57d350bee158");
        report.put("automatedResolution", "1920x1080");
        report.put("masterVolume", 0.0);
        report.put("observedMovementMetres", displacement);
        report.put("returnDistanceMetres", distance(before, after));
        report.put("inputReleased", true);
        report.put("normalShutdown", true);
        report.put("userVisualAcceptance", "PENDING");
        Files.writeString(
                result.resolve("mcp-check.json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(report) + "\n");
        System.out.println(
                "MCP bootstrap, input, capture, animation observation and normal shutdown: PASS");
    }

    private static JsonObject json(Path path) throws Exception {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }

    private static double distance(JsonObject first, JsonObject second) {
        double squared = 0;
        for (String axis : new String[] {"x", "y", "z"}) {
            double delta = first.get(axis).getAsDouble() - second.get(axis).getAsDouble();
            squared += delta * delta;
        }
        return Math.sqrt(squared);
    }

    private static void require(boolean condition, String label) {
        if (!condition) throw new IllegalStateException(label);
    }
}
