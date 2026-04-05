package XiGyoku.furyborn.item;

import XiGyoku.furyborn.client.item.RenderBusterThrower;
import XiGyoku.furyborn.entity.FuryBornEntityTypes;
import XiGyoku.furyborn.entity.RobyteLaserEntity;
import XiGyoku.furyborn.sound.FuryBornSounds;
import java.util.List;
import java.util.function.Consumer;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.util.StorageUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBusterThrower extends Item implements IItemHUDProvider {

    public static final FloatingLong MAX_ENERGY = FloatingLong.createConst(2500000);
    public static final FloatingLong ENERGY_COST = FloatingLong.createConst(125000);
    public static final FloatingLong CHARGE_RATE = FloatingLong.createConst(50000);

    public ItemBusterThrower(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(
                () -> CHARGE_RATE,
                () -> MAX_ENERGY,
                BasicEnergyContainer.manualOnly,
                BasicEnergyContainer.alwaysTrue
        ));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getEnergyBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public void addHUDStrings(List<Component> list, Player player, ItemStack stack, EquipmentSlot slotType) {
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCreative()) {
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
            if (energyContainer == null || energyContainer.extract(ENERGY_COST, Action.SIMULATE, AutomationType.MANUAL).smallerThan(ENERGY_COST)) {
                return InteractionResultHolder.fail(stack);
            }
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingDuration) {
        if (!(entity instanceof Player player)) return;

        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (!player.isCreative()) {
            if (energyContainer == null || energyContainer.extract(ENERGY_COST, Action.SIMULATE, AutomationType.MANUAL).smallerThan(ENERGY_COST)) {
                entity.releaseUsingItem();
                return;
            }
        }

        int useTime = this.getUseDuration(stack) - remainingDuration;
        int chargeTime = 80;
        int cooldownTime = 40;
        int totalCycle = chargeTime + cooldownTime;

        int cycleTime = useTime % totalCycle;
        if (!level.isClientSide && useTime > 0 && cycleTime == 1) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), FuryBornSounds.ROBYTE_BUSTER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        if (level.isClientSide && useTime > 0 && cycleTime > 0 && cycleTime <= chargeTime) {
            double radius = 10.0D;
            double beamLength = 100.0D;
            double progress = (double) cycleTime / chargeTime;
            double currentZDist = progress * beamLength;

            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 up = new Vec3(0, 1, 0);
            if (Math.abs(look.y) > 0.99) {
                up = new Vec3(1, 0, 0);
            }
            Vec3 right = look.cross(up).normalize();
            Vec3 beamUp = right.cross(look).normalize();
            int particlesPerTick = 6;

            for (int i = 0; i < particlesPerTick; i++) {
                double angle = (useTime * 0.5) + (i * Math.PI * 2 / particlesPerTick);
                Vec3 offset = right.scale(Math.cos(angle) * radius).add(beamUp.scale(Math.sin(angle) * radius));
                Vec3 particlePos = start.add(look.scale(currentZDist)).add(offset);

                level.addParticle(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 0.0D, 0.0D, 0.0D);
            }
        }
        if (useTime > 0 && cycleTime == chargeTime) {
            if (!level.isClientSide) {
                if (!player.isCreative() && energyContainer != null) {
                    energyContainer.extract(ENERGY_COST, Action.EXECUTE, AutomationType.MANUAL);
                }

                RobyteLaserEntity laser = new RobyteLaserEntity(FuryBornEntityTypes.ROBYTE_LASER.get(), level);
                laser.setPos(player.getX(), player.getEyeY() - 0.2D, player.getZ());
                laser.setXRot(player.getXRot());
                laser.setYRot(player.getYRot());

                laser.setRadius(10.0F);
                laser.setMaxLife(400);
                laser.setOwner(player);

                level.addFreshEntity(laser);
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return RenderBusterThrower.RENDERER;
            }
        });
    }
}