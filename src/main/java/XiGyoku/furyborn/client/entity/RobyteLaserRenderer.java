package XiGyoku.furyborn.client.entity;

import XiGyoku.furyborn.entity.RobyteLaserEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RobyteLaserRenderer extends EntityRenderer<RobyteLaserEntity> {
    protected RobyteLaserRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(RobyteLaserEntity p_114482_) {
        return null;
    }
}
