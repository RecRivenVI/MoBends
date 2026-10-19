package io.github.recrivenvi.mobends.api.entity;

import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import javax.annotation.Nullable;

// Platform-agnostic item stack abstraction.
public interface IItemStack {
    //
    // @return Whether this item stack is empty
    //
    boolean isEmpty();

    //
    // @return The count of items in this stack
    //
    int getCount();

    //
    // @return The registry ID of this item (e.g., "minecraft:diamond_sword")
    //
    @Nullable
    IResourcePath getItemId();

    //
    // @return Whether this item is a tool (sword, pickaxe, axe, etc.)
    //
    boolean isTool();

    //
    // @return Whether this item is a bow
    //
    boolean isBow();

    //
    // @return Whether this item is a shield
    //
    boolean isShield();

    //
    // @return Whether this item is food
    //
    boolean isFood();

    //
    // @return The native platform item stack object
    //
    Object getNative();
}
