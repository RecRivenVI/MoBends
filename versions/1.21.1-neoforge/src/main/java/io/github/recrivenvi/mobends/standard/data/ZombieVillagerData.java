// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.ZombieVillagerController;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;

public class ZombieVillagerData extends ZombieDataBase<ZombieVillager> {

    private final ZombieVillagerController controller = new ZombieVillagerController();

    public ZombieVillagerData(ZombieVillager entity) {
        super(entity);
    }

    @Override
    public ZombieVillagerController getController() {
        return controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.standard.animation.controller.ZombieVillagerController;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerData extends ZombieDataBase<ZombieVillager> {

    private final ZombieVillagerController controller = new ZombieVillagerController();

    public ZombieVillagerData(ZombieVillager entity) {
        super(entity);
    }

    @Override
    public ZombieVillagerController getController() {
        return controller;
    }

    @Override
    public void onTicksRestart() {
        // No behaviour
    }
}
// ?}
