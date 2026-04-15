package XiGyoku.furyborn.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class ColorShiftingBufferSource implements MultiBufferSource {
    private final MultiBufferSource delegate;
    private final float r, g, b, a;

    public ColorShiftingBufferSource(MultiBufferSource delegate, float r, float g, float b, float a) {
        this.delegate = delegate;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return new ColorShiftingVertexConsumer(delegate.getBuffer(renderType), r, g, b, a);
    }
}