package io.github.recrivenvi.mobends.core.pack;

import io.github.recrivenvi.mobends.core.client.model.IModelPart;
import io.github.recrivenvi.mobends.core.data.EntityData;
import io.github.recrivenvi.mobends.core.kumo.KumoExpressionContext;
import io.github.recrivenvi.mobends.lib.math.SmoothOrientation;
import io.github.recrivenvi.mobends.lib.math.vector.IVec3f;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/** Applies the original local BendsPack language after the built-in animation pose. */
final class LegacyBendsPackPerformer {
    private LegacyBendsPackPerformer() {}

    static void perform(
            EntityData<?> entityData,
            LegacyBendsPackDefinition definition,
            String animatedEntityKey,
            @Nullable Collection<String> currentActions) {
        Map<String, List<LegacyBendsPackDefinition.Action>> target =
                definition.targets.get(animatedEntityKey.toLowerCase(Locale.ROOT));
        if (target == null) {
            return;
        }

        KumoExpressionContext variables = new KumoExpressionContext(entityData);
        IdentityHashMap<SmoothOrientation, float[]> preRotations = new IdentityHashMap<>();
        Set<String> actions = new LinkedHashSet<>();
        if (currentActions != null) {
            for (String action : currentActions) {
                if (action != null) {
                    actions.add(action.toLowerCase(Locale.ROOT));
                }
            }
        }

        for (String action : actions) {
            applyActions(entityData, target.get(action), variables, preRotations);
        }
        applyActions(entityData, target.get("all"), variables, preRotations);
    }

    private static void applyActions(
            EntityData<?> entityData,
            @Nullable List<LegacyBendsPackDefinition.Action> actions,
            KumoExpressionContext variables,
            IdentityHashMap<SmoothOrientation, float[]> preRotations) {
        if (actions == null) {
            return;
        }

        for (LegacyBendsPackDefinition.Action action : actions) {
            Object model = resolveModel(entityData, action.model);
            if (action.property == LegacyBendsPackDefinition.Property.SCALE
                    && model instanceof IModelPart part) {
                applyScale(part.getScale(), action, variables);
            } else if (action.property == LegacyBendsPackDefinition.Property.ROTATION) {
                SmoothOrientation rotation =
                        model instanceof IModelPart part
                                ? part.getRotation()
                                : model instanceof SmoothOrientation orientation
                                        ? orientation
                                        : null;
                if (rotation != null) {
                    applyRotation(rotation, action, variables, false, preRotations);
                }
            } else if (action.property == LegacyBendsPackDefinition.Property.PRE_ROTATION
                    && model instanceof IModelPart part) {
                applyRotation(part.getPreRotation(), action, variables, true, preRotations);
            }
        }
    }

    private static void applyRotation(
            SmoothOrientation rotation,
            LegacyBendsPackDefinition.Action action,
            KumoExpressionContext variables,
            boolean preRotation,
            IdentityHashMap<SmoothOrientation, float[]> preRotations) {
        float[] euler =
                preRotation
                        ? preRotations.computeIfAbsent(rotation, ignored -> new float[3])
                        : rotation.getEndEulerDegrees();
        int axis = axisIndex(action.axis);
        if (axis < 0) {
            return;
        }
        euler[axis] = calculate(euler[axis], action, variables);
        rotation.setSmoothness(action.smoothness);
        if (preRotation) {
            rotation.orientLegacyPreEulerDegrees(euler[0], euler[1], euler[2]);
        } else {
            rotation.orientEulerDegrees(euler[0], euler[1], euler[2]);
        }
    }

    private static void applyScale(
            IVec3f scale,
            LegacyBendsPackDefinition.Action action,
            KumoExpressionContext variables) {
        if (action.axis == LegacyBendsPackDefinition.Axis.ALL
                || action.axis == LegacyBendsPackDefinition.Axis.X) {
            scale.setX(calculate(scale.getX(), action, variables));
        }
        if (action.axis == LegacyBendsPackDefinition.Axis.ALL
                || action.axis == LegacyBendsPackDefinition.Axis.Y) {
            scale.setY(calculate(scale.getY(), action, variables));
        }
        if (action.axis == LegacyBendsPackDefinition.Axis.ALL
                || action.axis == LegacyBendsPackDefinition.Axis.Z) {
            scale.setZ(calculate(scale.getZ(), action, variables));
        }
    }

    private static float calculate(
            float input, LegacyBendsPackDefinition.Action action, KumoExpressionContext variables) {
        float output = input;
        for (LegacyBendsPackDefinition.Calculation calculation : action.calculations) {
            float operand =
                    calculation.variable == null
                            ? calculation.number
                            : (float) variables.getVariable(calculation.variable);
            output =
                    switch (calculation.operator) {
                        case SET -> operand;
                        case ADD -> output + operand;
                        case SUBTRACT -> output - operand;
                        case MULTIPLY -> output * operand;
                        case DIVIDE -> operand == 0.0F ? 0.0F : output / operand;
                    };
        }

        if (action.modifier == null) {
            return output;
        }
        return switch (action.modifier) {
            case COS -> (float) Math.cos(output);
            case SIN -> (float) Math.sin(output);
            case TAN -> (float) Math.tan(output);
            case ABS -> Math.abs(output);
            case POW -> (float) Math.pow(input, output);
        };
    }

    private static Object resolveModel(EntityData<?> data, String legacyName) {
        String canonicalName =
                switch (legacyName.toLowerCase(Locale.ROOT)) {
                    case "playerrotation" -> "renderRotation";
                    case "rightitemrotation" -> "renderRightItemRotation";
                    case "leftitemrotation" -> "renderLeftItemRotation";
                    case "leftarm" -> "leftArm";
                    case "rightarm" -> "rightArm";
                    case "leftleg" -> "leftLeg";
                    case "rightleg" -> "rightLeg";
                    case "leftforearm" -> "leftForeArm";
                    case "rightforearm" -> "rightForeArm";
                    case "leftforeleg" -> "leftForeLeg";
                    case "rightforeleg" -> "rightForeLeg";
                    case "body" -> "body";
                    case "head" -> "head";
                    default -> legacyName;
                };
        return data.getPartForName(canonicalName);
    }

    private static int axisIndex(LegacyBendsPackDefinition.Axis axis) {
        return switch (axis) {
            case X -> 0;
            case Y -> 1;
            case Z -> 2;
            case ALL -> -1;
        };
    }
}
