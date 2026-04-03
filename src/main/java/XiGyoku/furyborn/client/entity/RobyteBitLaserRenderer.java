package XiGyoku.furyborn.client.entity;

import XiGyoku.furyborn.entity.RobyteBitLaserEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RobyteBitLaserRenderer extends EntityRenderer<RobyteBitLaserEntity> {
    protected RobyteBitLaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(RobyteBitLaserEntity p_114482_) {
        return null;
    }
}
