// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.core.bender;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated.SquidRenderer;
import io.github.recrivenvi.mobends.standard.client.renderer.entity.mutated.ZombieRenderer;
import io.github.recrivenvi.mobends.standard.data.SquidData;
import io.github.recrivenvi.mobends.standard.data.ZombieData;
import io.github.recrivenvi.mobends.standard.mutators.SquidMutator;
import io.github.recrivenvi.mobends.standard.mutators.ZombieMutator;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.junit.jupiter.api.Test;

public class EntityBenderRegistryTest {
    @Test
    public void preservesOriginalZombieInheritanceWithoutAddingDrownedSupport() {
        EntityBenderRegistry registry = new EntityBenderRegistry();
        EntityBender<Zombie> zombieBender =
                new DefaultEntityBender<>(
                        "mobends",
                        "minecraft:zombie",
                        Zombie.class,
                        ZombieData::new,
                        ZombieMutator::new,
                        new ZombieRenderer<>());
        registry.registerBender(zombieBender);
        registry.excludeInheritedEntityClass(Drowned.class);

        assertSame(zombieBender, registry.findRegisteredBenderForEntityClass(Zombie.class));
        assertSame(zombieBender, registry.findRegisteredBenderForEntityClass(Husk.class));
        assertNull(registry.findRegisteredBenderForEntityClass(Drowned.class));
    }

    @Test
    public void doesNotExtendSquidAnimationsToGlowSquids() {
        EntityBenderRegistry registry = new EntityBenderRegistry();
        EntityBender<Squid> squidBender =
                new DefaultEntityBender<>(
                        "mobends",
                        "minecraft:squid",
                        Squid.class,
                        SquidData::new,
                        SquidMutator::new,
                        new SquidRenderer<>());
        registry.registerBender(squidBender);
        registry.excludeInheritedEntityClass(GlowSquid.class);

        assertSame(squidBender, registry.findRegisteredBenderForEntityClass(Squid.class));
        assertNull(registry.findRegisteredBenderForEntityClass(GlowSquid.class));
    }
}
// ?}
