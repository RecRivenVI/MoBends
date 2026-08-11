package goblinbob.mobends.core.pack;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonParseException;
import goblinbob.mobends.core.kumo.KumoSerializer;
import goblinbob.mobends.core.kumo.state.condition.AndCondition;
import goblinbob.mobends.core.kumo.state.condition.EquipmentNameCondition;
import goblinbob.mobends.core.kumo.state.condition.ExpressionCondition;
import goblinbob.mobends.core.kumo.state.condition.NotCondition;
import goblinbob.mobends.core.kumo.state.condition.OrCondition;
import goblinbob.mobends.core.kumo.state.condition.StateCondition;
import goblinbob.mobends.core.kumo.state.condition.TicksPassedCondition;
import goblinbob.mobends.core.kumo.state.condition.TriggerConditionRegistry;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.KeyframeLayerTemplate;
import goblinbob.mobends.core.kumo.state.template.keyframe.StandardKeyframeNodeTemplate;
import goblinbob.mobends.standard.kumo.WolfStateCondition;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KumoPackCompatibilityTest {
    private static final List<String> BUILT_IN_TARGETS =
            List.of(
                    "mobends-player",
                    "mobends-minecraft:zombie",
                    "mobends-minecraft:skeleton",
                    "mobends-minecraft:zombified_piglin",
                    "mobends-minecraft:spider",
                    "mobends-minecraft:squid",
                    "mobends-minecraft:wolf");

    @TempDir Path temporaryDirectory;

    @BeforeAll
    static void registerBuiltInAddonCondition() {
        TriggerConditionRegistry.instance.register(
                "mobends:wolf_state", WolfStateCondition::new, WolfStateCondition.Template.class);
    }

    @Test
    void deserializesAndValidatesEveryBuiltInTargetAndKumoCondition() throws Exception {
        String targetEntries =
                BUILT_IN_TARGETS.stream()
                        .map(key -> "\"" + key + "\":" + animatorJson())
                        .reduce((left, right) -> left + "," + right)
                        .orElseThrow();
        String json =
                "{\"targets\":{"
                        + targetEntries
                        + "},"
                        + "\"keyframeAnimations\":{\"embedded\":{\"bones\":{}}}}";

        BendsPackData data = KumoSerializer.INSTANCE.gson.fromJson(json, BendsPackData.class);
        PackDataProvider.INSTANCE.validatePackData(data);

        assertEquals(
                BUILT_IN_TARGETS,
                BUILT_IN_TARGETS.stream().filter(data.targets::containsKey).toList());
        assertNotNull(data.getAnimation("embedded"));

        KeyframeLayerTemplate layer =
                (KeyframeLayerTemplate) data.targets.get("mobends-player").layers.get(0);
        StandardKeyframeNodeTemplate node = (StandardKeyframeNodeTemplate) layer.nodes.get(0);
        List<TriggerConditionTemplate> conditions =
                node.connections.stream().map(connection -> connection.triggerCondition).toList();

        assertAll(
                () -> assertInstanceOf(AndCondition.Template.class, conditions.get(0)),
                () -> assertInstanceOf(TicksPassedCondition.Template.class, conditions.get(1)),
                () -> assertInstanceOf(EquipmentNameCondition.Template.class, conditions.get(2)),
                () -> assertInstanceOf(ExpressionCondition.Template.class, conditions.get(3)),
                () -> assertEquals(TriggerConditionTemplate.class, conditions.get(4).getClass()),
                () -> assertInstanceOf(WolfStateCondition.Template.class, conditions.get(5)));

        AndCondition.Template and = (AndCondition.Template) conditions.get(0);
        assertInstanceOf(StateCondition.Template.class, and.conditions.get(0));
        OrCondition.Template or = (OrCondition.Template) and.conditions.get(1);
        assertInstanceOf(NotCondition.Template.class, or.conditions.get(0));
        assertEquals("core:animation_finished", or.conditions.get(1).getType());
    }

    @Test
    void rejectsUnknownSerializedKumoTypes() {
        assertAll(
                () ->
                        assertThrows(
                                JsonParseException.class,
                                () ->
                                        parse(
                                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"DRIVER"}]}}}
                        """)),
                () ->
                        assertThrows(
                                JsonParseException.class,
                                () ->
                                        parse(
                                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"addon:missing","connections":[]}
                        ]}]}}}
                        """)),
                () ->
                        assertThrows(
                                JsonParseException.class,
                                () ->
                                        parse(
                                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[
                            {"targetNodeIndex":0,"triggerCondition":{"type":"addon:missing"}}
                          ]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """)));
    }

    @Test
    void reportsUnknownKumoTypesAsInvalidLocalPacks() throws Exception {
        Path metadata = temporaryDirectory.resolve("invalid.bendsmeta");
        Path data = temporaryDirectory.resolve("invalid.bends");
        Files.writeString(
                metadata,
                """
                {"key":"invalid","displayName":"Invalid KUMO"}
                """);
        Files.writeString(
                data,
                """
                {"targets":{"mobends-player":{"layers":[{"type":"DRIVER"}]}}}
                """);
        LocalBendsPack pack = LocalBendsPack.readFromFile(metadata.toFile());
        PackDataProvider.INSTANCE.clearCache();

        InvalidPackFormatException exception =
                assertThrows(
                        InvalidPackFormatException.class,
                        () -> PackDataProvider.INSTANCE.getDataForPack(pack));

        assertEquals("Invalid KUMO", exception.getPackName());
    }

    @Test
    void rejectsNullPackDocuments() {
        assertThrows(
                MalformedKumoTemplateException.class,
                () -> PackDataProvider.INSTANCE.validatePackData(null));
    }

    @Test
    void rejectsMalformedGraphsBeforeTheyReachRuntime() {
        Map<String, String> malformedPacks =
                Map.of(
                        "empty target layers",
                                """
                        {"targets":{"mobends-player":{"layers":[]}}}
                        """,
                        "empty keyframe nodes",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[]}]}}}
                        """,
                        "entry node out of bounds",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","entryNode":1,"nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """,
                        "connection target out of bounds",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[
                            {"targetNodeIndex":2,"triggerCondition":{"type":"core:animation_finished"}}
                          ]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """,
                        "missing standard animation",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"missing","connections":[]}
                        ]}]}}}
                        """,
                        "missing movement animation",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:movement","animationKey":"missing","connections":[]}
                        ]}]}}}
                        """,
                        "empty and condition",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[
                            {"targetNodeIndex":0,"triggerCondition":{"type":"core:and","conditions":[]}}
                          ]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """,
                        "invalid equipment pattern",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[
                            {"targetNodeIndex":0,"triggerCondition":{"type":"core:equipment_name","namePattern":"[","slot":"MAINHAND"}}
                          ]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """,
                        "negative tick condition",
                                """
                        {"targets":{"mobends-player":{"layers":[{"type":"KEYFRAME","nodes":[
                          {"type":"core:standard","animationKey":"embedded","connections":[
                            {"targetNodeIndex":0,"triggerCondition":{"type":"core:ticks_passed","ticksToPass":-1}}
                          ]}
                        ]}]}},"keyframeAnimations":{"embedded":{"bones":{}}}}
                        """);

        assertAll(
                malformedPacks.entrySet().stream()
                        .map(
                                entry ->
                                        () ->
                                                assertThrows(
                                                        MalformedKumoTemplateException.class,
                                                        () ->
                                                                PackDataProvider.INSTANCE
                                                                        .validatePackData(
                                                                                parse(
                                                                                        entry
                                                                                                .getValue())),
                                                        entry.getKey())));
    }

    @Test
    void validatesProceduralLayersAndEmbeddedAnimations() throws Exception {
        BendsPackData data =
                parse(
                        """
                {
                  "targets": {
                    "mobends-player": {
                      "layers": [
                        {
                          "type": "PROCEDURAL",
                          "bones": {
                            "body": {"rotationX": "sin(ticks) * 5"}
                          },
                          "blendWeight": 0.5,
                          "additive": true
                        }
                      ]
                    }
                  },
                  "keyframeAnimations": {"embedded": {"bones": {}}}
                }
                """);

        PackDataProvider.INSTANCE.validatePackData(data);
        assertTrue(data.targets.containsKey("mobends-player"));
        assertNotNull(data.getAnimation("embedded"));
    }

    private static BendsPackData parse(String json) {
        return KumoSerializer.INSTANCE.gson.fromJson(json, BendsPackData.class);
    }

    private static String animatorJson() {
        return """
                {
                  "layers": [
                    {
                      "type": "KEYFRAME",
                      "entryNode": 0,
                      "nodes": [
                        {
                          "type": "core:standard",
                          "animationKey": "embedded",
                          "connections": [
                            {
                              "targetNodeIndex": 1,
                              "transitionDuration": 2,
                              "triggerCondition": {
                                "type": "core:and",
                                "conditions": [
                                  {"type":"core:state","state":"ON_GROUND"},
                                  {
                                    "type":"core:or",
                                    "conditions":[
                                      {"type":"core:not","condition":{"type":"core:state","state":"AIRBORNE"}},
                                      {"type":"core:animation_finished"}
                                    ]
                                  }
                                ]
                              }
                            },
                            {"targetNodeIndex":1,"triggerCondition":{"type":"core:ticks_passed","ticksToPass":0}},
                            {"targetNodeIndex":1,"triggerCondition":{"type":"core:equipment_name","namePattern":".*","slot":"MAINHAND"}},
                            {"targetNodeIndex":1,"triggerCondition":{"type":"core:expression","expression":"1"}},
                            {"targetNodeIndex":1,"triggerCondition":{"type":"core:animation_finished"}},
                            {"targetNodeIndex":1,"triggerCondition":{"type":"mobends:wolf_state","state":"SITTING"}}
                          ]
                        },
                        {
                          "type": "core:movement",
                          "animationKey": "embedded",
                          "connections": []
                        }
                      ]
                    }
                  ]
                }
                """;
    }
}
