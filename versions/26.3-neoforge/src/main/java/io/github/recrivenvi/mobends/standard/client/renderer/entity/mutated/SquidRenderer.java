package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import net.minecraft.world.entity.animal.squid.Squid;

public class SquidRenderer<T extends Squid> extends MutatedRenderer<T> {
    @Override
    protected float getChildScale() {
        return 1.0F;
    }
}
