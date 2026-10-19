// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieRenderer<T extends Zombie> extends BipedRenderer<T> {
    @Override
    protected float getChildScale() {
        // 26.x zombies and zombified piglins have dedicated baby meshes.
        return 1.0F;
    }
}
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer<T extends Zombie> extends BipedRenderer<T> {}*/
// ?}
