package XiGyoku.furyborn.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FuryBornSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "furyborn");

    public static final RegistryObject<SoundEvent> SUPERCOMPUTER_LOADING = registerSoundEvent("t_supercomputer_loading");

    public static final RegistryObject<SoundEvent> ROBYTE_BGM = registerSoundEvent("robyte_bgm");

    public static final RegistryObject<SoundEvent> ROBYTE_TELEPORT = registerSoundEvent("robyte_teleport");

    public static final RegistryObject<SoundEvent> ROBYTE_GETUP = registerSoundEvent("robyte_getup");

    public static final RegistryObject<SoundEvent> ROBYTE_BEAMSTART = registerSoundEvent("robyte_beamstart");

    public static final RegistryObject<SoundEvent> ROBYTE_BEAMING = registerSoundEvent("robyte_beaming");

    public static final RegistryObject<SoundEvent> ROBYTE_BEAMEND = registerSoundEvent("robyte_beamend");
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("furyborn", name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}