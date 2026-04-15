package XiGyoku.furyborn.network;

import XiGyoku.furyborn.sound.FuryBornSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleAfterImage {

    public PacketToggleAfterImage() {}

    public PacketToggleAfterImage(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                boolean currentState = player.getPersistentData().getBoolean("ExolumenAfterImage");
                boolean newState = !currentState;
                player.getPersistentData().putBoolean("ExolumenAfterImage", newState);
                Level level = player.level();
                level.playSound(null, player.getX(), player.getY(), player.getZ(), FuryBornSounds.ROBYTE_TELEPORT.get(), SoundSource.PLAYERS, 1.0F, 1.0F
                );
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}