package goblinbob.mobends.core.pack;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;

/** Reader for the local, non-networked BendsPack language shipped with Mo' Bends 1.12. */
public final class LegacyBendsPackParser {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern METADATA =
            Pattern.compile("^(name|author|description)\\s*:\\s*(.*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TARGET =
            Pattern.compile("^target\\s+(.+?)\\s*\\{\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ANIMATION =
            Pattern.compile("^anim\\s+(.+?)\\s*\\{\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACTION =
            Pattern.compile(
                    "^@([^:]+):(rot|prerot|scale):([xyz]?)\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CALCULATION =
            Pattern.compile(
                    "([+\\-*/=]=)\\s*(?::(cos|sin|tan|abs|pow):)?\\s*"
                            + "(\\$[A-Za-z][A-Za-z0-9_]*|[-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][-+]?\\d+)?)",
                    Pattern.CASE_INSENSITIVE);

    private LegacyBendsPackParser() {}

    public static ParsedPack parse(File file) throws IOException {
        BendsPackMeta metadata = new BendsPackMeta();
        metadata.key = stripExtension(file.getName());
        metadata.displayName = metadata.key;
        metadata.author = "";
        metadata.description = "";

        LegacyBendsPackDefinition definition = new LegacyBendsPackDefinition();
        String currentTarget = null;
        String currentAnimation = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String rawLine;
            int lineNumber = 0;
            while ((rawLine = reader.readLine()) != null) {
                lineNumber++;
                String line = rawLine.strip();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                Matcher metadataMatcher = METADATA.matcher(line);
                if (metadataMatcher.matches()) {
                    String value = unquote(metadataMatcher.group(2).strip());
                    switch (metadataMatcher.group(1).toLowerCase(Locale.ROOT)) {
                        case "name" -> metadata.displayName = value;
                        case "author" -> metadata.author = value;
                        case "description" -> metadata.description = value;
                        default -> {}
                    }
                    continue;
                }

                Matcher targetMatcher = TARGET.matcher(line);
                if (targetMatcher.matches()) {
                    currentTarget = canonicalTarget(targetMatcher.group(1));
                    currentAnimation = null;
                    continue;
                }

                Matcher animationMatcher = ANIMATION.matcher(line);
                if (animationMatcher.matches()) {
                    currentAnimation = animationMatcher.group(1).strip().toLowerCase(Locale.ROOT);
                    continue;
                }

                if (line.startsWith("@") && currentTarget != null && currentAnimation != null) {
                    LegacyBendsPackDefinition.Action action = parseAction(line);
                    if (action == null) {
                        LOGGER.warn(
                                "Ignoring malformed legacy BendsPack action in {} at line {}: {}",
                                file.getName(),
                                lineNumber,
                                line);
                    } else {
                        definition.addAction(currentTarget, currentAnimation, action);
                    }
                }
            }
        }

        return new ParsedPack(metadata, definition);
    }

    static LegacyBendsPackDefinition.Action parseAction(String line) {
        float smoothness = 1.0F;
        int smoothMarker = line.lastIndexOf('#');
        if (smoothMarker >= 0) {
            try {
                smoothness = Float.parseFloat(line.substring(smoothMarker + 1).strip());
            } catch (NumberFormatException exception) {
                return null;
            }
            line = line.substring(0, smoothMarker).stripTrailing();
        }

        Matcher actionMatcher = ACTION.matcher(line);
        if (!actionMatcher.matches()) {
            return null;
        }

        String propertyName = actionMatcher.group(2).toLowerCase(Locale.ROOT);
        LegacyBendsPackDefinition.Property property =
                switch (propertyName) {
                    case "rot" -> LegacyBendsPackDefinition.Property.ROTATION;
                    case "prerot" -> LegacyBendsPackDefinition.Property.PRE_ROTATION;
                    case "scale" -> LegacyBendsPackDefinition.Property.SCALE;
                    default ->
                            throw new IllegalStateException("Unexpected property " + propertyName);
                };
        String axisName = actionMatcher.group(3).toLowerCase(Locale.ROOT);
        LegacyBendsPackDefinition.Axis axis =
                switch (axisName) {
                    case "x" -> LegacyBendsPackDefinition.Axis.X;
                    case "y" -> LegacyBendsPackDefinition.Axis.Y;
                    case "z" -> LegacyBendsPackDefinition.Axis.Z;
                    default -> LegacyBendsPackDefinition.Axis.ALL;
                };

        String expression = actionMatcher.group(4).strip();
        Matcher calculationMatcher = CALCULATION.matcher(expression);
        List<LegacyBendsPackDefinition.Calculation> calculations = new ArrayList<>();
        LegacyBendsPackDefinition.Modifier modifier = null;
        int consumed = 0;
        while (calculationMatcher.find()) {
            if (!expression.substring(consumed, calculationMatcher.start()).isBlank()) {
                return null;
            }
            consumed = calculationMatcher.end();

            LegacyBendsPackDefinition.Operator operator =
                    switch (calculationMatcher.group(1)) {
                        case "==" -> LegacyBendsPackDefinition.Operator.SET;
                        case "+=" -> LegacyBendsPackDefinition.Operator.ADD;
                        case "-=" -> LegacyBendsPackDefinition.Operator.SUBTRACT;
                        case "*=" -> LegacyBendsPackDefinition.Operator.MULTIPLY;
                        case "/=" -> LegacyBendsPackDefinition.Operator.DIVIDE;
                        default -> throw new IllegalStateException("Unexpected operator");
                    };
            if (calculationMatcher.group(2) != null) {
                modifier =
                        LegacyBendsPackDefinition.Modifier.valueOf(
                                calculationMatcher.group(2).toUpperCase(Locale.ROOT));
            }

            String operand = calculationMatcher.group(3);
            String variable = operand.startsWith("$") ? operand.substring(1) : null;
            float number = variable == null ? Float.parseFloat(operand) : 0.0F;
            calculations.add(new LegacyBendsPackDefinition.Calculation(operator, number, variable));
        }

        if (calculations.isEmpty() || !expression.substring(consumed).isBlank()) {
            return null;
        }

        return new LegacyBendsPackDefinition.Action(
                actionMatcher.group(1).strip(), property, axis, smoothness, modifier, calculations);
    }

    static String canonicalTarget(String target) {
        String normalized = target.strip().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("mobends-")) {
            return normalized;
        }
        if (normalized.contains(":")) {
            return "mobends-" + normalized;
        }

        return switch (normalized.replace('-', '_').replace(' ', '_')) {
            case "player" -> "mobends-player";
            case "zombie", "skeleton", "spider", "squid", "wolf" ->
                    "mobends-minecraft:" + normalized;
            case "pigzombie", "pig_zombie", "zombie_pigman", "zombified_piglin" ->
                    "mobends-minecraft:zombified_piglin";
            default -> "mobends-" + normalized;
        };
    }

    private static String stripExtension(String filename) {
        return filename.toLowerCase(Locale.ROOT).endsWith(".bends")
                ? filename.substring(0, filename.length() - ".bends".length())
                : filename;
    }

    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public static final class ParsedPack {
        final BendsPackMeta metadata;
        final LegacyBendsPackDefinition definition;

        ParsedPack(BendsPackMeta metadata, LegacyBendsPackDefinition definition) {
            this.metadata = metadata;
            this.definition = definition;
        }
    }
}
