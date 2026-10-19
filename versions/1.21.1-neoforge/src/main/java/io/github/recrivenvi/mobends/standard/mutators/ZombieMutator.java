package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.ZombieData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieMutator extends ZombieMutatorBase<ZombieData, Zombie, ZombieModel<Zombie>> {

    public ZombieMutator(IEntityDataFactory<Zombie> dataFactory) {
        super(dataFactory);
    }

    @Override
    public void storeVanillaModel(ZombieModel<Zombie> model) {
        super.storeVanillaModel(model);
    }

    @Override
    public boolean shouldModelBeSkipped(EntityModel<?> model) {
        return !(model instanceof ZombieModel);
    }
}
