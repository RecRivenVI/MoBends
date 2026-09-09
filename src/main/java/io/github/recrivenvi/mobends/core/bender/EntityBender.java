//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.bender;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import io.github.recrivenvi.mobends.core.mutators.IMutatorFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityBender<T extends LivingEntity> {
    protected final String key;

    private final MutatedRenderer<T> renderer;
    public final Class<T> entityClass;

    private final Map<LivingEntityRenderer<?, ?, ?>, Mutator<LivingEntityData<T>, T, ?>>
            mutatorMap = new HashMap<>();

    private boolean animate;

    public EntityBender(
            String modId, @Nullable String key, Class<T> entityClass, MutatedRenderer<T> renderer) {
        if (renderer == null)
            throw new NullPointerException("The mutated renderer cannot be null.");
        if (entityClass == null) throw new NullPointerException("The entity class cannot be null.");
        if (modId == null) throw new NullPointerException("The Mod ID cannot be null.");

        if (key == null) {
            // Get entity type from registry
            EntityType<?> entityType = getEntityTypeForClass(entityClass);
            if (entityType == null)
                throw new RuntimeException(
                        "Unable to find an EntityType for " + entityClass.getName());

            Identifier resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (resourceLocation == null)
                throw new RuntimeException("Unable to find a key for " + entityClass.getName());

            key = resourceLocation.toString();
        }

        this.key = modId + "-" + key;
        this.entityClass = entityClass;
        this.renderer = renderer;
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> EntityType<T> getEntityTypeForClass(
            Class<T> entityClass) {
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            // We need to check if this entity type creates instances of our class
            // This is a simplified check - in practice, this should work for most entities
            try {
                if (entityClass
                        .getSimpleName()
                        .equalsIgnoreCase(
                                BuiltInRegistries.ENTITY_TYPE
                                        .getKey(entityType)
                                        .getPath()
                                        .replace("_", ""))) {
                    return (EntityType<T>) entityType;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public abstract IEntityDataFactory<T> getDataFactory();

    public abstract IMutatorFactory<T> getMutatorFactory();

    public String getKey() {
        return this.key;
    }

    // Returns true if entities assigned to this EntityBender should be animated.
    public boolean isAnimated() {
        return this.animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        this.renderer.beforeRender(data, entity, partialTicks, poseStack);
    }

    public void afterRender(T entity, float partialTicks, PoseStack poseStack) {
        this.renderer.afterRender(entity, partialTicks, poseStack);
    }

    //
    // Used to apply the effect of the mutation, or just to update the model if it was already
    // mutated. Called from EntityBender.
    //
    @SuppressWarnings("unchecked")
    public <M extends EntityModel<?>> boolean applyMutation(
            LivingEntityRenderer<?, ?, ?> renderer, T entity, float partialTicks) {
        Mutator<LivingEntityData<T>, T, M> mutator =
                (Mutator<LivingEntityData<T>, T, M>) mutatorMap.get(renderer);
        if (mutator == null) {
            mutator =
                    (Mutator<LivingEntityData<T>, T, M>)
                            this.getMutatorFactory().createMutator(this.getDataFactory());
            if (!mutator.mutate(renderer)) {
                return false;
            }

            mutatorMap.put(renderer, (Mutator<LivingEntityData<T>, T, ?>) mutator);
        }

        mutator.updateModel(entity, renderer, partialTicks);
        LivingEntityData<T> data = mutator.getOrMakeData(entity);
        mutator.performAnimations(data, this.key, renderer, partialTicks);
        mutator.syncUpWithData(data);

        return true;
    }

    //
    // Restores an entity's already-computed pose while deferred rendering consumes a model node.
    // This must not advance controllers again: a single entity submit can produce several model
    // nodes (body, armor and attachments) that are consumed later.
    //
    @SuppressWarnings("unchecked")
    public boolean restoreMutation(LivingEntityRenderer<?, ?, ?> renderer, T entity) {
        Mutator<LivingEntityData<T>, T, ?> mutator = mutatorMap.get(renderer);
        if (mutator == null) {
            return false;
        }

        LivingEntityData<T> data = mutator.getData(entity);
        if (data == null) {
            return false;
        }

        mutator.syncUpWithData(data);
        return true;
    }

    // Used to reverse the effect of the mutation. Called from EntityBender.
    @SuppressWarnings("unchecked")
    public <M extends EntityModel<?>> void deapplyMutation(
            LivingEntityRenderer<?, ?, ?> renderer, LivingEntity entity) {
        if (mutatorMap.containsKey(renderer)) {
            Mutator<LivingEntityData<T>, T, M> mutator =
                    (Mutator<LivingEntityData<T>, T, M>) mutatorMap.get(renderer);
            mutator.demutate(renderer);
            mutatorMap.remove(renderer);
        }
    }

    // Used to refresh the mutators in case of real-time changes during development.
    @SuppressWarnings("unchecked")
    public void refreshMutation() {
        for (Entry<LivingEntityRenderer<?, ?, ?>, Mutator<LivingEntityData<T>, T, ?>> entry :
                mutatorMap.entrySet()) {
            LivingEntityRenderer<?, ?, ?> renderer = entry.getKey();
            Mutator<LivingEntityData<T>, T, ?> mutator = entry.getValue();
            mutator.demutate(renderer);
            mutator.mutate(renderer);
            mutator.postRefresh();
        }
    }

    public Mutator<?, ?, ?> getMutator(LivingEntityRenderer<?, ?, ?> renderer) {
        return this.mutatorMap.get(renderer);
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.core.bender;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.recrivenvi.mobends.core.client.MutatedRenderer;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.data.IEntityDataFactory;
import io.github.recrivenvi.mobends.core.data.LivingEntityData;
import io.github.recrivenvi.mobends.core.mutators.IMutatorFactory;
import io.github.recrivenvi.mobends.core.mutators.Mutator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityBender<T extends LivingEntity> {
    protected final String key;

    private final MutatedRenderer<T> renderer;
    public final Class<T> entityClass;

    private final Map<LivingEntityRenderer<? extends T, ?>, Mutator<LivingEntityData<T>, T, ?>>
            mutatorMap = new HashMap<>();

    private boolean animate;

    public EntityBender(
            String modId, @Nullable String key, Class<T> entityClass, MutatedRenderer<T> renderer) {
        if (renderer == null)
            throw new NullPointerException("The mutated renderer cannot be null.");
        if (entityClass == null) throw new NullPointerException("The entity class cannot be null.");
        if (modId == null) throw new NullPointerException("The Mod ID cannot be null.");

        if (key == null) {
            // Get entity type from registry
            EntityType<?> entityType = getEntityTypeForClass(entityClass);
            if (entityType == null)
                throw new RuntimeException(
                        "Unable to find an EntityType for " + entityClass.getName());

            ResourceLocation resourceLocation = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (resourceLocation == null)
                throw new RuntimeException("Unable to find a key for " + entityClass.getName());

            key = resourceLocation.toString();
        }

        this.key = modId + "-" + key;
        this.entityClass = entityClass;
        this.renderer = renderer;
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> EntityType<T> getEntityTypeForClass(
            Class<T> entityClass) {
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            // We need to check if this entity type creates instances of our class
            // This is a simplified check - in practice, this should work for most entities
            try {
                if (entityClass
                        .getSimpleName()
                        .equalsIgnoreCase(
                                BuiltInRegistries.ENTITY_TYPE
                                        .getKey(entityType)
                                        .getPath()
                                        .replace("_", ""))) {
                    return (EntityType<T>) entityType;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public abstract IEntityDataFactory<T> getDataFactory();

    public abstract IMutatorFactory<T> getMutatorFactory();

    public String getKey() {
        return this.key;
    }

    // Returns true if entities assigned to this EntityBender should be animated.
    public boolean isAnimated() {
        return this.animate;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void beforeRender(
            EntityData<T> data, T entity, float partialTicks, PoseStack poseStack) {
        this.renderer.beforeRender(data, entity, partialTicks, poseStack);
    }

    public void afterRender(T entity, float partialTicks, PoseStack poseStack) {
        this.renderer.afterRender(entity, partialTicks, poseStack);
    }

    //
    // Used to apply the effect of the mutation, or just to update the model if it was already
    // mutated. Called from EntityBender.
    //
    @SuppressWarnings("unchecked")
    public <M extends EntityModel<T>> boolean applyMutation(
            LivingEntityRenderer<T, M> renderer, T entity, float partialTicks) {
        Mutator<LivingEntityData<T>, T, M> mutator =
                (Mutator<LivingEntityData<T>, T, M>) mutatorMap.get(renderer);
        if (mutator == null) {
            mutator =
                    (Mutator<LivingEntityData<T>, T, M>)
                            this.getMutatorFactory().createMutator(this.getDataFactory());
            if (!mutator.mutate(renderer)) {
                return false;
            }

            mutatorMap.put(renderer, (Mutator<LivingEntityData<T>, T, ?>) mutator);
        }

        mutator.updateModel(entity, renderer, partialTicks);
        LivingEntityData<T> data = mutator.getOrMakeData(entity);
        mutator.performAnimations(data, this.key, renderer, partialTicks);
        mutator.syncUpWithData(data);

        return true;
    }

    //
    // Restores an entity's already-computed pose while deferred rendering consumes a model node.
    // This must not advance controllers again: a single entity submit can produce several model
    // nodes (body, armor and attachments) that are consumed later.
    //
    @SuppressWarnings("unchecked")
    public boolean restoreMutation(LivingEntityRenderer<? extends T, ?> renderer, T entity) {
        Mutator<LivingEntityData<T>, T, ?> mutator = mutatorMap.get(renderer);
        if (mutator == null) {
            return false;
        }

        LivingEntityData<T> data = mutator.getData(entity);
        if (data == null) {
            return false;
        }

        mutator.syncUpWithData(data);
        return true;
    }

    // Used to reverse the effect of the mutation. Called from EntityBender.
    @SuppressWarnings("unchecked")
    public <M extends EntityModel<T>> void deapplyMutation(
            LivingEntityRenderer<T, M> renderer, LivingEntity entity) {
        if (mutatorMap.containsKey(renderer)) {
            Mutator<LivingEntityData<T>, T, M> mutator =
                    (Mutator<LivingEntityData<T>, T, M>) mutatorMap.get(renderer);
            mutator.demutate(renderer);
            mutatorMap.remove(renderer);
        }
    }

    // Used to refresh the mutators in case of real-time changes during development.
    @SuppressWarnings("unchecked")
    public void refreshMutation() {
        for (Entry<LivingEntityRenderer<? extends T, ?>, Mutator<LivingEntityData<T>, T, ?>> entry :
                mutatorMap.entrySet()) {
            LivingEntityRenderer<T, EntityModel<T>> renderer =
                    (LivingEntityRenderer<T, EntityModel<T>>) entry.getKey();
            Mutator<LivingEntityData<T>, T, EntityModel<T>> mutator =
                    (Mutator<LivingEntityData<T>, T, EntityModel<T>>) entry.getValue();
            mutator.demutate(renderer);
            mutator.mutate(renderer);
            mutator.postRefresh();
        }
    }

    public Mutator<?, ?, ?> getMutator(LivingEntityRenderer<? extends LivingEntity, ?> renderer) {
        return this.mutatorMap.get(renderer);
    }
}*/
//?}
