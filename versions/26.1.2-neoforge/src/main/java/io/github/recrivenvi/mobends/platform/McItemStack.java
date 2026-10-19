package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.entity.IItemStack;
import io.github.recrivenvi.mobends.api.resource.IResourcePath;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;

//
// Shared implementation of IItemStack. Wraps Minecraft's ItemStack with a platform-agnostic
// interface.
//
public class McItemStack implements IItemStack {
    private final ItemStack itemStack;

    public McItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    @Override
    public int getCount() {
        return itemStack.getCount();
    }

    @Override
    @Nullable
    public IResourcePath getItemId() {
        Identifier key = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        return new McResourcePath(key);
    }

    @Override
    public boolean isTool() {
        return itemStack.is(ItemTags.SWORDS)
                || itemStack.is(ItemTags.AXES)
                || itemStack.is(ItemTags.PICKAXES)
                || itemStack.is(ItemTags.SHOVELS)
                || itemStack.is(ItemTags.HOES)
                || itemStack.getItem() instanceof TridentItem;
    }

    @Override
    public boolean isBow() {
        return itemStack.getItem() instanceof BowItem;
    }

    @Override
    public boolean isShield() {
        return itemStack.getItem() instanceof ShieldItem;
    }

    @Override
    public boolean isFood() {
        return VersionAdapter.Holder.get().isItemEdible(itemStack);
    }

    @Override
    public Object getNative() {
        return itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
