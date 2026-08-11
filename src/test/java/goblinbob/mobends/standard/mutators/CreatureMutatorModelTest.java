package goblinbob.mobends.standard.mutators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.blaze3d.vertex.PoseStack;
import goblinbob.mobends.api.rendering.IEntityVertexHelper;
import goblinbob.mobends.core.client.model.BendsCube;
import goblinbob.mobends.core.client.model.BendsModelPart;
import goblinbob.mobends.standard.client.renderer.entity.mutated.WolfRenderer;
import goblinbob.mobends.standard.data.PigZombieData;
import goblinbob.mobends.standard.data.SkeletonData;
import goblinbob.mobends.standard.data.SpiderData;
import goblinbob.mobends.standard.data.SquidData;
import goblinbob.mobends.standard.data.WolfData;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.junit.jupiter.api.Test;

public class CreatureMutatorModelTest {
    private static final float EPSILON = 0.0001F;

    @Test
    public void modernWalkAnimationStateAlreadyContainsTheBabyPhaseMultiplier() {
        WalkAnimationState walkAnimation = new WalkAnimationState();
        walkAnimation.update(1.0F, 1.0F, 3.0F);

        assertEquals(3.0F, walkAnimation.position(1.0F), EPSILON);
    }

    @Test
    public void modernBabyWolfKeepsDedicatedModelSizeButHalvesOriginalAnimationOffsets() {
        TestWolfRenderer renderer = new TestWolfRenderer();

        assertEquals(0.5F, renderer.childAnimationScale(), EPSILON);
        assertEquals(1.0F, renderer.childModelScale(), EPSILON);
        assertEquals(0.5F, renderer.childAnimationScale() / renderer.childModelScale(), EPSILON);
    }

    @Test
    public void squidHasEightFullySegmentedTentacles() {
        SquidMutator mutator = new SquidMutator(SquidData::new);
        assertTrue(mutator.createParts(null, 0.0F));
        assertTrue(mutator.shouldRenderCustom());
        assertEquals(8, mutator.squidTentacles.length);

        for (BendsModelPart[] tentacle : mutator.squidTentacles) {
            assertEquals(SquidData.TENTACLE_SECTIONS, tentacle.length);
            for (int section = 0; section < tentacle.length; ++section) {
                assertTrue(cubeOf(tentacle[section]).isFaceVisible(BendsCube.TOP));
                assertTrue(cubeOf(tentacle[section]).isFaceVisible(BendsCube.BOTTOM));
                assertEquals(48, tentacle[section].getTextureOffsetX());
                assertEquals(0, tentacle[section].getTextureOffsetY());
            }
        }
    }

    @Test
    public void spiderLegGeometryPreservesOriginalDevelopedBoxResize() {
        SpiderMutator mutator = new SpiderMutator(SpiderData::new);
        assertTrue(mutator.createParts(null, 0.0F));

        BendsCube upper = cubeOf(mutator.spiderUpperLimbs[1]);
        assertEquals(-1.0F, upper.minX, EPSILON);
        assertEquals(11.0F, upper.maxX, EPSILON);

        BendsCube lower = cubeOf(mutator.spiderLowerLimbs[1]);
        assertEquals(0.0F, lower.minX, EPSILON);
        assertEquals(12.0F, lower.maxX, EPSILON);
        assertEquals(1.99F, lower.maxY, EPSILON);
        assertEquals(-0.995F, lower.minZ, EPSILON);
        assertEquals(0.995F, lower.maxZ, EPSILON);
    }

    @Test
    public void adultWolfLegsPreserveOriginalFacesAndUv() throws ReflectiveOperationException {
        WolfMutator mutator = new WolfMutator(WolfData::new);
        assertTrue(mutator.createParts(null, 0.0F));
        assertTrue(mutator.shouldRenderCustom());

        for (BendsModelPart upper :
                new BendsModelPart[] {
                    mutator.wolfLeg1, mutator.wolfLeg2, mutator.wolfLeg3, mutator.wolfLeg4
                }) {
            assertTrue(cubeOf(upper).isFaceVisible(BendsCube.BOTTOM));
        }
        for (BendsModelPart lower :
                new BendsModelPart[] {
                    mutator.foreLeg1, mutator.foreLeg2, mutator.foreLeg3, mutator.foreLeg4
                }) {
            BendsCube cube = cubeOf(lower);
            assertTrue(cube.isFaceVisible(BendsCube.TOP));
            assertTrue(cube.isFaceVisible(BendsCube.BOTTOM));
            assertEquals(18.0F / 32.0F, minimumV(cube, BendsCube.BOTTOM), EPSILON);
        }
    }

