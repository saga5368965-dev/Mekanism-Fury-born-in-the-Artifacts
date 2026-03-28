package XiGyoku.furyborn.entity.client;

import XiGyoku.furyborn.Furyborn;
import XiGyoku.furyborn.entity.RobyteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RobyteRenderer extends GeoEntityRenderer<RobyteEntity> {
    public RobyteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RobyteModel());
        this.shadowRadius = 0.5f;
        this.withScale(2.0f);
    }

    @Override
    protected float getDeathMaxRotation(RobyteEntity entity) {
        return 0.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(RobyteEntity entity) {
        return new ResourceLocation(Furyborn.MODID, "textures/entity/robyte_texture.png");
    }
}

