package goblinbob.mobends.standard;

import goblinbob.mobends.core.addon.AddonAnimationRegistry;
import goblinbob.mobends.core.addon.IAddon;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.standard.client.model.armor.cache.CacheManager;
import goblinbob.mobends.standard.client.renderer.entity.ArrowTrailManager;
import goblinbob.mobends.standard.client.renderer.entity.mutated.*;
import goblinbob.mobends.standard.data.*;
import goblinbob.mobends.standard.kumo.WolfStateCondition;
import goblinbob.mobends.standard.main.ModConfig;
import goblinbob.mobends.standard.mutators.*;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

public class DefaultAddon implements IAddon {
    @Override
    public void registerContent(AddonAnimationRegistry registry) {
        registry.registerEntity(new PlayerBender());

        registry.registerNewEntity(
                Zombie.class, ZombieData::new, ZombieMutator::new, new ZombieRenderer<>());
        EntityBenderRegistry.instance.excludeInheritedEntityClass(Drowned.class);

        registry.registerNewEntity(
                Skeleton.class, SkeletonData::new, SkeletonMutator::new, new BipedRenderer<>());
        registry.registerNewEntity(
                ZombifiedPiglin.class,
                PigZombieData::new,
                PigZombieMutator::new,
                new ZombieRenderer<>());

        registry.registerNewEntity(
                Spider.class, SpiderData::new, SpiderMutator::new, new SpiderRenderer<>());

        registry.registerNewEntity(
                Squid.class, SquidData::new, SquidMutator::new, new SquidRenderer<>());
        EntityBenderRegistry.instance.excludeInheritedEntityClass(GlowSquid.class);
        EntityBenderRegistry.instance.excludeEntityWhen(
                entity -> entity instanceof Squid squid && squid.isBaby());

        registry.registerNewEntity(
                Wolf.class, WolfData::new, WolfMutator::new, new WolfRenderer<>());
        registry.registerTriggerCondition(
                "wolf_state", WolfStateCondition::new, WolfStateCondition.Template.class);
    }

    @Override
    public String getDisplayName() {
        return "Default";
    }

    @Override
    public void onRenderTick(float partialTicks) {
        if (ModConfig.showArrowTrails) ArrowTrailManager.onRenderTick();
    }

    @Override
    public void onRefresh() {
        CacheManager.getInstance().onResourceReload();
    }
}
