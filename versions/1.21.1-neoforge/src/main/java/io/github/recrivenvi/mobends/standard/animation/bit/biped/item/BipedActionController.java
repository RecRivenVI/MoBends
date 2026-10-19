// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import io.github.recrivenvi.mobends.api.item.IItemCapabilityProvider;
import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.animation.layer.HardAnimationLayer;
import io.github.recrivenvi.mobends.standard.AttackActionType;
import io.github.recrivenvi.mobends.standard.UseActionType;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.EatingAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.ShieldAnimationBit;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

public class BipedActionController {
    protected HardAnimationLayer<BipedEntityData<?>> layerAction = new HardAnimationLayer<>();
    protected UseActionType currentUseActionType = null;
    protected VanillaUsePose currentVanillaUsePose = null;
    protected HumanoidArm currentUseActionHand = null;
    protected AttackActionType currentAttackActionType = null;
    protected AnimationBit<BipedEntityData<?>> actionBit = null;

    private static final Map<UseActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>>
            ITEM_USE_ACTION_MAP = new HashMap<>();
    private static final Map<AttackActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>>
            ITEM_ATTACK_ACTION_MAP = new HashMap<>();

    static {
        ITEM_USE_ACTION_MAP.put(UseActionType.FOOD, EatingAnimationBit::new);
        ITEM_USE_ACTION_MAP.put(UseActionType.BOW, BowAction::new);
        ITEM_USE_ACTION_MAP.put(UseActionType.SHIELD, ShieldAnimationBit::new);

        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.TOOL, ToolAction::new);
        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.FISTS, PunchingAction::new);
        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.SWORD, SwordAction::new);

        // Completeness checks
        for (UseActionType type : UseActionType.values()) {
            if (!ITEM_USE_ACTION_MAP.containsKey(type))
                throw new IllegalStateException(
                        "The ITEM_USE_ACTION_MAP map needs to be complete.");
        }

        // Completeness checks
        for (AttackActionType type : AttackActionType.values()) {
            if (!ITEM_ATTACK_ACTION_MAP.containsKey(type))
                throw new IllegalStateException(
                        "The ITEM_ATTACK_ACTION_MAP map needs to be complete.");
        }
    }

    private static HumanoidModel.ArmPose getAction(LivingEntity entity, ItemStack heldItem) {
        if (!heldItem.isEmpty()) {
            if (entity.getUseItemRemainingTicks() > 0) {
                ItemUseAnimation useAnim = heldItem.getUseAnimation();

                if (useAnim == ItemUseAnimation.BLOCK) return HumanoidModel.ArmPose.BLOCK;
                else if (useAnim == ItemUseAnimation.BOW)
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;

                HumanoidModel.ArmPose modernPose = armPoseForUseAnimation(useAnim.name());
                if (modernPose != null) return modernPose;
            }

            if (heldItem.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(heldItem)) {
                HumanoidModel.ArmPose holdPose = armPoseByName("CROSSBOW_HOLD");
                if (holdPose != null) return holdPose;
            }

            return HumanoidModel.ArmPose.ITEM;
        }

        return HumanoidModel.ArmPose.EMPTY;
    }

    static HumanoidModel.ArmPose armPoseForUseAnimation(String useAnimationName) {
        String[] candidates =
                switch (useAnimationName) {
                    case "CROSSBOW" -> new String[] {"CROSSBOW_CHARGE"};
                    case "TRIDENT" -> new String[] {"THROW_TRIDENT", "THROW_SPEAR"};
                    case "SPEAR" -> new String[] {"SPEAR", "THROW_TRIDENT", "THROW_SPEAR"};
                    case "SPYGLASS", "TOOT_HORN", "BRUSH" -> new String[] {useAnimationName};
                    default -> new String[0];
                };

        for (String candidate : candidates) {
            HumanoidModel.ArmPose pose = armPoseByName(candidate);
            if (pose != null) return pose;
        }
        return null;
    }

    private static HumanoidModel.ArmPose armPoseByName(String name) {
        try {
            return HumanoidModel.ArmPose.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static UseActionType getBuiltInItemUseAction(
            Item item, HumanoidModel.ArmPose armPoseMain, HumanoidModel.ArmPose armPoseOff) {
        if (item == Items.AIR) return null;

        // Use platform abstraction to check if item is food (handles DataComponents in 1.21.1,
        // isEdible in 1.20.1)
        IItemCapabilityProvider capabilityProvider = IItemCapabilityProvider.Holder.getProvider();
        if (capabilityProvider != null && capabilityProvider.isFood(item))
            return UseActionType.FOOD;

        if (item instanceof BowItem
                || armPoseMain == HumanoidModel.ArmPose.BOW_AND_ARROW
                || armPoseOff == HumanoidModel.ArmPose.BOW_AND_ARROW) return UseActionType.BOW;

        if (armPoseMain == HumanoidModel.ArmPose.BLOCK || armPoseOff == HumanoidModel.ArmPose.BLOCK)
            return UseActionType.SHIELD;

        ItemUseAnimation useAnim = new ItemStack(item).getUseAnimation();
        if (useAnim == ItemUseAnimation.EAT || useAnim == ItemUseAnimation.DRINK)
            return UseActionType.FOOD;

        return null;
    }

    static VanillaUsePose getBuiltInVanillaUsePose(HumanoidModel.ArmPose activeArmPose) {
        return activeArmPose == null ? null : VanillaUsePose.fromArmPoseName(activeArmPose.name());
    }

    public static UseActionType getItemUseAction(
            Item item, HumanoidModel.ArmPose armPoseMain, HumanoidModel.ArmPose armPoseOff) {
        UseActionType useActionType = ModConfig.getItemUseAction(item);

        return useActionType != null
                ? useActionType
                : getBuiltInItemUseAction(item, armPoseMain, armPoseOff);
    }

    public static AttackActionType getBuiltInItemAttackAction(Item item) {
        if (item.builtInRegistryHolder().is(ItemTags.SWORDS)) return AttackActionType.SWORD;

        if (item == Items.AIR) return AttackActionType.FISTS;

        return AttackActionType.TOOL;
    }

    public static AttackActionType getItemAttackAction(Item item) {
        AttackActionType attackActionType = ModConfig.getItemAttackAction(item);

        return attackActionType != null ? attackActionType : getBuiltInItemAttackAction(item);
    }

    public void perform(
            BipedEntityData<?> data,
            HumanoidArm primaryHand,
            ItemStack heldItemMainhand,
            ItemStack heldItemOffhand,
            Item activeItem,
            Collection<String> actions) {
        final LivingEntity entity = data.getEntity();
        final HumanoidArm offHand =
                primaryHand == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        final HumanoidModel.ArmPose armPoseMain = getAction(entity, heldItemMainhand);
        final HumanoidModel.ArmPose armPoseOff = getAction(entity, heldItemOffhand);
        final HumanoidArm activeHandSide =
                entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? primaryHand : offHand;
        final HumanoidModel.ArmPose activeArmPose =
                activeHandSide == primaryHand ? armPoseMain : armPoseOff;

        UseActionType useActionType = getItemUseAction(activeItem, armPoseMain, armPoseOff);
        VanillaUsePose vanillaUsePose =
                useActionType == null ? getBuiltInVanillaUsePose(activeArmPose) : null;
        HumanoidArm useActionHand = activeHandSide;
        if (useActionType == null && vanillaUsePose == null) {
            vanillaUsePose = getBuiltInVanillaUsePose(armPoseMain);
            if (vanillaUsePose != null) {
                useActionHand = primaryHand;
            } else {
                vanillaUsePose = getBuiltInVanillaUsePose(armPoseOff);
                useActionHand = offHand;
            }
        }
        if (shouldReplaceUseAction(
                currentUseActionType,
                currentVanillaUsePose,
                currentUseActionHand,
                useActionType,
                vanillaUsePose,
                useActionHand)) {
            currentUseActionType = useActionType;
            currentVanillaUsePose = vanillaUsePose;
            currentUseActionHand =
                    useActionType == null && vanillaUsePose == null ? null : useActionHand;

            if (useActionType != null) {
                ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory =
                        ITEM_USE_ACTION_MAP.get(useActionType);
                this.actionBit = factory.create(useActionHand);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            } else if (vanillaUsePose != null) {
                this.actionBit = new VanillaUseAnimationBit(useActionHand, vanillaUsePose);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            } else {
                this.layerAction.clearAnimation();
                this.currentAttackActionType = null;
            }
        }

        AttackActionType attackActionType = getItemAttackAction(heldItemMainhand.getItem());

        if (this.currentAttackActionType != attackActionType) {
            this.currentAttackActionType = attackActionType;

            ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory =
                    ITEM_ATTACK_ACTION_MAP.get(attackActionType);
            if (factory == null) {
                this.actionBit = null;
                this.layerAction.clearAnimation();
            } else {
                this.actionBit = factory.create(primaryHand);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            }
        }

        this.layerAction.perform(data, actions);
    }

    public void clearAction() {
        layerAction.clearAnimation();
        currentUseActionType = null;
        currentVanillaUsePose = null;
        currentUseActionHand = null;
    }

    static boolean shouldReplaceUseAction(
            UseActionType currentType,
            HumanoidArm currentHand,
            UseActionType nextType,
            HumanoidArm nextHand) {
        return currentType != nextType || nextType != null && currentHand != nextHand;
    }

    static boolean shouldReplaceUseAction(
            UseActionType currentType,
            VanillaUsePose currentVanillaPose,
            HumanoidArm currentHand,
            UseActionType nextType,
            VanillaUsePose nextVanillaPose,
            HumanoidArm nextHand) {
        return currentType != nextType
                || currentVanillaPose != nextVanillaPose
                || (nextType != null || nextVanillaPose != null) && currentHand != nextHand;
    }
}
*/
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import io.github.recrivenvi.mobends.api.item.IItemCapabilityProvider;
import io.github.recrivenvi.mobends.core.animation.bit.AnimationBit;
import io.github.recrivenvi.mobends.core.animation.layer.HardAnimationLayer;
import io.github.recrivenvi.mobends.standard.AttackActionType;
import io.github.recrivenvi.mobends.standard.UseActionType;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.EatingAnimationBit;
import io.github.recrivenvi.mobends.standard.animation.bit.biped.ShieldAnimationBit;
import io.github.recrivenvi.mobends.standard.data.BipedEntityData;
import io.github.recrivenvi.mobends.standard.main.ModConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;

public class BipedActionController {
    protected HardAnimationLayer<BipedEntityData<?>> layerAction = new HardAnimationLayer<>();
    protected UseActionType currentUseActionType = null;
    protected VanillaUsePose currentVanillaUsePose = null;
    protected HumanoidArm currentUseActionHand = null;
    protected AttackActionType currentAttackActionType = null;
    protected AnimationBit<BipedEntityData<?>> actionBit = null;

    private static final Map<UseActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>>
            ITEM_USE_ACTION_MAP = new HashMap<>();
    private static final Map<AttackActionType, ItemActionFactory<AnimationBit<BipedEntityData<?>>>>
            ITEM_ATTACK_ACTION_MAP = new HashMap<>();

    static {
        ITEM_USE_ACTION_MAP.put(UseActionType.FOOD, EatingAnimationBit::new);
        ITEM_USE_ACTION_MAP.put(UseActionType.BOW, BowAction::new);
        ITEM_USE_ACTION_MAP.put(UseActionType.SHIELD, ShieldAnimationBit::new);

        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.TOOL, ToolAction::new);
        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.FISTS, PunchingAction::new);
        ITEM_ATTACK_ACTION_MAP.put(AttackActionType.SWORD, SwordAction::new);

        // Completeness checks
        for (UseActionType type : UseActionType.values()) {
            if (!ITEM_USE_ACTION_MAP.containsKey(type))
                throw new IllegalStateException(
                        "The ITEM_USE_ACTION_MAP map needs to be complete.");
        }

        // Completeness checks
        for (AttackActionType type : AttackActionType.values()) {
            if (!ITEM_ATTACK_ACTION_MAP.containsKey(type))
                throw new IllegalStateException(
                        "The ITEM_ATTACK_ACTION_MAP map needs to be complete.");
        }
    }

    private static HumanoidModel.ArmPose getAction(LivingEntity entity, ItemStack heldItem) {
        if (!heldItem.isEmpty()) {
            if (entity.getUseItemRemainingTicks() > 0) {
                UseAnim useAnim = heldItem.getUseAnimation();

                if (useAnim == UseAnim.BLOCK) return HumanoidModel.ArmPose.BLOCK;
                else if (useAnim == UseAnim.BOW) return HumanoidModel.ArmPose.BOW_AND_ARROW;

                HumanoidModel.ArmPose modernPose = armPoseForUseAnimation(useAnim.name());
                if (modernPose != null) return modernPose;
            }

            if (heldItem.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(heldItem)) {
                HumanoidModel.ArmPose holdPose = armPoseByName("CROSSBOW_HOLD");
                if (holdPose != null) return holdPose;
            }

            return HumanoidModel.ArmPose.ITEM;
        }

        return HumanoidModel.ArmPose.EMPTY;
    }

    static HumanoidModel.ArmPose armPoseForUseAnimation(String useAnimationName) {
        String[] candidates =
                switch (useAnimationName) {
                    case "CROSSBOW" -> new String[] {"CROSSBOW_CHARGE"};
                    case "TRIDENT" -> new String[] {"THROW_TRIDENT", "THROW_SPEAR"};
                    case "SPEAR" -> new String[] {"SPEAR", "THROW_TRIDENT", "THROW_SPEAR"};
                    case "SPYGLASS", "TOOT_HORN", "BRUSH" -> new String[] {useAnimationName};
                    default -> new String[0];
                };

        for (String candidate : candidates) {
            HumanoidModel.ArmPose pose = armPoseByName(candidate);
            if (pose != null) return pose;
        }
        return null;
    }

    private static HumanoidModel.ArmPose armPoseByName(String name) {
        try {
            return HumanoidModel.ArmPose.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static UseActionType getBuiltInItemUseAction(
            Item item, HumanoidModel.ArmPose armPoseMain, HumanoidModel.ArmPose armPoseOff) {
        if (item == Items.AIR) return null;

        // Use platform abstraction to check if item is food (handles DataComponents in 1.21.1,
        // isEdible in 1.20.1)
        IItemCapabilityProvider capabilityProvider = IItemCapabilityProvider.Holder.getProvider();
        if (capabilityProvider != null && capabilityProvider.isFood(item))
            return UseActionType.FOOD;

        if (item instanceof BowItem
                || armPoseMain == HumanoidModel.ArmPose.BOW_AND_ARROW
                || armPoseOff == HumanoidModel.ArmPose.BOW_AND_ARROW) return UseActionType.BOW;

        if (armPoseMain == HumanoidModel.ArmPose.BLOCK || armPoseOff == HumanoidModel.ArmPose.BLOCK)
            return UseActionType.SHIELD;

        UseAnim useAnim = new ItemStack(item).getUseAnimation();
        if (useAnim == UseAnim.EAT || useAnim == UseAnim.DRINK) return UseActionType.FOOD;

        return null;
    }

    static VanillaUsePose getBuiltInVanillaUsePose(HumanoidModel.ArmPose activeArmPose) {
        return activeArmPose == null ? null : VanillaUsePose.fromArmPoseName(activeArmPose.name());
    }

    public static UseActionType getItemUseAction(
            Item item, HumanoidModel.ArmPose armPoseMain, HumanoidModel.ArmPose armPoseOff) {
        UseActionType useActionType = ModConfig.getItemUseAction(item);

        return useActionType != null
                ? useActionType
                : getBuiltInItemUseAction(item, armPoseMain, armPoseOff);
    }

    public static AttackActionType getBuiltInItemAttackAction(Item item) {
        if (item instanceof SwordItem) return AttackActionType.SWORD;

        if (item == Items.AIR) return AttackActionType.FISTS;

        return AttackActionType.TOOL;
    }

    public static AttackActionType getItemAttackAction(Item item) {
        AttackActionType attackActionType = ModConfig.getItemAttackAction(item);

        return attackActionType != null ? attackActionType : getBuiltInItemAttackAction(item);
    }

    public void perform(
            BipedEntityData<?> data,
            HumanoidArm primaryHand,
            ItemStack heldItemMainhand,
            ItemStack heldItemOffhand,
            Item activeItem,
            Collection<String> actions) {
        final LivingEntity entity = data.getEntity();
        final HumanoidArm offHand =
                primaryHand == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        final HumanoidModel.ArmPose armPoseMain = getAction(entity, heldItemMainhand);
        final HumanoidModel.ArmPose armPoseOff = getAction(entity, heldItemOffhand);
        final HumanoidArm activeHandSide =
                entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? primaryHand : offHand;
        final HumanoidModel.ArmPose activeArmPose =
                activeHandSide == primaryHand ? armPoseMain : armPoseOff;

        UseActionType useActionType = getItemUseAction(activeItem, armPoseMain, armPoseOff);
        VanillaUsePose vanillaUsePose =
                useActionType == null ? getBuiltInVanillaUsePose(activeArmPose) : null;
        HumanoidArm useActionHand = activeHandSide;
        if (useActionType == null && vanillaUsePose == null) {
            vanillaUsePose = getBuiltInVanillaUsePose(armPoseMain);
            if (vanillaUsePose != null) {
                useActionHand = primaryHand;
            } else {
                vanillaUsePose = getBuiltInVanillaUsePose(armPoseOff);
                useActionHand = offHand;
            }
        }
        if (shouldReplaceUseAction(
                currentUseActionType,
                currentVanillaUsePose,
                currentUseActionHand,
                useActionType,
                vanillaUsePose,
                useActionHand)) {
            currentUseActionType = useActionType;
            currentVanillaUsePose = vanillaUsePose;
            currentUseActionHand =
                    useActionType == null && vanillaUsePose == null ? null : useActionHand;

            if (useActionType != null) {
                ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory =
                        ITEM_USE_ACTION_MAP.get(useActionType);
                this.actionBit = factory.create(useActionHand);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            } else if (vanillaUsePose != null) {
                this.actionBit = new VanillaUseAnimationBit(useActionHand, vanillaUsePose);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            } else {
                this.layerAction.clearAnimation();
                this.currentAttackActionType = null;
            }
        }

        AttackActionType attackActionType = getItemAttackAction(heldItemMainhand.getItem());

        if (this.currentAttackActionType != attackActionType) {
            this.currentAttackActionType = attackActionType;

            ItemActionFactory<AnimationBit<BipedEntityData<?>>> factory =
                    ITEM_ATTACK_ACTION_MAP.get(attackActionType);
            if (factory == null) {
                this.actionBit = null;
                this.layerAction.clearAnimation();
            } else {
                this.actionBit = factory.create(primaryHand);
                this.layerAction.playOrContinueBit(this.actionBit, data);
            }
        }

        this.layerAction.perform(data, actions);
    }

    public void clearAction() {
        layerAction.clearAnimation();
        currentUseActionType = null;
        currentVanillaUsePose = null;
        currentUseActionHand = null;
    }

    static boolean shouldReplaceUseAction(
            UseActionType currentType,
            HumanoidArm currentHand,
            UseActionType nextType,
            HumanoidArm nextHand) {
        return currentType != nextType || nextType != null && currentHand != nextHand;
    }

    static boolean shouldReplaceUseAction(
            UseActionType currentType,
            VanillaUsePose currentVanillaPose,
            HumanoidArm currentHand,
            UseActionType nextType,
            VanillaUsePose nextVanillaPose,
            HumanoidArm nextHand) {
        return currentType != nextType
                || currentVanillaPose != nextVanillaPose
                || (nextType != null || nextVanillaPose != null) && currentHand != nextHand;
    }
}
// ?}
