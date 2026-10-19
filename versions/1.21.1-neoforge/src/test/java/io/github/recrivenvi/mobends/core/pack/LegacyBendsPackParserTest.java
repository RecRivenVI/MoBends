package io.github.recrivenvi.mobends.core.pack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.recrivenvi.mobends.core.client.model.ModelPartTransform;
import io.github.recrivenvi.mobends.core.data.EntityData;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LegacyBendsPackParserTest {
    @TempDir Path temporaryDirectory;

    @Test
    void parsesMetadataCaseInsensitiveBonesAndChainedCalculations() throws Exception {
        Path file = temporaryDirectory.resolve("classic.bends");
        Files.writeString(
                file,
                """
                name: "Classic Pack"
                author: "Pack Author"
                description: "Legacy syntax"

                target player {
                    anim attack_2 {
                        @rightarm:rot:y += $ticksAfterPunch+=15.2 #0.3
                        @rightArm:prerot:z == 49.7 #1.0
                    }
                }
                """);

        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file.toFile());
        assertEquals("classic", parsed.metadata.key);
        assertEquals("Classic Pack", parsed.metadata.displayName);
        assertEquals("Pack Author", parsed.metadata.author);
        assertEquals("Legacy syntax", parsed.metadata.description);

        List<LegacyBendsPackDefinition.Action> actions =
                parsed.definition.targets.get("mobends-player").get("attack_2");
        assertEquals(2, actions.size());
        assertEquals(2, actions.get(0).calculations.size());
        assertEquals("ticksAfterPunch", actions.get(0).calculations.get(0).variable);
        assertEquals(0.3F, actions.get(0).smoothness);
    }

    @Test
    void ignoresOneMalformedActionWithoutRejectingThePack() throws Exception {
        Path file = temporaryDirectory.resolve("tolerant.bends");
        Files.writeString(
                file,
                """
                name: "Tolerant"
                target player {
                    anim stand {
                        @leftArm:rot:z  #1.0
                        @rightArm:rot:x == -60.0 #0.3
                    }
                }
                """);

        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file.toFile());
        List<LegacyBendsPackDefinition.Action> actions =
                parsed.definition.targets.get("mobends-player").get("stand");
        assertEquals(1, actions.size());
        assertEquals("rightArm", actions.get(0).model);
        assertNull(LegacyBendsPackParser.parseAction("@leftArm:rot:z  #1.0"));
    }

    @Test
    void preservesCustomActionNamesContainingSpaces() throws Exception {
        Path file = temporaryDirectory.resolve("custom-action.bends");
        Files.writeString(
                file,
                """
                name: "Custom Action"
                target player {
                    anim fight pose {
                        @body:prerot:x += 25.0 #1.0
                    }
                }
                """);

        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file.toFile());

        assertEquals(1, parsed.definition.targets.get("mobends-player").get("fight pose").size());
    }

    @Test
    void mapsSimpleLegacyTargetNamesToEveryBuiltInBender() throws Exception {
        Path file = temporaryDirectory.resolve("all-targets.bends");
        Files.writeString(
                file,
                """
                name: "All Targets"
                target player {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target zombie {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target skeleton {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target pigzombie {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target spider {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target squid {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                target wolf {
                    anim stand {
                        @body:rot:x += 1 #1
                    }
                }
                """);

        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file.toFile());

        assertEquals(
                java.util.Set.of(
                        "mobends-player",
                        "mobends-minecraft:zombie",
                        "mobends-minecraft:skeleton",
                        "mobends-minecraft:zombified_piglin",
                        "mobends-minecraft:spider",
                        "mobends-minecraft:squid",
                        "mobends-minecraft:wolf"),
                parsed.definition.targets.keySet());
    }

    @Test
    void appliesRotationAndLegacyPreRotationAfterBuiltInPose() throws Exception {
        Path file = temporaryDirectory.resolve("pose.bends");
        Files.writeString(
                file,
                """
                name: "Pose"
                target player {
                    anim stand {
                        @body:rot:x += 25.0 #1.0
                        @body:prerot:y == 15.0 #1.0
                    }
                }
                """);
        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file.toFile());
        TestEntityData data = new TestEntityData();
        data.body.rotation.orientEulerDegrees(10.0F, 0.0F, 0.0F).finish();

        LegacyBendsPackPerformer.perform(
                data, parsed.definition, "mobends-player", List.of("stand"));

        assertEquals(35.0F, data.body.rotation.getEndEulerDegrees()[0], 0.01F);
        assertFalse(data.body.preRotation.getEnd().isIdentity());
    }

    private static final class TestEntityData
            extends EntityData<net.minecraft.world.entity.Entity> {
        private final ModelPartTransform body = new ModelPartTransform();

        private TestEntityData() {
            super(null);
            nameToPartMap.put("body", body);
        }

        @Override
        public io.github.recrivenvi.mobends.core.animation.controller.IAnimationController<?>
                getController() {
            return null;
        }

        @Override
        public void onTicksRestart() {}
    }
}
