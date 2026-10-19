package io.github.recrivenvi.mobends.core.pack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Parsed representation of the single-file BendsPack format used by Mo' Bends 1.12. */
public final class LegacyBendsPackDefinition {
    final Map<String, Map<String, List<Action>>> targets = new LinkedHashMap<>();

    void addAction(String target, String animation, Action action) {
        targets.computeIfAbsent(target, ignored -> new LinkedHashMap<>())
                .computeIfAbsent(animation, ignored -> new ArrayList<>())
                .add(action);
    }

    public Map<String, Map<String, List<Action>>> getTargets() {
        return targets;
    }

    public enum Property {
        ROTATION,
        PRE_ROTATION,
        SCALE
    }

    public enum Axis {
        X,
        Y,
        Z,
        ALL
    }

    public enum Operator {
        SET,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }

    public enum Modifier {
        COS,
        SIN,
        TAN,
        ABS,
        POW
    }

    public static final class Calculation {
        final Operator operator;
        final float number;
        final String variable;

        Calculation(Operator operator, float number, String variable) {
            this.operator = operator;
            this.number = number;
            this.variable = variable;
        }
    }

    public static final class Action {
        final String model;
        final Property property;
        final Axis axis;
        final float smoothness;
        final Modifier modifier;
        final List<Calculation> calculations;

        Action(
                String model,
                Property property,
                Axis axis,
                float smoothness,
                Modifier modifier,
                List<Calculation> calculations) {
            this.model = model;
            this.property = property;
            this.axis = axis;
            this.smoothness = smoothness;
            this.modifier = modifier;
            this.calculations = List.copyOf(calculations);
        }
    }
}
