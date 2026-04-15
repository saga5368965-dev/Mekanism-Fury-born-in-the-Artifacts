package XiGyoku.furyborn.event;

import XiGyoku.furyborn.sound.FuryBornSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "furyborn")
public class PlayerDriveshiftEvent {

    private static final UUID DRIVESHIFT_SPEED_ID = UUID.fromString("1f2d3e4c-5b6a-7f8e-9d0c-1b2a3f4e5d6c");
    private static final UUID DRIVESHIFT_DAMAGE_ID = UUID.fromString("2f3d4e5c-6b7a-8f9e-0d1c-2b3a4f5e6d7c");
    private static final UUID DRIVESHIFT_ARMOR_ID = UUID.fromString("3f4d5e6c-7b8a-9f0e-1d2c-3b4a5f6e7d8c");
    private static final String DRIVESHIFT_FADE_PROGRESS = "DriveshiftFadeProgress";
    private static final float FADE_IN_SPEED = 0.1f;
    private static final float FADE_OUT_SPEED = 0.05f;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        boolean isActive = player.getPersistentData().getBoolean("ExolumenAfterImage");
        float progress = player.getPersistentData().contains(DRIVESHIFT_FADE_PROGRESS) ? player.getPersistentData().getFloat(DRIVESHIFT_FADE_PROGRESS) : 0.0f;

        if (isActive) {
            if (progress < 1.0f) {
                progress = Math.min(1.0f, progress + FADE_IN_SPEED);
                player.getPersistentData().putFloat(DRIVESHIFT_FADE_PROGRESS, progress);
            }
        } else {
            if (progress > 0.0f) {
                progress = Math.max(0.0f, progress - FADE_OUT_SPEED);
                player.getPersistentData().putFloat(DRIVESHIFT_FADE_PROGRESS, progress);
            }
        }

        if (player.getPersistentData().contains("DriveshiftGreenTicks")) {
            int greenTicks = player.getPersistentData().getInt("DriveshiftGreenTicks");
            if (greenTicks > 0) {
                player.getPersistentData().putInt("DriveshiftGreenTicks", greenTicks - 1);
            }
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance damage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);

        if (isActive) {
            player.resetAttackStrengthTicker();

            if (speed != null && speed.getModifier(DRIVESHIFT_SPEED_ID) == null) {
                speed.addTransientModifier(new AttributeModifier(DRIVESHIFT_SPEED_ID, "Driveshift Speed", 2.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            if (damage != null && damage.getModifier(DRIVESHIFT_DAMAGE_ID) == null) {
                damage.addTransientModifier(new AttributeModifier(DRIVESHIFT_DAMAGE_ID, "Driveshift Damage", 2.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            if (armor != null && armor.getModifier(DRIVESHIFT_ARMOR_ID) == null) {
                armor.addTransientModifier(new AttributeModifier(DRIVESHIFT_ARMOR_ID, "Driveshift Armor", 2.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        } else {
            if (speed != null && speed.getModifier(DRIVESHIFT_SPEED_ID) != null) {
                speed.removeModifier(DRIVESHIFT_SPEED_ID);
            }
            if (damage != null && damage.getModifier(DRIVESHIFT_DAMAGE_ID) != null) {
                damage.removeModifier(DRIVESHIFT_DAMAGE_ID);
            }
            if (armor != null && armor.getModifier(DRIVESHIFT_ARMOR_ID) != null) {
                armor.removeModifier(DRIVESHIFT_ARMOR_ID);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getPersistentData().getBoolean("ExolumenAfterImage")) {
                if (player.level().random.nextFloat() < 0.9F) {
                    event.setCanceled(true);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), FuryBornSounds.ROBYTE_TELEPORT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.getPersistentData().getBoolean("ExolumenAfterImage")) {
            if (player.getPersistentData().getBoolean("DriveshiftAttacking")) return;
            player.getPersistentData().putBoolean("DriveshiftAttacking", true);

            Entity target = event.getTarget();
            for (int i = 0; i < 2; i++) {
                player.attack(target);
            }

            player.getPersistentData().putBoolean("DriveshiftAttacking", false);
        }
    }

    @SubscribeEvent
    public static void onProjectileJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Projectile projectile && !event.getLevel().isClientSide()) {
            if (projectile.getOwner() instanceof Player player) {
                if (player.getPersistentData().getBoolean("ExolumenAfterImage")) {
                    if (projectile.getPersistentData().getBoolean("DriveshiftCloned")) return;
                    projectile.getPersistentData().putBoolean("DriveshiftCloned", true);

                    for (int i = 0; i < 2; i++) {
                        Entity clone = projectile.getType().create(event.getLevel());
                        if (clone instanceof Projectile clonedProj) {
                            clonedProj.copyPosition(projectile);
                            clonedProj.setOwner(player);
                            Vec3 delta = projectile.getDeltaMovement();
                            double spreadX = (player.level().random.nextDouble() - 0.5) * 0.2;
                            double spreadY = (player.level().random.nextDouble() - 0.5) * 0.2;
                            double spreadZ = (player.level().random.nextDouble() - 0.5) * 0.2;
                            clonedProj.setDeltaMovement(delta.add(spreadX, spreadY, spreadZ));
                            clonedProj.getPersistentData().putBoolean("DriveshiftCloned", true);
                            event.getLevel().addFreshEntity(clonedProj);
                        }
                    }
                }
            }
        }
    }
}