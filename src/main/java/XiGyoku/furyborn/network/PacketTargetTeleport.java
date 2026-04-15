package XiGyoku.furyborn.network;

import XiGyoku.furyborn.sound.FuryBornSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketTargetTeleport {
    public PacketTargetTeleport() {}

    public PacketTargetTeleport(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.getPersistentData().getBoolean("ExolumenAfterImage")) {
                AABB box = player.getBoundingBox().inflate(20.0D);
                List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive() && e instanceof Enemy);

                LivingEntity closest = null;
                double minDist = Double.MAX_VALUE;
                for (LivingEntity e : entities) {
                    double dist = player.distanceToSqr(e);
                    if (dist < minDist) {
                        minDist = dist;
                        closest = e;
                    }
                }

                if (closest != null) {
                    float targetYawRad = closest.getYRot() * ((float) Math.PI / 180F);
                    double fwdX = -Mth.sin(targetYawRad);
                    double fwdZ = Mth.cos(targetYawRad);

                    Vec3 back = closest.position().subtract(fwdX * 1.5D, 0, fwdZ * 1.5D);

                    player.connection.teleport(back.x, closest.getY() + 0.2D, back.z, closest.getYRot(), closest.getXRot());
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), FuryBornSounds.ROBYTE_TELEPORT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}