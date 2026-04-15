package XiGyoku.furyborn.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class ColorShiftingVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float r, g, b, a;

    public ColorShiftingVertexConsumer(VertexConsumer delegate, float r, float g, float b, float a) {
        this.delegate = delegate;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color((int) (red * this.r), (int) (green * this.g), (int) (blue * this.b), (int) (alpha * this.a));
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        delegate.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        delegate.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
        delegate.defaultColor((int) (red * this.r), (int) (green * this.g), (int) (blue * this.b), (int) (alpha * this.a));
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }
}