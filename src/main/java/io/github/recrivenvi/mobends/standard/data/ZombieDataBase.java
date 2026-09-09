//? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import java.util.Random;
import net.minecraft.world.entity.monster.zombie.Zombie;

public abstract class ZombieDataBase<E extends Zombie> extends BipedEntityData<E> {
    public static final int ANIMATION_SETS_AMOUNT = 2;

    //
    // This determines what set of animations this zombie should get.
    //
    protected int animationSet = 0;
    protected int currentWalkingState = 0;
    protected float ticksBeforeStateChange = 0;
    private final Random random = new Random();

    public ZombieDataBase(E entity) {
        super(entity);
        // Getting a pseudo-random animationType based on something
        // that is shared across clients, so that every player
        // sees the same variation
        this.animationSet = ((int) (entity.getId() * 3.61352F)) % ANIMATION_SETS_AMOUNT;
    }

    public int getAnimationSet() {
        return this.animationSet;
    }

    public int getCurrentWalkingState() {
        return this.currentWalkingState;
    }

    @Override
    public void update(float partialTicks) {
        super.update(partialTicks);

        this.ticksBeforeStateChange -= DataUpdateHandler.ticksPerFrame;

        if (this.ticksBeforeStateChange <= 0) {
            this.currentWalkingState = random.nextInt(2);
            this.ticksBeforeStateChange = 80 + random.nextInt(20);
        }
    }
}
//?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.standard.data;

import io.github.recrivenvi.mobends.core.client.event.DataUpdateHandler;
import java.util.Random;
import net.minecraft.world.entity.monster.Zombie;

public abstract class ZombieDataBase<E extends Zombie> extends BipedEntityData<E> {
    public static final int ANIMATION_SETS_AMOUNT = 2;

    //
    // This determines what set of animations this zombie should get.
    //
    protected int animationSet = 0;
    protected int currentWalkingState = 0;
    protected float ticksBeforeStateChange = 0;
    private final Random random = new Random();

    public ZombieDataBase(E entity) {
        super(entity);
        // Getting a pseudo-random animationType based on something
        // that is shared across clients, so that every player
        // sees the same variation
        this.animationSet = ((int) (entity.getId() * 3.61352F)) % ANIMATION_SETS_AMOUNT;
    }

    public int getAnimationSet() {
        return this.animationSet;
    }

    public int getCurrentWalkingState() {
        return this.currentWalkingState;
    }

    @Override
    public void update(float partialTicks) {
        super.update(partialTicks);

        this.ticksBeforeStateChange -= DataUpdateHandler.ticksPerFrame;

        if (this.ticksBeforeStateChange <= 0) {
            this.currentWalkingState = random.nextInt(2);
            this.ticksBeforeStateChange = 80 + random.nextInt(20);
        }
    }
}*/
//?}
