package XiGyoku.furyborn.network;

import XiGyoku.furyborn.sound.FuryBornSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketDirectionalDash {
    private final boolean f, b, l, r, u, d;

    public PacketDirectionalDash(boolean f, boolean b, boolean l, boolean r, boolean u, boolean d) {
        this.f = f;
        this.b = b;
        this.l = l;
        this.r = r;
        this.u = u;
        this.d = d;
    }

    public PacketDirectionalDash(FriendlyByteBuf buf) {
        this.f = buf.readBoolean();
        this.b = buf.readBoolean();
        this.l = buf.readBoolean();
        this.r = buf.readBoolean();
        this.u = buf.readBoolean();
        this.d = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(f);
        buf.writeBoolean(b);
        buf.writeBoolean(l);
        buf.writeBoolean(r);
        buf.writeBoolean(u);
        buf.writeBoolean(d);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.getPersistentData().getBoolean("ExolumenAfterImage")) {
                float yawRad = player.getYRot() * ((float) Math.PI / 180F);

                double fwdX = -Mth.sin(yawRad);
                double fwdZ = Mth.cos(yawRad);
                Vec3 forward = new Vec3(fwdX, 0, fwdZ).normalize();

                double rightX = -Mth.cos(yawRad);
                double rightZ = -Mth.sin(yawRad);
                Vec3 right = new Vec3(rightX, 0, rightZ).normalize();

                Vec3 move = Vec3.ZERO;
                if (this.f) move = move.add(forward);
                if (this.b) move = move.subtract(forward);
                if (this.l) move = move.subtract(right);
                if (this.r) move = move.add(right);
                if (this.u) move = move.add(0, 1, 0);
                if (this.d) move = move.subtract(0, 1, 0);

                if (move.lengthSqr() == 0) move = forward;
                move = move.normalize().scale(10.0);

                Vec3 target = player.position().add(move);

                player.connection.teleport(target.x, target.y + 0.2D, target.z, player.getYRot(), player.getXRot());
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), FuryBornSounds.ROBYTE_TELEPORT.get(), SoundSource.PLAYERS, 0.1F, 1.0F);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}