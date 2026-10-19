package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer<T extends Wolf> extends MutatedRenderer<T> {
    @Override
    protected float getChildModelScale() {
        // WolfMutator reproduces vanilla's full-size head and half-size body passes.
        // Applying the common child model scale here would halve both a second time.
        return 1.0F;
    }
}
