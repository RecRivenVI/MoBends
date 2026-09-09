//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.client;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

// Loader-neutral association between Mojang's reusable render states and entities.
public final class RenderStateEntityContext {
    private static final Map<LivingEntityRenderState, LivingEntity> ENTITIES =
            Collections.synchronizedMap(new WeakHashMap<>());

    private RenderStateEntityContext() {}

    public static void put(LivingEntityRenderState state, LivingEntity entity) {
        ENTITIES.put(state, entity);
    }

    public static LivingEntity get(LivingEntityRenderState state) {
        return ENTITIES.get(state);
    }
}
//?}
