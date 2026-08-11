package goblinbob.mobends.standard.client.renderer.entity.mutated;

import goblinbob.mobends.core.client.MutatedRenderer;
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
