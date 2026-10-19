package io.github.recrivenvi.mobends.core.bender;

import com.mojang.logging.LogUtils;
import io.github.recrivenvi.mobends.core.configuration.CoreClientConfig;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import java.util.*;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/** This class is responsible for keeping track of all entity benders. */
public class EntityBenderRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final EntityBenderRegistry instance = new EntityBenderRegistry();

    private final Map<Class<? extends LivingEntity>, EntityBender<?>> entityClassToBenderMap =
            new HashMap<>();

    /**
     * Modern entities that inherit from an originally supported class, but did not exist in 1.12.2.
     * They must stay vanilla unless Mo' Bends 1.2.2 explicitly supported an equivalent entity.
     */
    private final Set<Class<? extends LivingEntity>> excludedInheritedEntityClasses =
            new HashSet<>();

    private final List<Predicate<LivingEntity>> excludedEntityPredicates = new ArrayList<>();

    /** Used to cache entity-to-bender relationships, so they won't be calculated every time. */
    private final Map<LivingEntity, EntityBender<?>> entityToBenderMap = new HashMap<>();

    public void registerBender(EntityBender<?> entityBender) {
        LOGGER.info(String.format("Registering %s", entityBender.getKey()));
        entityClassToBenderMap.put(entityBender.entityClass, entityBender);
    }

    public List<EntityBender<?>> getRegistered() {
        return entityClassToBenderMap.values().stream()
                .sorted(Comparator.comparing(EntityBender::getKey))
                .toList();
    }

    public void excludeInheritedEntityClass(Class<? extends LivingEntity> entityClass) {
        excludedInheritedEntityClasses.add(entityClass);
    }

    public void excludeEntityWhen(Predicate<LivingEntity> predicate) {
        excludedEntityPredicates.add(predicate);
    }

    public void applyConfiguration(CoreClientConfig config) {
        config.ensureEntityDefaults(
                entityClassToBenderMap.values().stream().map(EntityBender::getKey).toList());
        for (EntityBender<?> entityBender : entityClassToBenderMap.values()) {
            entityBender.setAnimate(config.isEntityAnimated(entityBender.getKey()));
        }
    }

    public <E extends LivingEntity> EntityBender<E> getForEntityClass(Class<E> c) {
        // noinspection unchecked
        return (EntityBender<E>) entityClassToBenderMap.get(c);
    }

    public <E extends LivingEntity> EntityBender<E> getForEntity(E entity) {
        for (Predicate<LivingEntity> predicate : excludedEntityPredicates) {
            if (predicate.test(entity)) {
                return null;
            }
        }

        // noinspection unchecked
        return (EntityBender<E>)
                entityToBenderMap.computeIfAbsent(
                        entity,
                        key -> {
                            // Checking the config blacklist
                            if (ModConfig.shouldKeepEntityAsVanilla(entity)) return null;

                            return findRegisteredBenderForEntityClass(entity.getClass());
                        });
    }

    EntityBender<?> findRegisteredBenderForEntityClass(Class<? extends LivingEntity> entityClass) {
        // Checking direct registration
        EntityBender<?> direct = entityClassToBenderMap.get(entityClass);
        if (direct != null) {
            return direct;
        }

        // Do not accidentally extend the original support matrix merely because a modern
        // entity was added as a subclass of an old Minecraft entity.
        for (Class<? extends LivingEntity> excludedClass : excludedInheritedEntityClasses) {
            if (excludedClass.isAssignableFrom(entityClass)) {
                return null;
            }
        }

        // Checking indirect inheritance
        for (EntityBender<?> entityBender : entityClassToBenderMap.values()) {
            if (entityBender.entityClass.isAssignableFrom(entityClass)) {
                return entityBender;
            }
        }

        return null;
    }

    public <E extends LivingEntity> void clearCache(E entity) {
        entityToBenderMap.remove(entity);
    }

    /**
     * Will clear any associations between entities and EntityBenders. This is usually called
     * whenever the player joins a new world.
     */
    public void clearCache() {
        entityToBenderMap.clear();
    }

    public void refreshMutators() {
        clearCache();

        for (EntityBender<?> entityBender : entityClassToBenderMap.values())
            entityBender.refreshMutation();
    }
}
