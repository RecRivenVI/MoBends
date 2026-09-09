package io.github.recrivenvi.mobends.lib.math.physics;

public interface ICollider {

    RayHitInfo intersect(Ray ray);
}
