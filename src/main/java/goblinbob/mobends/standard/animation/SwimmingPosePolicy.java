package goblinbob.mobends.standard.animation;

/** Shared swimming-state and root-transform policy for every supported Minecraft family. */
public final class SwimmingPosePolicy {
    private static final float HORIZONTAL_ROOT_PITCH = 80.0F;
    private static final float MIN_VANILLA_PITCH = -90.0F;
    private static final float MAX_VANILLA_PITCH = 90.0F;

    private SwimmingPosePolicy() {}

    /** Starts Mo' Bends swimming only after vanilla has entered its visual swimming pose. */
    public static boolean shouldAnimate(boolean vanillaVisuallySwimming) {
        return vanillaVisuallySwimming;
    }

    /**
     * Keeps vanilla's root rotation for an unmodified player and suppresses it when Mo' Bends owns
     * the complete swimming pose.
     */
    public static float vanillaRootAmount(float swimAmount, boolean mobendsAnimated) {
        return mobendsAnimated ? 0.0F : swimAmount;
    }

    /** Carries vanilla's up/down look direction into Mo' Bends' differently-oriented root chain. */
    public static float mobendsBaseRootPitch(float transition) {
        float clampedTransition = clamp(transition, 0.0F, 1.0F);
        return HORIZONTAL_ROOT_PITCH * clampedTransition;
    }

    /** Applies only the directional pitch around the renderer's body-center pivot. */
    public static float mobendsViewPitch(float transition, float vanillaPitch) {
        float clampedTransition = clamp(transition, 0.0F, 1.0F);
        float clampedPitch = clamp(vanillaPitch, MIN_VANILLA_PITCH, MAX_VANILLA_PITCH);
        return clampedPitch * clampedTransition;
    }

    /** Prevents the root-owned swimming pitch from being applied to the head a second time. */
    public static float mobendsHeadCounterRotation(float transition, float vanillaPitch) {
        return -mobendsBaseRootPitch(transition) - mobendsViewPitch(transition, vanillaPitch);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
