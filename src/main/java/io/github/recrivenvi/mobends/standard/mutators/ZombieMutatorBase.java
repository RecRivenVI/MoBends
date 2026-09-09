//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.ZombieDataBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.monster.zombie.Zombie;

//
// This base is used both by ZombieMutator and ZombieVillagerMutator, but since the
// ZombieVillagerModel doesn't extend ZombieModel, a separate base class had to be made.
//
// @param <D>
// @param <E>
// @param <M>
//
public abstract class ZombieMutatorBase<
                D extends ZombieDataBase<E>, E extends Zombie, M extends HumanoidModel<?>>
        extends BipedMutator<D, E, M> {

    // Should the height of the texture be 64 or 32 (half)?
    protected boolean halfTexture = false;

    public ZombieMutatorBase(IEntityDataFactory<E> dataCreationFunction) {
        super(dataCreationFunction);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<?, ?, ?> renderer) {
        super.fetchFields(renderer);

        // In 1.20.1, we can check the texture height from the model if needed
        // For now, default to false (64-pixel height)
        this.halfTexture = false;
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.mutators;

import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.standard.data.ZombieDataBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.monster.Zombie;

//
// This base is used both by ZombieMutator and ZombieVillagerMutator, but since the
// ZombieVillagerModel doesn't extend ZombieModel, a separate base class had to be made.
//
// @param <D>
// @param <E>
// @param <M>
//
public abstract class ZombieMutatorBase<
                D extends ZombieDataBase<E>, E extends Zombie, M extends HumanoidModel<E>>
        extends BipedMutator<D, E, M> {

    // Should the height of the texture be 64 or 32 (half)?
    protected boolean halfTexture = false;

    public ZombieMutatorBase(IEntityDataFactory<E> dataCreationFunction) {
        super(dataCreationFunction);
    }

    @Override
    public void fetchFields(LivingEntityRenderer<E, M> renderer) {
        super.fetchFields(renderer);

        // In 1.20.1, we can check the texture height from the model if needed
        // For now, default to false (64-pixel height)
        this.halfTexture = false;
    }
}*/
//?}
