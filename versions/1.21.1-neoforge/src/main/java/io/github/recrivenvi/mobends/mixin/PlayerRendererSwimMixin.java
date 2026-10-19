// ? if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.mixin;

import io.github.recrivenvi.mobends.core.bender.EntityBender;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.standard.animation.SwimmingPosePolicy;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererSwimMixin {
    @Redirect(
            method = "setupRotations",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/player/AbstractClientPlayer;getSwimAmount(F)F"),
            require = 0)
    private float mobends$suppressSwimRotation(AbstractClientPlayer entity, float partialTick) {
        EntityBender bender = EntityBenderRegistry.instance.getForEntity(entity);
        boolean mobendsAnimated = bender != null && bender.isAnimated();
        return SwimmingPosePolicy.vanillaRootAmount(
                entity.getSwimAmount(partialTick), mobendsAnimated);
    }
}
// ?}
