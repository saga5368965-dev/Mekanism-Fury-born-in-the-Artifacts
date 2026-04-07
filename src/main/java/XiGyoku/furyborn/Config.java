package XiGyoku.furyborn;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Furyborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    static {
        BUILDER.push("Halo of Exolumen Settings");
    }

    private static final ForgeConfigSpec.DoubleValue HALO_OFFSET_X = BUILDER
            .comment("X offset from player for Halo of Exolumen")
            .defineInRange("haloOffsetX", 1.0, -2.0, 2.0);

    private static final ForgeConfigSpec.DoubleValue HALO_OFFSET_Y = BUILDER
            .comment("Y offset from player for Halo of Exolumen")
            .defineInRange("haloOffsetY", -1.0, -2.0, 2.0);

    private static final ForgeConfigSpec.DoubleValue HALO_OFFSET_Z = BUILDER
            .comment("Z offset from player for Halo of Exolumen")
            .defineInRange("haloOffsetZ", 1.0, -2.0, 2.0);

    private static final ForgeConfigSpec.DoubleValue HALO_ROTATION_X = BUILDER
            .comment("X rotation for Halo of Exolumen (Degrees)")
            .defineInRange("haloRotationX", 180.0, -360.0, 360.0);

    private static final ForgeConfigSpec.DoubleValue HALO_ROTATION_Y = BUILDER
            .comment("Y rotation for Halo of Exolumen (Degrees)")
            .defineInRange("haloRotationY", 0.0, -360.0, 360.0);

    private static final ForgeConfigSpec.DoubleValue HALO_ROTATION_Z = BUILDER
            .comment("Z rotation for Halo of Exolumen (Degrees)")
            .defineInRange("haloRotationZ", 0.0, -360.0, 360.0);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float haloOffsetX;
    public static float haloOffsetY;
    public static float haloOffsetZ;
    public static float haloRotX;
    public static float haloRotY;
    public static float haloRotZ;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        haloOffsetX = HALO_OFFSET_X.get().floatValue();
        haloOffsetY = HALO_OFFSET_Y.get().floatValue();
        haloOffsetZ = HALO_OFFSET_Z.get().floatValue();
        haloRotX = HALO_ROTATION_X.get().floatValue();
        haloRotY = HALO_ROTATION_Y.get().floatValue();
        haloRotZ = HALO_ROTATION_Z.get().floatValue();
    }
}