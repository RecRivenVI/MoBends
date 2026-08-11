package goblinbob.mobends.neoforge.platform;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import goblinbob.mobends.api.rendering.DrawMode;
import goblinbob.mobends.api.rendering.IBufferBuilder;
import goblinbob.mobends.api.rendering.ITesselator;
import goblinbob.mobends.api.rendering.VertexFormatType;

/** Compatibility builder for the legacy mesh API on Minecraft 26.2. */
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
}
