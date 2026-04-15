package XiGyoku.furyborn.client.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = "furyborn", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientAfterImageManager {
    public static final Map<UUID, List<Vec3>> PAST_POSITIONS = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            if (player.getPersistentData().getBoolean("ExolumenAfterImage")) {
                List<Vec3> positions = PAST_POSITIONS.computeIfAbsent(player.getUUID(), k -> new ArrayList<>());
                positions.add(0, player.position());
                if (positions.size() > 20) {
                    positions.remove(positions.size() - 1);
                }
            } else {
                PAST_POSITIONS.remove(player.getUUID());
            }
        }
    }
}