package io.github.recrivenvi.mobends.lib.math.physics;

import io.github.recrivenvi.mobends.lib.math.matrix.IMat4x4d;
import io.github.recrivenvi.mobends.lib.math.vector.IVec3fRead;

public interface IOBBox {

    IVec3fRead getMin();

    IVec3fRead getMax();

    IMat4x4d getTransform();
}
