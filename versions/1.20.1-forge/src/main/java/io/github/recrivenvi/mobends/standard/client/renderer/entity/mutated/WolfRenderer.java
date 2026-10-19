// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class WolfRenderer<T extends Wolf> extends MutatedRenderer<T> {
    @Override
    protected float getChildModelScale() {
        // 26.x uses a dedicated baby mesh instead of scaling the adult wolf.
        // The original animation offsets still need the inherited 0.5 child
        // scale, notably the eight-pixel root translation in the sitting pose.
        return 1.0F;
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
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
// ?}
