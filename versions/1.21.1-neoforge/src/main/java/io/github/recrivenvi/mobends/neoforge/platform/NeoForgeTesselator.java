// ? if target_1_21_1_neoforge {
package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.recrivenvi.mobends.api.rendering.DrawMode;
import io.github.recrivenvi.mobends.api.rendering.IBufferBuilder;
import io.github.recrivenvi.mobends.api.rendering.ITesselator;
import io.github.recrivenvi.mobends.api.rendering.VertexFormatType;

// NeoForge implementation of ITesselator. Direct passthrough to MC 1.21.1's Tesselator API.
public class NeoForgeTesselator implements ITesselator {
    private final Tesselator tesselator;

    public NeoForgeTesselator() {
        this.tesselator = Tesselator.getInstance();
    }

    @Override
    public IBufferBuilder begin(DrawMode mode, VertexFormatType format) {
        VertexFormat.Mode mcMode = mapDrawMode(mode);
        VertexFormat mcFormat = mapVertexFormat(format);
        BufferBuilder builder = tesselator.begin(mcMode, mcFormat);
        return new NeoForgeBufferBuilder(builder);
    }

    @Override
    public void endAndDraw(IBufferBuilder builder) {
        BufferBuilder nativeBuilder = (BufferBuilder) builder.getNative();
        BufferUploader.drawWithShader(nativeBuilder.buildOrThrow());
    }

    @Override
    public Object getNative() {
        return tesselator;
    }

    private VertexFormat.Mode mapDrawMode(DrawMode mode) {
        return switch (mode) {
            case QUADS -> VertexFormat.Mode.QUADS;
            case TRIANGLES -> VertexFormat.Mode.TRIANGLES;
            case TRIANGLE_STRIP -> VertexFormat.Mode.TRIANGLE_STRIP;
            case TRIANGLE_FAN -> VertexFormat.Mode.TRIANGLE_FAN;
            case LINES -> VertexFormat.Mode.LINES;
            case LINE_STRIP -> VertexFormat.Mode.LINE_STRIP;
            case DEBUG_LINES -> VertexFormat.Mode.DEBUG_LINES;
            case DEBUG_LINE_STRIP -> VertexFormat.Mode.DEBUG_LINE_STRIP;
        };
    }

    private VertexFormat mapVertexFormat(VertexFormatType format) {
        return switch (format) {
            case POSITION -> DefaultVertexFormat.POSITION;
            case POSITION_COLOR -> DefaultVertexFormat.POSITION_COLOR;
            case POSITION_TEX -> DefaultVertexFormat.POSITION_TEX;
            case POSITION_TEX_COLOR -> DefaultVertexFormat.POSITION_TEX_COLOR;
            case POSITION_TEX_COLOR_NORMAL -> DefaultVertexFormat.BLOCK;
        };
    }
}
// ?} else if target_26_1_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.recrivenvi.mobends.api.rendering.DrawMode;
import io.github.recrivenvi.mobends.api.rendering.IBufferBuilder;
import io.github.recrivenvi.mobends.api.rendering.ITesselator;
import io.github.recrivenvi.mobends.api.rendering.VertexFormatType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

// NeoForge implementation of ITesselator. Direct passthrough to MC 1.21.1's Tesselator API.
public class NeoForgeTesselator implements ITesselator {
    private final Tesselator tesselator;
    private VertexFormat.Mode activeMode = VertexFormat.Mode.QUADS;

    public NeoForgeTesselator() {
        this.tesselator = Tesselator.getInstance();
    }

    @Override
    public IBufferBuilder begin(DrawMode mode, VertexFormatType format) {
        VertexFormat.Mode mcMode = mapDrawMode(mode);
        this.activeMode = mcMode;
        VertexFormat mcFormat = mapVertexFormat(format);
        BufferBuilder builder = tesselator.begin(mcMode, mcFormat);
        return new NeoForgeBufferBuilder(builder);
    }

    @Override
    public void endAndDraw(IBufferBuilder builder) {
        BufferBuilder nativeBuilder = (BufferBuilder) builder.getNative();
        RenderType renderType =
                switch (this.activeMode) {
                    case LINES, DEBUG_LINES, DEBUG_LINE_STRIP -> RenderTypes.lines();
                    case TRIANGLE_FAN -> RenderTypes.debugTriangleFan();
                    case POINTS -> RenderTypes.debugPoint();
                    default -> RenderTypes.debugQuads();
                };
        try (MeshData mesh = nativeBuilder.buildOrThrow()) {
            renderType.draw(mesh);
        }
    }

