// ? if target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.core.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.junit.jupiter.api.Test;

class TrailSubmitterTest {
    @Test
    void submitsTrailsAfterTranslucentTerrain() {
        SubmitNodeStorage storage = new SubmitNodeStorage();
        PoseStack poseStack = new PoseStack();

        new TrailPhaseBridge(RenderTypes.debugQuads())
                .submit(poseStack, storage, RenderTypes.debugQuads(), (pose, consumer) -> {});

        SubmitNodeCollection collection = storage.order(0);
        assertTrue(collection.translucentCustomGeometry.isEmpty());
        assertFalse(collection.afterTerrain.isEmpty());
    }
}
*/// ?}