    @Test
    public void adultWolfPreservesOriginalBodyAndManeGeometry()
            throws ReflectiveOperationException {
        WolfMutator mutator = new WolfMutator(WolfData::new);
        assertTrue(mutator.createParts(null, 0.0F));

        BendsCube body = cubeOf(mutator.wolfBody);
        assertEquals(-3.0F, body.minX, EPSILON);
        assertEquals(-3.0F, body.minY, EPSILON);
        assertEquals(-8.0F, body.minZ, EPSILON);
        assertEquals(3.0F, body.maxX, EPSILON);
        assertEquals(3.0F, body.maxY, EPSILON);
        assertEquals(1.0F, body.maxZ, EPSILON);
        assertEquals(36.0F / 64.0F, minimumU(body, BendsCube.TOP), EPSILON);
        assertEquals(20.0F / 32.0F, minimumV(body, BendsCube.TOP), EPSILON);
        assertEquals(30.0F / 64.0F, minimumU(body, BendsCube.BACK), EPSILON);
        assertEquals(14.0F / 32.0F, minimumV(body, BendsCube.BACK), EPSILON);
        assertEquals(30.0F / 64.0F, minimumU(body, BendsCube.LEFT), EPSILON);
        assertEquals(36.0F / 64.0F, maximumU(body, BendsCube.LEFT), EPSILON);
        assertEquals(20.0F / 32.0F, minimumV(body, BendsCube.LEFT), EPSILON);
        assertEquals(29.0F / 32.0F, maximumV(body, BendsCube.LEFT), EPSILON);
        assertEquals(18.0F / 64.0F, minimumU(body, BendsCube.RIGHT), EPSILON);
        assertEquals(24.0F / 64.0F, maximumU(body, BendsCube.RIGHT), EPSILON);

        BendsCube mane = cubeOf(mutator.wolfMane);
        assertEquals(-4.0F, mane.minX, EPSILON);
        assertEquals(-3.5F, mane.minY, EPSILON);
        assertEquals(-2.0F, mane.minZ, EPSILON);
        assertEquals(4.0F, mane.maxX, EPSILON);
        assertEquals(3.5F, mane.maxY, EPSILON);
        assertEquals(4.0F, mane.maxZ, EPSILON);
    }

    @Test
    public void babyWolfUsesDedicatedMuzzleAndTwoLevelTail() throws ReflectiveOperationException {
        WolfMutator mutator = new WolfMutator(WolfData::new);
        assertTrue(mutator.createParts(null, 0.0F));
        selectBabyWolfParts(mutator);

        BendsCube muzzle = cubeOf(mutator.nose);
        assertEquals(0.76F, mutator.nose.position.y, EPSILON);
        assertEquals(-4.0F, mutator.nose.position.z, EPSILON);
        assertEquals(-1.5F, muzzle.minX, EPSILON);
        assertEquals(-1.0F, muzzle.minY, EPSILON);
        assertEquals(-1.0F, muzzle.minZ, EPSILON);
        assertEquals(1.5F, muzzle.maxX, EPSILON);
        assertEquals(1.0F, muzzle.maxY, EPSILON);
        assertEquals(1.0F, muzzle.maxZ, EPSILON);
        assertFalse(muzzle.isFaceVisible(BendsCube.BOTTOM));
        assertEquals(1.76F, mutator.mouth.position.y, EPSILON);
        assertEquals(-3.0F, mutator.mouth.position.z, EPSILON);
        assertEquals(1.76F, mutator.tongue.position.y, EPSILON);
        assertEquals(-3.0F, mutator.tongue.position.z, EPSILON);

        assertTrue(mutator.wolfTail.getCubes().isEmpty());
        assertEquals(1, mutator.wolfTail.getChildren().size());
        BendsModelPart tailGeometry = mutator.wolfTail.getChildren().get(0);
        assertEquals(-0.6F, tailGeometry.position.y, EPSILON);
        assertEquals(0.2F, tailGeometry.position.z, EPSILON);
        BendsCube tail = cubeOf(tailGeometry);
        assertEquals(-5.7F, tail.minY, EPSILON);
        assertEquals(0.3F, tail.maxY, EPSILON);
        assertFalse(tailGeometry.rotation.getEnd().isIdentity());
    }

