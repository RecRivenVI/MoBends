package io.github.recrivenvi.mobends.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class StartupCheckTest {
    private JsonObject observation() {
        return JsonParser.parseString(
                        """
                {"player":"RecRivenVI","registeredBenders":7,"playerAnimated":true,
                 "entityDataAvailable":true,"framebufferWidth":1920,"framebufferHeight":1080,
                 "masterVolume":0,"movementDistance":1,"noGravity":false,"noPhysics":false,
                 "flying":false,"onGround":true,"positionBefore":"same","positionAfter":"same",
                 "idlePose":{"leftKneeQuaternion":[0,0,0,1]},
                 "walkingPose110":{"leftKneeQuaternion":[0.1,0,0,0.995]},
                 "walkingPose120":{"leftKneeQuaternion":[0,0,0,1]},
                 "walkingPose130":{"leftKneeQuaternion":[0,0,0,1]}}
                """)
                .getAsJsonObject();
    }

    @Test
    void acceptsBoundedObservation() {
        assertDoesNotThrow(() -> StartupCheck.validate(observation()));
    }

    @Test
    void rejectsFlyingResidue() {
        var value = observation();
        value.addProperty("flying", true);
        assertThrows(IllegalStateException.class, () -> StartupCheck.validate(value));
    }

    @Test
    void rejectsUnrestoredPosition() {
        var value = observation();
        value.addProperty("positionAfter", "different");
        assertThrows(IllegalStateException.class, () -> StartupCheck.validate(value));
    }

    @Test
    void rejectsRegistrationWithoutJointProgression() {
        var value = observation();
        value.add("walkingPose110", value.get("idlePose").deepCopy());
        assertThrows(IllegalStateException.class, () -> StartupCheck.validate(value));
    }

    @Test
    void rejectsWrongFramebuffer() {
        var value = observation();
        value.addProperty("framebufferWidth", 1280);
        assertThrows(IllegalStateException.class, () -> StartupCheck.validate(value));
    }
}
