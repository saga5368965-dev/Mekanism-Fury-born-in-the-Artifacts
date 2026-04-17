package XiGyoku.furyborn.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncAfterImage {
    private final boolean isActive;

    public PacketSyncAfterImage(boolean isActive) {
        this.isActive = isActive;
    }

    public PacketSyncAfterImage(FriendlyByteBuf buf) {
        this.isActive = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isActive);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getPersistentData().putBoolean("ExolumenAfterImage", this.isActive);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}