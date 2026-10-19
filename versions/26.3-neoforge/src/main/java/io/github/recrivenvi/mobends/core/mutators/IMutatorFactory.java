package io.github.recrivenvi.mobends.core.mutators;

import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface IMutatorFactory<E extends LivingEntity> {

    Mutator<? extends LivingEntityData<E>, ? extends E, ?> createMutator(
            IEntityDataFactory<E> dataFactory);
}
