package io.github.recrivenvi.mobends.validation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Comparator;
import javax.imageio.ImageIO;

/** Checks the bounded startup observation; it does not certify all animation visuals. */
public final class StartupCheck {
    private StartupCheck() {}

    public static void main(String[] arguments) throws Exception {
        Path root = Path.of(arguments[0]).toAbsolutePath().normalize();
        Path validation = root.resolve("validations").resolve(arguments[1]).normalize();
        require(validation.startsWith(root.resolve("validations")), "Invalid validation path");
        Path result = validation.resolve("result");
        JsonObject observation =
                JsonParser.parseString(Files.readString(result.resolve("startup.json")))
                        .getAsJsonObject();
        validate(observation);
        Path instance =
                Path.of(observation.get("gameDirectory").getAsString())
                        .toAbsolutePath()
                        .normalize();
        require(
                instance.startsWith(validation.resolve("instance")),
                "Observation is not from this validation instance");
        String log = Files.readString(instance.resolve("logs/latest.log"));
        require(
                log.contains("Mo' Bends client setup complete") && log.contains("Saving worlds"),
                "Missing setup/save evidence");
        require(
                !log.matches(
                        "(?s).*(Mixin apply failed|InvalidMixinException|InjectionError|NoClassDefFoundError|ModLoadingException).*"),
                "New loading or Mixin failure");
        try (var files = Files.list(instance.resolve("screenshots"))) {
            var frames =
                    files.filter(path -> path.getFileName().toString().endsWith(".png"))
                            .sorted(Comparator.comparing(Path::toString).reversed())
                            .limit(2)
                            .toList();
            require(frames.size() == 2, "Missing observation frames");
            for (int index = 0; index < frames.size(); index++) {
                var image = ImageIO.read(frames.get(index).toFile());
                require(
                        image != null && image.getWidth() == 1920 && image.getHeight() == 1080,
                        "Unreadable or incorrectly sized frame");
                Files.copy(
                        frames.get(index),
                        result.resolve(index == 0 ? "walking.png" : "idle.png"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.copy(
                instance.resolve("logs/latest.log"),
                result.resolve("runtime.log"),
                StandardCopyOption.REPLACE_EXISTING);
        JsonObject checked = new JsonObject();
        checked.addProperty("createdAt", Instant.now().toString());
        checked.addProperty("status", "PASS: bounded startup and animation-state smoke");
        checked.addProperty("userVisualAcceptance", "PENDING");
        checked.add("observation", observation);
        Files.writeString(
                result.resolve("startup-check.json"),
                new GsonBuilder().setPrettyPrinting().create().toJson(checked) + "\n");
        System.out.println(
                "Startup, pose progression, frame readability and state restoration: PASS; full visual acceptance: PENDING");
    }

    static void validate(JsonObject observation) {
        require(observation.get("player").getAsString().equals("RecRivenVI"), "Unexpected player");
        require(
                observation.get("registeredBenders").getAsInt() == 7,
                "Missing Bender registration");
        require(
                observation.get("playerAnimated").getAsBoolean()
                        && observation.get("entityDataAvailable").getAsBoolean(),
                "Player animation data is unavailable");
        require(
                observation.get("framebufferWidth").getAsInt() == 1920
                        && observation.get("framebufferHeight").getAsInt() == 1080,
                "Unexpected framebuffer");
        require(observation.get("masterVolume").getAsDouble() == 0, "Automatic run is not muted");
        require(
                observation.get("movementDistance").getAsDouble() > 0,
                "No normal movement observed");
        for (String flag : new String[] {"noGravity", "noPhysics", "flying"}) {
            require(!observation.get(flag).getAsBoolean(), "Test state remains: " + flag);
        }
        require(observation.get("onGround").getAsBoolean(), "Player is not grounded");
        require(
                observation.get("positionBefore").equals(observation.get("positionAfter")),
                "Test position was not restored");
        JsonArray idle =
                observation.getAsJsonObject("idlePose").getAsJsonArray("leftKneeQuaternion");
        boolean changed = false;
        for (int tick : new int[] {110, 120, 130}) {
            JsonArray walking =
                    observation
                            .getAsJsonObject("walkingPose" + tick)
                            .getAsJsonArray("leftKneeQuaternion");
            require(walking.size() == 4 && idle.size() == 4, "Invalid quaternion length");
            for (int axis = 0; axis < 4; axis++) {
                double value = walking.get(axis).getAsDouble();
                require(
                        Double.isFinite(value) && Double.isFinite(idle.get(axis).getAsDouble()),
                        "Non-finite pose");
                changed |= Math.abs(value - idle.get(axis).getAsDouble()) > 0.00001;
            }
        }
        require(changed, "No joint animation progression observed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
