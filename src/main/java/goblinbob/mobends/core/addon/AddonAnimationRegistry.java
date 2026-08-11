package goblinbob.mobends.core.addon;

import goblinbob.mobends.core.bender.DefaultEntityBender;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.MutatedRenderer;
import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.core.kumo.state.condition.ITriggerCondition;
import goblinbob.mobends.core.kumo.state.condition.ITriggerConditionFactory;
import goblinbob.mobends.core.kumo.state.condition.TriggerConditionRegistry;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import goblinbob.mobends.core.mutators.IMutatorFactory;
import net.minecraft.world.entity.LivingEntity;

public class AddonAnimationRegistry {

    private final String modId;

    public AddonAnimationRegistry(String modId) {
        this.modId = modId;
    }

    public <T extends LivingEntity> String registerNewEntity(
            Class<T> entityClass,
            IEntityDataFactory<T> entityDataFactory,
            IMutatorFactory<T> mutatorFactory,
            MutatedRenderer<T> renderer) {
        return registerNewEntity(null, entityClass, entityDataFactory, mutatorFactory, renderer);
    }

    /**
     * Registers the entity as an animated one.
     *
     * @return The entity's identifier key.
     */
    public <T extends LivingEntity> String registerNewEntity(
            String key,
            Class<T> entityClass,
            IEntityDataFactory<T> entityDataFactory,
            IMutatorFactory<T> mutatorFactory,
            MutatedRenderer<T> renderer) {
        EntityBender<T> entityBender =
                new DefaultEntityBender<>(
                        modId, key, entityClass, entityDataFactory, mutatorFactory, renderer);
        return registerEntity(entityBender);
    }

    /**
     * Use this in case you want to use a custom sub-type of EntityBender for extended
     * functionality.
     *
     * @param entityBender An instance of the EntityBender to put into the system.
     * @return The entity's identifier key.
     */
    public <T extends LivingEntity> String registerEntity(EntityBender<T> entityBender) {
        String key = entityBender.getKey();
        if (!key.startsWith(this.modId)) {
            throw new IllegalArgumentException(
                    "The EntityBender's ModID does not match that of the AddonAnimationRegistry.");
        }
        EntityBenderRegistry.instance.registerBender(entityBender);
        return entityBender.getKey();
    }

    /**
     * Registers a trigger condition that can be used in KUMO internal animators as well as
     * BendsPacks.
     *
     * @param key The internal name of the trigger condition. (snake_case preferable) This is going
     *     to be automatically prefixed with the modid like so "modid:key"
     * @param factory The constructor of the trigger condition instance.
     * @param templateType The type of the template that this condition is going to be serialized
     *     into.
     */
    public <T extends TriggerConditionTemplate> void registerTriggerCondition(
            String key, ITriggerConditionFactory<?, T> factory, Class<T> templateType) {
        TriggerConditionRegistry.instance.register(
                String.format("%s:%s", modId, key), factory, templateType);
    }

    /**
     * Registers a trigger condition that can be used in KUMO internal animators as well as
     * BendsPacks.
     *
     * @param key The internal name of the trigger condition. (snake_case preferable) This is going
     *     to be automatically prefixed with the modid like so "modid:key"
     * @param condition The trigger condition instance.
     */
    public void registerTriggerCondition(String key, ITriggerCondition condition) {
        TriggerConditionRegistry.instance.register(String.format("%s:%s", modId, key), condition);
    }
}
