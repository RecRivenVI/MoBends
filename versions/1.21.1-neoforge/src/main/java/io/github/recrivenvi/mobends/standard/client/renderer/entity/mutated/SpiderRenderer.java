// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import net.minecraft.world.entity.monster.spider.Spider;

public class SpiderRenderer<T extends Spider> extends MutatedRenderer<T> {
    //
    // Override beforeRender to skip the sandwich transform pattern. For spider, we apply transforms
    // directly in SpiderMutator.renderMutated instead, because the sandwich pattern doesn't work
    // correctly with the 1.20.1 PoseStack nesting.
    //
    @Override
    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        // Skip the sandwich transforms - they will be applied in SpiderMutator.renderMutated
        // Only call renderLocalAccessories for any accessories that might need rendering
        this.renderLocalAccessories(entity, data, partialTicks, poseStack);
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer<T extends Spider> extends MutatedRenderer<T> {
    //
    // Override beforeRender to skip the sandwich transform pattern. For spider, we apply transforms
    // directly in SpiderMutator.renderMutated instead, because the sandwich pattern doesn't work
    // correctly with the 1.20.1 PoseStack nesting.
    //
    @Override
    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        // Skip the sandwich transforms - they will be applied in SpiderMutator.renderMutated
        // Only call renderLocalAccessories for any accessories that might need rendering
        this.renderLocalAccessories(entity, data, partialTicks, poseStack);
    }
}
// ?}
