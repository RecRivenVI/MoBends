package io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated;

import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieRenderer<T extends Zombie> extends BipedRenderer<T> {
    @Override
    protected float getChildScale() {
        // 26.x zombies and zombified piglins have dedicated baby meshes.
        return 1.0F;
    }
}
