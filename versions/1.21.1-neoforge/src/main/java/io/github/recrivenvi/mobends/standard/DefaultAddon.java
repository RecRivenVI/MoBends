// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard;

import io.github.recrivenvi.mobends.core.addon.AddonAnimationRegistry;
import io.github.recrivenvi.mobends.core.addon.IAddon;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.standard.client.model.armor.cache.CacheManager;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.ArrowTrailManager;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated.*;
import io.github.recrivenvi.mobends.standard.data.*;
import io.github.recrivenvi.mobends.standard.kumo.WolfStateCondition;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import io.github.recrivenvi.mobends.standard.mutators.*;
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
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard;

import io.github.recrivenvi.mobends.core.addon.AddonAnimationRegistry;
import io.github.recrivenvi.mobends.core.addon.IAddon;
import io.github.recrivenvi.mobends.core.bender.EntityBenderRegistry;
import io.github.recrivenvi.mobends.standard.client.model.armor.cache.CacheManager;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.ArrowTrailManager;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated.*;
import io.github.recrivenvi.mobends.standard.data.*;
import io.github.recrivenvi.mobends.standard.kumo.WolfStateCondition;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import io.github.recrivenvi.mobends.standard.mutators.*;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;

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
// ?}
