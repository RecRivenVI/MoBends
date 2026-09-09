//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.ZombieController;
import net.minecraft.world.entity.monster.zombie.Zombie;

public class ZombieData extends ZombieDataBase<Zombie> {

    private final ZombieController controller = new ZombieController();

    public ZombieData(Zombie entity) {
        super(entity);
    }

    @Override
    public ZombieController getController() {
        return this.controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.ZombieController;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieData extends ZombieDataBase<Zombie> {

    private final ZombieController controller = new ZombieController();

    public ZombieData(Zombie entity) {
        super(entity);
    }

    @Override
    public ZombieController getController() {
        return this.controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}*/
//?}
