package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.animation.layer.HardAnimationLayer;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.*;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.AttackStanceAnimationBit;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class SwordAction extends AnimationBit<BipedEntityData<?>> {
    protected final HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<>();
    protected final AttackStanceAnimationBit bitAttackStance = new AttackStanceAnimationBit();
    protected final AttackStanceSprintAnimationBit bitAttackStanceSprint =
            new AttackStanceSprintAnimationBit();

    protected float lastTicksAfterAttack = 0.0F;
    protected int moveId = 0;
    protected int activeMoveId = 0;

    private static final List<AnimationBit<BipedEntityData<?>>> bits =
            Arrays.asList(
                    new AttackSlashUpAnimationBit(),
                    new AttackSlashDownAnimationBit(),
                    new AttackSlashInwardAnimationBit(),
                    new AttackSlashOutwardAnimationBit(),
                    new AttackWhirlSlashAnimationBit());

    public SwordAction(HumanoidArm ignoredHandSide) {}

    private void nextMove(BipedEntityData<?> entityData) {
        LivingEntity entity = entityData.getEntity();
        boolean isLocal = entity == Minecraft.getInstance().player;

        int moveCount = ModConfig.performSpinAttack ? bits.size() : bits.size() - 1;

        if (!isLocal) {
            // For remote players, derive moveId from tick count for deterministic multiplayer sync
            moveId = (entity.tickCount / 6) % moveCount;
        } else if (moveId >= moveCount) {
            moveId = 0;
        }

        activeMoveId = moveId;
        AnimationBit<BipedEntityData<?>> bit = bits.get(moveId);

        if (bit != null) {
            this.layerBase.playBit(bit, entityData);
        } else {
            this.layerBase.clearAnimation();
        }

        if (isLocal) {
            moveId = (moveId + 1) % moveCount;
        }
    }

    @Override
    public String[] getActions(BipedEntityData<?> entityData) {
        if (entityData.getTicksAfterAttack() < 10.0F && layerBase.isPlaying()) {
            return new String[] {"attack", "attack_" + activeMoveId};
        }
        if (layerBase.isPlaying(bitAttackStance) || layerBase.isPlaying(bitAttackStanceSprint)) {
            return new String[] {"attack_stance"};
        }
        return new String[] {};
    }

    @Override
    public void perform(BipedEntityData<?> entityData) {
        float ticksAfterAttack = entityData.getTicksAfterAttack();
        if (ticksAfterAttack < lastTicksAfterAttack) {
            nextMove(entityData);
        }
        lastTicksAfterAttack = ticksAfterAttack;

        LivingEntity entity = entityData.getEntity();

        int comboClearTime = 20;

        // Creating the combo.
        if (ticksAfterAttack > comboClearTime) {
            moveId = 0;
        }

        if (ticksAfterAttack < 10) {
        } else if (ticksAfterAttack < 60 && entityData.isOnGround()) {
            if (entity.isSprinting()) {
                this.layerBase.playOrContinueBit(this.bitAttackStanceSprint, entityData);
            } else if (entityData.isStillHorizontally()) {
                this.layerBase.playOrContinueBit(this.bitAttackStance, entityData);
            } else {
                this.layerBase.clearAnimation();
            }
        } else {
            this.layerBase.clearAnimation();
        }

        this.layerBase.perform(entityData);
    }
}
