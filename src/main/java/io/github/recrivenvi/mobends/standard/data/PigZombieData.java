//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.PigZombieController;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class PigZombieData extends BipedEntityData<ZombifiedPiglin> {

    private final PigZombieController controller = new PigZombieController();

    public PigZombieData(ZombifiedPiglin entity) {
        super(entity);
    }

    @Override
    public PigZombieController getController() {
        return controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.PigZombieController;
import net.minecraft.world.entity.monster.ZombifiedPiglin;

public class PigZombieData extends BipedEntityData<ZombifiedPiglin> {

    private final PigZombieController controller = new PigZombieController();

    public PigZombieData(ZombifiedPiglin entity) {
        super(entity);
    }

    @Override
    public PigZombieController getController() {
        return controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}*/
//?}
