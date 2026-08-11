package goblinbob.mobends.lib.math;

import goblinbob.mobends.lib.util.EnumAxis;

public class SmoothOrientation {
    public static final float PI = (float) Math.PI;

    protected Quaternion start;
    protected Quaternion end;
    protected Quaternion smooth;
    protected float progress;
    protected float smoothness;

    public SmoothOrientation() {
        this.start = new Quaternion();
        this.end = new Quaternion();
        this.smooth = new Quaternion();
        this.progress = 1.0F;
        this.smoothness = 1.0F;
    }

    public Quaternion getEnd() {
        return this.end;
    }

    public Quaternion getSmooth() {
        return this.smooth;
    }

    /** Returns the target quaternion as intrinsic X/Y/Z Euler angles in degrees. */
    public float[] getEndEulerDegrees() {
        float sinXCosY = 2.0F * (end.w * end.x + end.y * end.z);
        float cosXCosY = 1.0F - 2.0F * (end.x * end.x + end.y * end.y);
        float sinY = 2.0F * (end.w * end.y - end.z * end.x);
        float sinZCosY = 2.0F * (end.w * end.z + end.x * end.y);
        float cosZCosY = 1.0F - 2.0F * (end.y * end.y + end.z * end.z);

        float x = (float) Math.atan2(sinXCosY, cosXCosY);
        float y =
                Math.abs(sinY) >= 1.0F
                        ? (float) Math.copySign(Math.PI / 2.0, sinY)
                        : (float) Math.asin(sinY);
        float z = (float) Math.atan2(sinZCosY, cosZCosY);
        float radiansToDegrees = 180.0F / PI;
        return new float[] {x * radiansToDegrees, y * radiansToDegrees, z * radiansToDegrees};
    }

    /** Sets the target from intrinsic X/Y/Z Euler angles in degrees. */
    public SmoothOrientation orientEulerDegrees(float x, float y, float z) {
        float halfX = x * PI / 360.0F;
        float halfY = y * PI / 360.0F;
        float halfZ = z * PI / 360.0F;
        float sinX = (float) Math.sin(halfX);
        float cosX = (float) Math.cos(halfX);
        float sinY = (float) Math.sin(halfY);
        float cosY = (float) Math.cos(halfY);
        float sinZ = (float) Math.sin(halfZ);
        float cosZ = (float) Math.cos(halfZ);

        this.start.set(this.smooth);
        this.end.set(
                sinX * cosY * cosZ - cosX * sinY * sinZ,
                cosX * sinY * cosZ + sinX * cosY * sinZ,
                cosX * cosY * sinZ - sinX * sinY * cosZ,
                cosX * cosY * cosZ + sinX * sinY * sinZ);
        this.end.normalise();
        this.progress = 0.0F;
        this.updateSmooth();
        return this;
    }

    /**
     * Reproduces the rotation order used by the 1.12 BendsPack {@code prerot} property: negative Y,
     * then X, then Z.
     */
    public SmoothOrientation orientLegacyPreEulerDegrees(float x, float y, float z) {
        Quaternion rotationX = new Quaternion();
        Quaternion rotationY = new Quaternion();
        Quaternion rotationZ = new Quaternion();
        rotationX.setFromAxisAngle(1.0F, 0.0F, 0.0F, x * PI / 180.0F);
        rotationY.setFromAxisAngle(0.0F, 1.0F, 0.0F, -y * PI / 180.0F);
        rotationZ.setFromAxisAngle(0.0F, 0.0F, 1.0F, z * PI / 180.0F);

        Quaternion xThenZ = Quaternion.mul(rotationX, rotationZ, new Quaternion());
        Quaternion target = Quaternion.mul(rotationY, xThenZ, new Quaternion());
        this.start.set(this.smooth);
        this.end.set(target);
        this.end.normalise();
        this.progress = 0.0F;
        this.updateSmooth();
        return this;
    }

    public void set(SmoothOrientation other) {
        this.start.set(other.start);
        this.end.set(other.end);
        this.smooth.set(other.smooth);
        this.progress = other.progress;
    }

