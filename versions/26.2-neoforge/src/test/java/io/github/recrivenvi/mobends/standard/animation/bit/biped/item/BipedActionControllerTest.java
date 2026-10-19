package io.github.recrivenvi.mobends.standard.animation.bit.biped.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.recrivenvi.mobends.standard.UseActionType;
import net.minecraft.world.entity.HumanoidArm;
import org.junit.jupiter.api.Test;

class BipedActionControllerTest {
    @Test
    void replacesCachedUseActionWhenTheActiveHandChanges() {
        assertTrue(
                BipedActionController.shouldReplaceUseAction(
                        UseActionType.BOW, HumanoidArm.RIGHT,
                        UseActionType.BOW, HumanoidArm.LEFT));
    }

    @Test
    void keepsCachedUseActionWhenTypeAndHandStayTheSame() {
        assertFalse(
                BipedActionController.shouldReplaceUseAction(
                        UseActionType.BOW, HumanoidArm.LEFT,
                        UseActionType.BOW, HumanoidArm.LEFT));
    }

    @Test
    void replacesUseActionWhenItsTypeStartsOrStops() {
        assertTrue(
                BipedActionController.shouldReplaceUseAction(
                        null, null, UseActionType.BOW, HumanoidArm.RIGHT));
        assertTrue(
                BipedActionController.shouldReplaceUseAction(
                        UseActionType.BOW, HumanoidArm.RIGHT, null, HumanoidArm.RIGHT));
    }

    @Test
    void recognizesModernVanillaArmPosesWithoutClassifyingThemAsFood() {
        assertEquals(
                VanillaUsePose.CROSSBOW_CHARGE, VanillaUsePose.fromArmPoseName("CROSSBOW_CHARGE"));
        assertEquals(VanillaUsePose.THROW_SPEAR, VanillaUsePose.fromArmPoseName("THROW_TRIDENT"));
        assertEquals(VanillaUsePose.SPEAR, VanillaUsePose.fromArmPoseName("SPEAR"));
        assertEquals(VanillaUsePose.SPYGLASS, VanillaUsePose.fromArmPoseName("SPYGLASS"));
        assertEquals(VanillaUsePose.TOOT_HORN, VanillaUsePose.fromArmPoseName("TOOT_HORN"));
        assertEquals(VanillaUsePose.BRUSH, VanillaUsePose.fromArmPoseName("BRUSH"));
        assertNull(VanillaUsePose.fromArmPoseName("ITEM"));
    }

    @Test
    void mapsModernUseAnimationsThroughTheArmPosesAvailableInEachMinecraftVersion() {
        assertEquals(
                VanillaUsePose.CROSSBOW_CHARGE,
                BipedActionController.getBuiltInVanillaUsePose(
                        BipedActionController.armPoseForUseAnimation("CROSSBOW")));
        assertEquals(
                VanillaUsePose.THROW_SPEAR,
                BipedActionController.getBuiltInVanillaUsePose(
                        BipedActionController.armPoseForUseAnimation("TRIDENT")));
        assertEquals(
                "spear",
                BipedActionController.getBuiltInVanillaUsePose(
                                BipedActionController.armPoseForUseAnimation("SPEAR"))
                        .actionName());
        assertEquals(
                VanillaUsePose.SPYGLASS,
                BipedActionController.getBuiltInVanillaUsePose(
                        BipedActionController.armPoseForUseAnimation("SPYGLASS")));
        assertEquals(
                VanillaUsePose.TOOT_HORN,
                BipedActionController.getBuiltInVanillaUsePose(
                        BipedActionController.armPoseForUseAnimation("TOOT_HORN")));
        assertEquals(
                VanillaUsePose.BRUSH,
                BipedActionController.getBuiltInVanillaUsePose(
                        BipedActionController.armPoseForUseAnimation("BRUSH")));
        assertNull(BipedActionController.armPoseForUseAnimation("BUNDLE"));
    }

    @Test
    void replacesCachedVanillaPoseWhenPoseOrHandChanges() {
        assertFalse(
                BipedActionController.shouldReplaceUseAction(
                        null,
                        VanillaUsePose.SPYGLASS,
                        HumanoidArm.RIGHT,
                        null,
                        VanillaUsePose.SPYGLASS,
                        HumanoidArm.RIGHT));
        assertTrue(
                BipedActionController.shouldReplaceUseAction(
                        null,
                        VanillaUsePose.SPYGLASS,
                        HumanoidArm.RIGHT,
                        null,
                        VanillaUsePose.TOOT_HORN,
                        HumanoidArm.RIGHT));
        assertTrue(
                BipedActionController.shouldReplaceUseAction(
                        null,
                        VanillaUsePose.SPYGLASS,
                        HumanoidArm.RIGHT,
                        null,
                        VanillaUsePose.SPYGLASS,
                        HumanoidArm.LEFT));
    }
}
