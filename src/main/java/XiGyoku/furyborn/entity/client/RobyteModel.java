package XiGyoku.furyborn.entity.client;

import XiGyoku.furyborn.Furyborn;
import XiGyoku.furyborn.entity.RobyteEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RobyteModel extends GeoModel<RobyteEntity> {

    @Override
    public ResourceLocation getModelResource(RobyteEntity robyteEntity) {
        return new ResourceLocation(Furyborn.MODID, "geo/robyte.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RobyteEntity robyteEntity) {
        return new ResourceLocation(Furyborn.MODID, "textures/entity/robyte_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RobyteEntity robyteEntity) {
        return new ResourceLocation(Furyborn.MODID, "animations/robyte.animation.json");
    }
}
