package io.github.recrivenvi.mobends.lib.math.physics;

import io.github.recrivenvi.mobends.lib.math.vector.IVec3fRead;

public interface IAABBox {

    IVec3fRead getMin();

    IVec3fRead getMax();
}
