package XiGyoku.furyborn.entity;

import XiGyoku.furyborn.Furyborn;
import XiGyoku.furyborn.entity.client.RobyteAreaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FuryBornEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Furyborn.MODID);

    public static final RegistryObject<EntityType<RobyteEntity>> ROBYTE =
            ENTITY_TYPES.register("robyte",
                    () -> EntityType.Builder.of(RobyteEntity::new, MobCategory.MONSTER)
                            .sized(0.8f,3.0f)
                            .build(new ResourceLocation(Furyborn.MODID, "robyte").toString()));

    public static final RegistryObject<EntityType<RobyteAreaEntity>> ROBYTE_AREA =
            ENTITY_TYPES.register("robyte_area",
                    () -> EntityType.Builder.of(RobyteAreaEntity::new, MobCategory.MISC)
                            .sized(0.8f,3.0f)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Furyborn.MODID, "robyte_area").toString()));

    public static void register(IEventBus eventBus) {
            ENTITY_TYPES.register(eventBus);
    }
}
