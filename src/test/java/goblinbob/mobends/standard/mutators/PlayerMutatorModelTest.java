package goblinbob.mobends.standard.mutators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import goblinbob.mobends.core.client.model.BendsCube;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.standard.data.PlayerData;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlayerMutatorModelTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void wideAndSlimArmsMatchUpstreamPlayerGeometry() {
        PlayerMutator wide = createMutator(false);
        PlayerMutator slim = createMutator(true);

        assertEquals(4.02F, widthOf(wide.leftArm), EPSILON);
        assertEquals(4.02F, widthOf(wide.rightArm), EPSILON);
        assertEquals(3.02F, widthOf(slim.leftArm), EPSILON);
        assertEquals(3.02F, widthOf(slim.rightArm), EPSILON);
        assertEquals(-10.0F, wide.leftArm.position.y, EPSILON);
        assertEquals(-9.5F, slim.leftArm.position.y, EPSILON);
        assertEquals(-10.0F, wide.rightArm.position.y, EPSILON);
        assertEquals(-9.5F, slim.rightArm.position.y, EPSILON);
        assertEquals(5.0F, slim.leftArm.position.x, EPSILON);
        assertEquals(-5.0F, slim.rightArm.position.x, EPSILON);
    }

    @Test
    public void playerLeftSideUsesItsOwnUnmirroredAtlasRegions() {
        PlayerMutator mutator = createMutator(false);

        for (BendsModelPart part :
                List.of(
                        mutator.leftArm,
                        mutator.leftForeArm,
                        mutator.leftLeg,
                        mutator.leftForeLeg,
                        mutator.leftArmwear,
                        mutator.leftForeArmwear,
                        mutator.leftLegwear,
                        mutator.leftForeLegwear)) {
            assertFalse(part.mirror);
        }
    }

    @Test
    public void limbJointFacesAreNotRendered() {
        PlayerMutator mutator = createMutator(false);

        for (BendsModelPart upperArm :
                List.of(
                        mutator.leftArm,
                        mutator.rightArm,
                        mutator.leftArmwear,
                        mutator.rightArmwear)) {
            assertFalse(cubeOf(upperArm).isFaceVisible(BendsCube.BOTTOM));
            assertTrue(cubeOf(upperArm).isFaceVisible(BendsCube.TOP));
        }

        for (BendsModelPart lowerArm :
                List.of(
                        mutator.leftForeArm,
                        mutator.rightForeArm,
                        mutator.leftForeArmwear,
                        mutator.rightForeArmwear)) {
            assertFalse(cubeOf(lowerArm).isFaceVisible(BendsCube.TOP));
            assertTrue(cubeOf(lowerArm).isFaceVisible(BendsCube.BOTTOM));
        }

        for (BendsModelPart bareLeg :
                List.of(
                        mutator.leftLeg,
                        mutator.rightLeg,
                        mutator.leftForeLeg,
                        mutator.rightForeLeg)) {
            assertTrue(cubeOf(bareLeg).isFaceVisible(BendsCube.TOP));
            assertTrue(cubeOf(bareLeg).isFaceVisible(BendsCube.BOTTOM));
        }

        for (BendsModelPart upperLegWear : List.of(mutator.leftLegwear, mutator.rightLegwear)) {
            assertTrue(cubeOf(upperLegWear).isFaceVisible(BendsCube.TOP));
            assertFalse(cubeOf(upperLegWear).isFaceVisible(BendsCube.BOTTOM));
        }

        for (BendsModelPart lowerLegWear :
                List.of(mutator.leftForeLegwear, mutator.rightForeLegwear)) {
            assertFalse(cubeOf(lowerLegWear).isFaceVisible(BendsCube.TOP));
            assertTrue(cubeOf(lowerLegWear).isFaceVisible(BendsCube.BOTTOM));
        }
    }

    @Test
    public void wearSegmentsPreserveOriginalResizedBounds() {
        PlayerMutator mutator = createMutator(false);

        BendsCube upperArmWear = cubeOf(mutator.leftArmwear);
        assertEquals(-1.2525F, upperArmWear.minX, EPSILON);
        assertEquals(3.2525F, upperArmWear.maxX, EPSILON);
        assertEquals(-2.25F, upperArmWear.minY, EPSILON);
        assertEquals(4.0F, upperArmWear.maxY, EPSILON);

        BendsCube foreArmWear = cubeOf(mutator.leftForeArmwear);
        assertEquals(-1.255F, foreArmWear.minX, EPSILON);
        assertEquals(3.255F, foreArmWear.maxX, EPSILON);
        assertEquals(0.0F, foreArmWear.minY, EPSILON);
        assertEquals(6.25F, foreArmWear.maxY, EPSILON);
    }

    @Test
    public void lowerWearUsesOriginalBottomFaceRows() throws ReflectiveOperationException {
        PlayerMutator mutator = createMutator(false);

        assertEquals(
                48.0F / 64.0F,
                minimumV(cubeOf(mutator.leftForeArmwear), BendsCube.BOTTOM),
                EPSILON);
        assertEquals(
                32.0F / 64.0F,
                minimumV(cubeOf(mutator.rightForeArmwear), BendsCube.BOTTOM),
                EPSILON);
        assertEquals(
                48.0F / 64.0F,
                minimumV(cubeOf(mutator.leftForeLegwear), BendsCube.BOTTOM),
                EPSILON);
        assertEquals(
                32.0F / 64.0F,
                minimumV(cubeOf(mutator.rightForeLegwear), BendsCube.BOTTOM),
                EPSILON);
    }

    @Test
    public void bottomFaceKeepsOriginalReversedVDirection() throws ReflectiveOperationException {
        PlayerMutator mutator = createMutator(false);
        BendsCube.BendsVertex[] vertices =
                quad(cubeOf(mutator.leftForeArmwear), BendsCube.BOTTOM).vertices;

        assertEquals(52.0F / 64.0F, vertices[0].v, EPSILON);
        assertEquals(52.0F / 64.0F, vertices[1].v, EPSILON);
        assertEquals(48.0F / 64.0F, vertices[2].v, EPSILON);
        assertEquals(48.0F / 64.0F, vertices[3].v, EPSILON);
    }

    @Test
    public void skinLayerVisibilityFollowsAvatarRenderStateFlags() {
        PlayerMutator mutator = createMutator(false);

        mutator.syncPlayerVisibility(false, false, true, false, true, false, true);

        assertTrue(mutator.body.isShowing());
        assertTrue(mutator.head.isShowing());
        assertFalse(mutator.headwear.isShowing());
        assertTrue(mutator.bodywear.isShowing());
        assertFalse(mutator.leftArmwear.isShowing());
        assertFalse(mutator.leftForeArmwear.isShowing());
        assertTrue(mutator.rightArmwear.isShowing());
        assertTrue(mutator.rightForeArmwear.isShowing());
        assertFalse(mutator.leftLegwear.isShowing());
        assertFalse(mutator.leftForeLegwear.isShowing());
        assertTrue(mutator.rightLegwear.isShowing());
        assertTrue(mutator.rightForeLegwear.isShowing());

        mutator.syncPlayerVisibility(true, true, true, true, true, true, true);

        assertFalse(mutator.body.isShowing());
        assertTrue(mutator.head.isShowing());
        assertTrue(mutator.headwear.isShowing());
        assertFalse(mutator.leftArm.isShowing());
        assertFalse(mutator.rightArm.isShowing());
        assertFalse(mutator.leftLeg.isShowing());
        assertFalse(mutator.rightLeg.isShowing());
    }

    private static PlayerMutator createMutator(boolean slim) {
        PlayerMutator mutator = new PlayerMutator(PlayerData::new);
        mutator.smallArms = slim;
        assertTrue(mutator.createParts(null, 0.0F));
        return mutator;
    }

    private static BendsCube cubeOf(BendsModelPart part) {
        assertEquals(1, part.getCubes().size());
        return part.getCubes().get(0);
    }

    private static float widthOf(BendsModelPart part) {
        BendsCube cube = cubeOf(part);
        return cube.maxX - cube.minX;
    }

    private static float minimumV(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        float minimum = Float.POSITIVE_INFINITY;
        for (BendsCube.BendsVertex vertex : quad(cube, faceIndex).vertices) {
            minimum = Math.min(minimum, vertex.v);
        }
        return minimum;
    }

    private static BendsCube.BendsQuad quad(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        Field quadsField = BendsCube.class.getDeclaredField("quads");
        quadsField.setAccessible(true);
        return ((BendsCube.BendsQuad[]) quadsField.get(cube))[faceIndex];
    }
}
