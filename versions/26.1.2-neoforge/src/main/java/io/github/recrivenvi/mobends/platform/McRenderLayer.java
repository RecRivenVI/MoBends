// ? if target_26_1_2_neoforge || target_26_2_fabric || target_26_2_neoforge {
package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;

//
// Shared implementation of IRenderLayer. Wraps Minecraft's RenderType with a platform-agnostic
// interface.
//
public class McRenderLayer implements IRenderLayer {
    private final RenderType renderType;

    public McRenderLayer(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public String name() {
        return renderType.toString();
    }

    @Override
    public Object getNative() {
        return renderType;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}
// ?} else if target_1_20_1_forge || target_1_21_1_neoforge {
/*package io.github.recrivenvi.mobends.platform;

import io.github.recrivenvi.mobends.api.rendering.IRenderLayer;
import net.minecraft.client.renderer.RenderType;

//
// Shared implementation of IRenderLayer. Wraps Minecraft's RenderType with a platform-agnostic
// interface.
//
public class McRenderLayer implements IRenderLayer {
    private final RenderType renderType;

    public McRenderLayer(RenderType renderType) {
        this.renderType = renderType;
    }

    @Override
    public String name() {
        return renderType.toString();
    }

    @Override
    public Object getNative() {
        return renderType;
    }

    public RenderType getRenderType() {
        return renderType;
    }
}*/
// ?}