    @Test
    public void babyWolfJointCapsUseNativeLegEndUvs() throws ReflectiveOperationException {
        WolfMutator mutator = new WolfMutator(WolfData::new);
        assertTrue(mutator.createParts(null, 0.0F));
        selectBabyWolfParts(mutator);

        BendsModelPart[] upperLegs = {
            mutator.wolfLeg1, mutator.wolfLeg2, mutator.wolfLeg3, mutator.wolfLeg4
        };
        BendsModelPart[] lowerLegs = {
            mutator.foreLeg1, mutator.foreLeg2, mutator.foreLeg3, mutator.foreLeg4
        };
        int[][] textureOrigins = {{0, 22}, {8, 22}, {0, 0}, {20, 0}};
        float[] hingeOrigins = {-1.0F, -1.0F, 1.0F, 1.0F};

        for (int index = 0; index < upperLegs.length; ++index) {
            int u = textureOrigins[index][0];
            int v = textureOrigins[index][1];
            BendsCube upper = cubeOf(upperLegs[index]);
            BendsCube lower = cubeOf(lowerLegs[index]);

            assertTrue(upper.isFaceVisible(BendsCube.BOTTOM));
            assertTrue(lower.isFaceVisible(BendsCube.TOP));
            assertEquals(0.0F, lower.minY, EPSILON);
            assertEquals(1.0F, lower.maxY, EPSILON);
            assertEquals(hingeOrigins[index], lowerLegs[index].position.z, EPSILON);
            assertEquals(-1.0F, lowerLegs[index].position.z + lower.minZ, EPSILON);
            assertEquals(1.0F, lowerLegs[index].position.z + lower.maxZ, EPSILON);
            assertEquals((u + 4.0F) / 32.0F, minimumU(upper, BendsCube.BOTTOM), EPSILON);
            assertEquals((u + 6.0F) / 32.0F, maximumU(upper, BendsCube.BOTTOM), EPSILON);
            assertEquals(v / 32.0F, minimumV(upper, BendsCube.BOTTOM), EPSILON);
            assertEquals((v + 2.0F) / 32.0F, maximumV(upper, BendsCube.BOTTOM), EPSILON);

            assertEquals((u + 2.0F) / 32.0F, minimumU(lower, BendsCube.TOP), EPSILON);
            assertEquals((u + 4.0F) / 32.0F, maximumU(lower, BendsCube.TOP), EPSILON);
            assertEquals(v / 32.0F, minimumV(lower, BendsCube.TOP), EPSILON);
            assertEquals((v + 2.0F) / 32.0F, maximumV(lower, BendsCube.TOP), EPSILON);
        }
    }

    @Test
    public void adultWolfEmitsVisibleGeometryInLivingRendererCoordinates() {
        WolfMutator mutator = new WolfMutator(WolfData::new);
        assertTrue(mutator.createParts(null, 0.0F));

        int[] vertexCount = {0};
        float[] bounds = {
            Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
            Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY
        };
        IEntityVertexHelper previous = IEntityVertexHelper.Holder.getHelper();
        IEntityVertexHelper.Holder.setHelper(
                (consumer, x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ) -> {
                    vertexCount[0]++;
                    bounds[0] = Math.min(bounds[0], x);
                    bounds[1] = Math.min(bounds[1], y);
                    bounds[2] = Math.min(bounds[2], z);
                    bounds[3] = Math.max(bounds[3], x);
                    bounds[4] = Math.max(bounds[4], y);
                    bounds[5] = Math.max(bounds[5], z);
                });

        try {
            PoseStack poseStack = new PoseStack();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            mutator.renderMutated(poseStack, null, 0, 0, -1);
        } finally {
            IEntityVertexHelper.Holder.setHelper(previous);
        }

        assertTrue(vertexCount[0] >= 300);
        for (float bound : bounds) {
            assertTrue(Float.isFinite(bound));
            assertTrue(Math.abs(bound) < 5.0F);
        }
        assertTrue(bounds[3] - bounds[0] > 0.25F);
        assertTrue(bounds[4] - bounds[1] > 0.5F);
        assertTrue(bounds[5] - bounds[2] > 0.5F);
    }

