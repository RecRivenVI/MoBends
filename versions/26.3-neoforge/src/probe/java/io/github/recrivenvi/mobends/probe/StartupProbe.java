package io.github.recrivenvi.mobends.probe;

import com.google.gson.GsonBuilder;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.core.data.EntityDatabase;
import io.github.recrivenvi.mobends.standard.data.PlayerData;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/** A bounded smoke observation, loaded only by the isolated validation task. */
@Mod(value = "mobends_probe", dist = Dist.CLIENT)
public final class StartupProbe {
    private final long deadline = System.nanoTime() + 180_000_000_000L;
    private final Map<String, Object> evidence = new LinkedHashMap<>();
    private int ticks;
    private Vec3 position;
    private Vec3 velocity;
    private CameraType camera;
    private boolean forward;
    private boolean finished;

    public StartupProbe() {
        NeoForge.EVENT_BUS.addListener(this::tick);
    }

    private void tick(ClientTickEvent.Post event) {
        if (finished) return;
        Minecraft client = Minecraft.getInstance();
        client.options.pauseOnLostFocus = false;
        client.options.getSoundSourceOptionInstance(SoundSource.MASTER).set(0.0);
        if (Boolean.getBoolean("mobends.probe.observeOnly")) {
            if (client.player != null && client.level != null && ++ticks % 10 == 0) {
                var player = client.player;
                evidence.put("pid", ProcessHandle.current().pid());
                evidence.put("player", player.getName().getString());
                evidence.put(
                        "registeredBenders", EntityBenderRegistry.instance.getRegistered().size());
                var bender = EntityBenderRegistry.instance.getForEntity(player);
                evidence.put("playerAnimated", bender != null && bender.isAnimated());
                evidence.put("position", player.position().toString());
                evidence.put("noGravity", player.isNoGravity());
                evidence.put("noPhysics", player.noPhysics);
                evidence.put("flying", player.getAbilities().flying);
                evidence.put("onGround", player.onGround());
                evidence.put(
                        "masterVolume",
                        client.options.getSoundSourceOptionInstance(SoundSource.MASTER).get());
                recordPose(client, "pose");
                write("mcp-state.json");
            }
            return;
        }
        if (System.nanoTime() > deadline) {
            evidence.put("status", "FAIL: world readiness timeout");
            finish(client);
            return;
        }
        if (client.player == null || client.level == null || client.gui.screen() != null) return;
        ticks++;
        var player = client.player;
        if (ticks == 60) {
            position = player.position();
            velocity = player.getDeltaMovement();
            camera = client.options.getCameraType();
            forward = client.options.keyUp.isDown();
            client.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            evidence.put("pid", ProcessHandle.current().pid());
            evidence.put("player", player.getName().getString());
            evidence.put("gameDirectory", Path.of("").toAbsolutePath().normalize().toString());
            evidence.put(
                    "masterVolume",
                    client.options.getSoundSourceOptionInstance(SoundSource.MASTER).get());
            evidence.put("dimension", client.level.dimension().toString());
            evidence.put("positionBefore", position.toString());
            evidence.put("framebufferWidth", client.getWindow().getWidth());
            evidence.put("framebufferHeight", client.getWindow().getHeight());
            evidence.put("registeredBenders", EntityBenderRegistry.instance.getRegistered().size());
            var bender = EntityBenderRegistry.instance.getForEntity(player);
            evidence.put("playerAnimated", bender != null && bender.isAnimated());
            evidence.put("entityDataAvailable", EntityDatabase.instance.get(player) != null);
        }
        if (ticks == 100) {
            recordPose(client, "idlePose");
            Screenshot.grab(client, false);
            client.options.keyUp.setDown(true);
        }
        if (ticks == 110 || ticks == 120 || ticks == 130) {
            recordPose(client, "walkingPose" + ticks);
        }
        if (ticks == 120) Screenshot.grab(client, false);
        if (ticks == 140) {
            client.options.keyUp.setDown(forward);
            evidence.put("movementDistance", player.position().distanceTo(position));
            player.setPos(position);
            player.setDeltaMovement(velocity);
            client.options.setCameraType(camera);
        }
        if (ticks == 180) {
            evidence.put("noGravity", player.isNoGravity());
            evidence.put("noPhysics", player.noPhysics);
            evidence.put("flying", player.getAbilities().flying);
            evidence.put("onGround", player.onGround());
            evidence.put("positionAfter", player.position().toString());
            evidence.put("velocityAfter", player.getDeltaMovement().toString());
            evidence.put("status", "OBSERVED: startup; visual acceptance pending");
            finish(client);
        }
    }

    private void recordPose(Minecraft client, String name) {
        Object observed = EntityDatabase.instance.get(client.player);
        if (observed instanceof PlayerData data) {
            var knee = data.leftForeLeg.rotation.getSmooth();
            var arm = data.rightArm.rotation.getSmooth();
            evidence.put(
                    name,
                    Map.of(
                            "dataType", data.getClass().getName(),
                            "leftKneeQuaternion", new float[] {knee.x, knee.y, knee.z, knee.w},
                            "rightArmQuaternion", new float[] {arm.x, arm.y, arm.z, arm.w}));
        }
    }

    private void finish(Minecraft client) {
        finished = true;
        client.options.keyUp.setDown(forward);
        write("startup.json");
        client.stop();
    }

    private void write(String name) {
        try {
            Path destination = Path.of(System.getProperty("validation.result"), name);
            Files.createDirectories(destination.getParent());
            Files.writeString(
                    destination,
                    new GsonBuilder().setPrettyPrinting().create().toJson(evidence) + "\n");
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot write startup evidence", exception);
        }
    }
}