    public SmoothOrientation setSmoothness(float smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    public SmoothOrientation set(float x, float y, float z, float w) {
        this.start.set(x, y, z, w);
        this.start.normalise();
        this.end.set(this.start);
        this.smooth.set(this.start);
        this.progress = 0F;
        return this;
    }

    public SmoothOrientation add(float x, float y, float z, float w) {
        this.start.x += x;
        this.start.y += y;
        this.start.z += z;
        this.start.w += w;
        this.end.set(this.start);
        this.smooth.set(this.start);
        this.progress = 0F;
        return this;
    }

    public SmoothOrientation orient(float angle, float x, float y, float z) {
        this.start.set(this.smooth);
        this.end.setFromAxisAngle(x, y, z, angle / 180F * PI);
        this.progress = 0F;
        this.updateSmooth();
        return this;
    }

    public SmoothOrientation orientX(float angle) {
        return this.orient(angle, 1F, 0F, 0F);
    }

    public SmoothOrientation orientY(float angle) {
        return this.orient(angle, 0F, 1F, 0F);
    }

    public SmoothOrientation orientZ(float angle) {
        return this.orient(angle, 0F, 0F, 1F);
    }

    public SmoothOrientation orientInstant(float a, float x, float y, float z) {
        this.end.setFromAxisAngle(x, y, z, a / 180.0F * PI);
        this.start.set(this.end);
        this.smooth.set(this.end);
        return this;
    }

    public SmoothOrientation orientInstantX(float angle) {
        return this.orientInstant(angle, 1F, 0F, 0F);
    }

    public SmoothOrientation orientInstantY(float angle) {
        return this.orientInstant(angle, 0F, 1F, 0F);
    }

    public SmoothOrientation orientInstantZ(float angle) {
        return this.orientInstant(angle, 0F, 0F, 1F);
    }

    public SmoothOrientation rotate(float angle, float x, float y, float z) {
        this.end.rotate(x, y, z, angle / 180.0F * PI);
        this.updateSmooth();
        return this;
    }

    public SmoothOrientation rotateX(float angle) {
        return this.rotate(angle, 1F, 0F, 0F);
    }

    public SmoothOrientation rotateY(float angle) {
        return this.rotate(angle, 0F, 1F, 0F);
    }

    public SmoothOrientation rotateZ(float angle) {
        return this.rotate(angle, 0F, 0F, 1F);
    }

    public SmoothOrientation rotateInstant(float angle, float x, float y, float z) {
        Quaternion rotation = new Quaternion();
        rotation.setFromAxisAngle(x, y, z, angle / 180.0F * PI);
        Quaternion.mul(rotation, this.end, this.end);
        this.start.set(this.end);
        this.smooth.set(this.end);
        return this;
    }

    public SmoothOrientation rotateInstantX(float angle) {
        return this.rotateInstant(angle, 1F, 0F, 0F);
    }

    public SmoothOrientation rotateInstantY(float angle) {
        return this.rotateInstant(angle, 0F, 1F, 0F);
    }

    public SmoothOrientation rotateInstantZ(float angle) {
        return this.rotateInstant(angle, 0F, 0F, 1F);
    }

    /** Rotates the orientation based on the local space, not the global space. */
    public SmoothOrientation localRotate(float angle, float x, float y, float z) {
        Quaternion rotation = new Quaternion();
        rotation.setFromAxisAngle(x, y, z, angle / 180.0F * PI);
        Quaternion.mul(this.end, rotation, this.end);
        this.updateSmooth();
        return this;
    }

    public SmoothOrientation localRotateX(float angle) {
        return this.localRotate(angle, 1F, 0F, 0F);
    }

    public SmoothOrientation localRotateY(float angle) {
        return this.localRotate(angle, 0F, 1F, 0F);
    }

    public SmoothOrientation localRotateZ(float angle) {
        return this.localRotate(angle, 0F, 0F, 1F);
    }

    public SmoothOrientation orientZero() {
        this.start.set(this.smooth);
        this.end.setIdentity();
        this.progress = 0F;
        this.updateSmooth();
        return this;
    }

    public SmoothOrientation identity() {
        this.start.setIdentity();
        this.end.setIdentity();
        this.smooth.setIdentity();
        this.progress = 1.0F;
        return this;
    }

    public SmoothOrientation finish() {
        this.smooth.set(this.end);
        this.start.set(this.end);
        this.progress = 1.0F;
        this.updateSmooth();
        return this;
    }

    public SmoothOrientation orient(EnumAxis axis, float angle) {
        if (axis == EnumAxis.X) this.orientX(angle);
        else if (axis == EnumAxis.Y) this.orientY(angle);
        else if (axis == EnumAxis.Z) this.orientZ(angle);
        return this;
    }

    public SmoothOrientation rotate(EnumAxis axis, float angle) {
        if (axis == EnumAxis.X) this.rotateX(angle);
        else if (axis == EnumAxis.Y) this.rotateY(angle);
        else if (axis == EnumAxis.Z) this.rotateZ(angle);
        return this;
    }

    public SmoothOrientation localRotate(EnumAxis axis, float angle) {
        if (axis == EnumAxis.X) this.localRotateX(angle);
        else if (axis == EnumAxis.Y) this.localRotateY(angle);
        else if (axis == EnumAxis.Z) this.localRotateZ(angle);
        return this;
    }

    public void update(float ticksPerFrame) {
        this.progress += ticksPerFrame * this.smoothness;
        this.progress = Math.min(this.progress, 1.0F);
        this.updateSmooth();
    }

    public void updateSmooth() {
        this.smooth.set(
                this.start.x + (this.end.x - this.start.x) * this.progress,
                this.start.y + (this.end.y - this.start.y) * this.progress,
                this.start.z + (this.end.z - this.start.z) * this.progress,
                this.start.w + (this.end.w - this.start.w) * this.progress);
        this.smooth.normalise();
    }
}
