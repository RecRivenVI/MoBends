// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.mixin;

import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Accessor mixin for LivingEntityRenderer to expose internal fields.
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor<
        T extends LivingEntity,
        S extends LivingEntityRenderState,
        M extends EntityModel<? super S>> {

    //
    // Gets the list of render layers.
    //
    // @return The render layers list
    //
    @Accessor("layers")
    List<RenderLayer<S, M>> mobends$getLayers();
}
*/// ?} else if target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.mixin;

import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Accessor mixin for LivingEntityRenderer to expose internal fields.
@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor<T extends LivingEntity, M extends EntityModel<T>> {

    //
    // Gets the list of render layers.
    //
    // @return The render layers list
    //
    @Accessor("layers")
    List<RenderLayer<T, M>> mobends$getLayers();
}*/
// ?}
