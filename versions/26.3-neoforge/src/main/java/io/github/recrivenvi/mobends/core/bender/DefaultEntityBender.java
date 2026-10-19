package io.github.recrivenvi.mobends.core.bender;

import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.mutators.IMutatorFactory;
import net.minecraft.world.entity.LivingEntity;

public class DefaultEntityBender<T extends LivingEntity> extends EntityBender<T> {

    private final IEntityDataFactory<T> entityDataFactory;
    private final IMutatorFactory<T> mutatorFactory;

    public DefaultEntityBender(
            String modId,
            String key,
            Class<T> entityClass,
            IEntityDataFactory<T> entityDataFactory,
            IMutatorFactory<T> mutatorFactory,
            MutatedRenderer<T> renderer) {
        super(modId, key, entityClass, renderer);

        this.entityDataFactory = entityDataFactory;
        this.mutatorFactory = mutatorFactory;
    }

    @Override
    public IEntityDataFactory<T> getDataFactory() {
        return entityDataFactory;
    }

    @Override
    public IMutatorFactory<T> getMutatorFactory() {
        return mutatorFactory;
    }
}
