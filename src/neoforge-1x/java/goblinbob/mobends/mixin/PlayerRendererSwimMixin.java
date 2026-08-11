package goblinbob.mobends.mixin;

import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.standard.animation.SwimmingPosePolicy;
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
