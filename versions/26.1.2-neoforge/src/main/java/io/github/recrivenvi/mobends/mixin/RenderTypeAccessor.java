package io.github.recrivenvi.mobends.mixin;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// 创建保持原版映射兼容的自定义渲染类型。
@Mixin(RenderType.class)
public interface RenderTypeAccessor {
    @Invoker("create")
    static RenderType mobends$create(String name, RenderSetup setup) {
        throw new AssertionError();
    }
}