    @Override
    public Object getNative() {
        return tesselator;
    }

    private VertexFormat.Mode mapDrawMode(DrawMode mode) {
        return switch (mode) {
            case QUADS -> VertexFormat.Mode.QUADS;
            case TRIANGLES -> VertexFormat.Mode.TRIANGLES;
            case TRIANGLE_STRIP -> VertexFormat.Mode.TRIANGLE_STRIP;
            case TRIANGLE_FAN -> VertexFormat.Mode.TRIANGLE_FAN;
            case LINES -> VertexFormat.Mode.LINES;
            case LINE_STRIP -> VertexFormat.Mode.DEBUG_LINE_STRIP;
            case DEBUG_LINES -> VertexFormat.Mode.DEBUG_LINES;
            case DEBUG_LINE_STRIP -> VertexFormat.Mode.DEBUG_LINE_STRIP;
        };
    }

    private VertexFormat mapVertexFormat(VertexFormatType format) {
        return switch (format) {
            case POSITION -> DefaultVertexFormat.POSITION;
            case POSITION_COLOR -> DefaultVertexFormat.POSITION_COLOR;
            case POSITION_TEX -> DefaultVertexFormat.POSITION_TEX;
            case POSITION_TEX_COLOR -> DefaultVertexFormat.POSITION_TEX_COLOR;
            case POSITION_TEX_COLOR_NORMAL -> DefaultVertexFormat.BLOCK;
        };
    }
}*/
// ?} else if target_26_2_neoforge {
/*package io.github.recrivenvi.mobends.neoforge.platform;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.recrivenvi.mobends.api.rendering.DrawMode;
import io.github.recrivenvi.mobends.api.rendering.IBufferBuilder;
import io.github.recrivenvi.mobends.api.rendering.ITesselator;
import io.github.recrivenvi.mobends.api.rendering.VertexFormatType;

// Compatibility builder for the legacy mesh API on Minecraft 26.2.
public class NeoForgeTesselator implements ITesselator {
    private ByteBufferBuilder storage;

    @Override
    public IBufferBuilder begin(DrawMode mode, VertexFormatType format) {
        this.storage = new ByteBufferBuilder(786432);
        BufferBuilder builder =
                new BufferBuilder(storage, mapDrawMode(mode), mapVertexFormat(format));
        return new NeoForgeBufferBuilder(builder);
    }

    @Override
    public void endAndDraw(IBufferBuilder builder) {
        BufferBuilder nativeBuilder = (BufferBuilder) builder.getNative();
        try (MeshData ignored = nativeBuilder.buildOrThrow()) {
            // Independent immediate draws no longer exist in 26.2. Production
            // model geometry is submitted through the prepared-model callback.
        } finally {
            if (storage != null) {
                storage.close();
                storage = null;
            }
        }
    }

    @Override
    public Object getNative() {
        return storage;
    }

    private PrimitiveTopology mapDrawMode(DrawMode mode) {
        return switch (mode) {
            case QUADS -> PrimitiveTopology.QUADS;
            case TRIANGLES -> PrimitiveTopology.TRIANGLES;
            case TRIANGLE_STRIP -> PrimitiveTopology.TRIANGLE_STRIP;
            case TRIANGLE_FAN -> PrimitiveTopology.TRIANGLE_FAN;
            case LINES -> PrimitiveTopology.LINES;
            case LINE_STRIP -> PrimitiveTopology.DEBUG_LINE_STRIP;
            case DEBUG_LINES -> PrimitiveTopology.DEBUG_LINES;
            case DEBUG_LINE_STRIP -> PrimitiveTopology.DEBUG_LINE_STRIP;
        };
    }

    private VertexFormat mapVertexFormat(VertexFormatType format) {
        return switch (format) {
            case POSITION -> DefaultVertexFormat.POSITION;
            case POSITION_COLOR -> DefaultVertexFormat.POSITION_COLOR;
            case POSITION_TEX -> DefaultVertexFormat.POSITION_TEX;
            case POSITION_TEX_COLOR -> DefaultVertexFormat.POSITION_TEX_COLOR;
            case POSITION_TEX_COLOR_NORMAL -> DefaultVertexFormat.BLOCK;
        };
    }
}*/
// ?}
