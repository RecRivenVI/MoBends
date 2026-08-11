package goblinbob.mobends.standard.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;

public class ArrowTrailManager {
    private static HashMap<AbstractArrow, ArrowTrail> trailMap = new HashMap<>();
    public static long time, lastTime;

    static {
        time = System.nanoTime() / 1000;
        lastTime = System.nanoTime() / 1000;
    }

    public static ArrowTrail getOrMake(AbstractArrow arrow) {
        ArrowTrail trail;
        if (!trailMap.containsKey(arrow)) {
            trail = new ArrowTrail(arrow);
            trailMap.put(arrow, trail);
        } else {
            trail = trailMap.get(arrow);
        }

        return trail;
    }

    public static void submitTrail(
            AbstractArrow entity,
            float partialTicks,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector) {
        getOrMake(entity).submit(partialTicks, poseStack, submitNodeCollector);
    }

    public static void cleanup() {
        trailMap.entrySet().removeIf(e -> e.getValue().shouldBeRemoved());
    }

    public static void onRenderTick() {
        for (final ArrowTrail trail : trailMap.values()) {
            trail.onRenderTick();
        }

        cleanup();
    }
}