    @Test
    public void skeletonHasSegmentedThinLimbs() {
        SkeletonMutator mutator = new SkeletonMutator(SkeletonData::new);
        assertTrue(mutator.createParts(null, 0.0F));
        assertFalse(cubeOf(mutator.leftArm).isFaceVisible(BendsCube.BOTTOM));
        assertFalse(cubeOf(mutator.leftForeArm).isFaceVisible(BendsCube.TOP));
        assertTrue(cubeOf(mutator.leftLeg).isFaceVisible(BendsCube.BOTTOM));
        assertTrue(cubeOf(mutator.leftForeLeg).isFaceVisible(BendsCube.TOP));
    }

    @Test
    public void adultZombifiedPiglinUsesIndependentLeftSideAtlasRegions() {
        PigZombieMutator mutator = new PigZombieMutator(PigZombieData::new);
        assertTrue(mutator.createParts(null, 0.0F));

        assertEquals(32, mutator.leftArm.getTextureOffsetX());
        assertEquals(48, mutator.leftArm.getTextureOffsetY());
        assertEquals(32, mutator.leftForeArm.getTextureOffsetX());
        assertEquals(54, mutator.leftForeArm.getTextureOffsetY());
        assertEquals(16, mutator.leftLeg.getTextureOffsetX());
        assertEquals(48, mutator.leftLeg.getTextureOffsetY());
        assertEquals(16, mutator.leftForeLeg.getTextureOffsetX());
        assertEquals(54, mutator.leftForeLeg.getTextureOffsetY());
        assertFalse(mutator.leftArm.mirror);
        assertFalse(mutator.leftLeg.mirror);
    }

    private static BendsCube cubeOf(BendsModelPart part) {
        assertEquals(1, part.getCubes().size());
        return part.getCubes().get(0);
    }

    private static void selectBabyWolfParts(WolfMutator mutator)
            throws ReflectiveOperationException {
        Method method = WolfMutator.class.getDeclaredMethod("selectParts", boolean.class);
        method.setAccessible(true);
        method.invoke(mutator, true);
    }

    private static float minimumV(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        Field quadsField = BendsCube.class.getDeclaredField("quads");
        quadsField.setAccessible(true);
        BendsCube.BendsQuad[] quads = (BendsCube.BendsQuad[]) quadsField.get(cube);

        float minimum = Float.POSITIVE_INFINITY;
        for (BendsCube.BendsVertex vertex : quads[faceIndex].vertices) {
            minimum = Math.min(minimum, vertex.v);
        }
        return minimum;
    }

    private static float minimumU(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        Field quadsField = BendsCube.class.getDeclaredField("quads");
        quadsField.setAccessible(true);
        BendsCube.BendsQuad[] quads = (BendsCube.BendsQuad[]) quadsField.get(cube);

        float minimum = Float.POSITIVE_INFINITY;
        for (BendsCube.BendsVertex vertex : quads[faceIndex].vertices) {
            minimum = Math.min(minimum, vertex.u);
        }
        return minimum;
    }

    private static float maximumU(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        float maximum = Float.NEGATIVE_INFINITY;
        for (BendsCube.BendsVertex vertex : quad(cube, faceIndex).vertices) {
            maximum = Math.max(maximum, vertex.u);
        }
        return maximum;
    }

    private static float maximumV(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        float maximum = Float.NEGATIVE_INFINITY;
        for (BendsCube.BendsVertex vertex : quad(cube, faceIndex).vertices) {
            maximum = Math.max(maximum, vertex.v);
        }
        return maximum;
    }

    private static BendsCube.BendsQuad quad(BendsCube cube, int faceIndex)
            throws ReflectiveOperationException {
        Field quadsField = BendsCube.class.getDeclaredField("quads");
        quadsField.setAccessible(true);
        return ((BendsCube.BendsQuad[]) quadsField.get(cube))[faceIndex];
    }

    private static final class TestWolfRenderer extends WolfRenderer<Wolf> {
        private float childAnimationScale() {
            return getChildScale();
        }

        private float childModelScale() {
            return getChildModelScale();
        }
    }
}
