package io.github.recrivenvi.mobends.core.data;

import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface IEntityDataFactory<E extends Entity> {
    EntityData<E> createEntityData(E entity);
}
