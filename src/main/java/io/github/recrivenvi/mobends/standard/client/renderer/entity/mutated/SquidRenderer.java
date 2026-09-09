//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import net.minecraft.world.entity.animal.squid.Squid;

public class SquidRenderer<T extends Squid> extends MutatedRenderer<T> {
    @Override
    protected float getChildScale() {
        return 1.0F;
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import net.minecraft.world.entity.animal.Squid;

public class SquidRenderer<T extends Squid> extends MutatedRenderer<T> {}*/
//?}
