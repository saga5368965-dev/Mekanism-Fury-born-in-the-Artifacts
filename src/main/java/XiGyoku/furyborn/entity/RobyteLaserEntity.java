package XiGyoku.furyborn.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import XiGyoku.furyborn.sound.FuryBornSounds;

import javax.annotation.Nullable;
import java.util.UUID;

public class RobyteLaserEntity extends Entity {
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(RobyteLaserEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MAX_LIFE = SynchedEntityData.defineId(RobyteLaserEntity.class, EntityDataSerializers.INT);

    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;

    public RobyteLaserEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RADIUS, 0.2F);
        this.entityData.define(MAX_LIFE, 40);
    }

    public void setRadius(float r) { this.entityData.set(RADIUS, r); }

    public float getRadius() { return this.entityData.get(RADIUS); }

    public void setMaxLife(int ticks) { this.entityData.set(MAX_LIFE, ticks); }

    public int getMaxLife() { return this.entityData.get(MAX_LIFE); }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) return this.cachedOwner;
        if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            this.cachedOwner = serverLevel.getEntity(this.ownerUUID);
            return this.cachedOwner;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.tickCount == 1) {
                this.playSound(FuryBornSounds.ROBYTE_BEAMING.get(), 0.25F, 1.0F);
            }
            if (this.tickCount >= getMaxLife()) {
                this.discard();
                return;
            }

            float growDuration = 5.0F;
            float radiusScale = Mth.clamp((float) this.tickCount / growDuration, 0.0F, 1.0F);
            float currentHitRadius = this.getRadius() * radiusScale;

            Vec3 start = this.position();
            Vec3 dir = this.getLookAngle();
            double length = 100.0D;
            Vec3 end = start.add(dir.scale(length));

            AABB searchBox = new AABB(start, end).inflate(currentHitRadius + 1.0D);
            Entity owner = this.getOwner();

            for (Entity target : this.level().getEntities(this, searchBox, e -> e instanceof LivingEntity && !e.isSpectator())) {

                if (target.is(owner) || (this.ownerUUID != null && target.getUUID().equals(this.ownerUUID))) {
                    continue;
                }

                if (owner instanceof RobyteBitLaserEntity bitOwner) {
                    if (target.is(bitOwner.getOwner()) || (bitOwner.getOwnerUUID() != null && target.getUUID().equals(bitOwner.getOwnerUUID()))) {
                        continue;
                    }
                }

                AABB targetBox = target.getBoundingBox().inflate(currentHitRadius);
                if (targetBox.clip(start, end).isPresent()) {
                    DamageSource source = this.level().damageSources().indirectMagic(this, owner != null ? owner : this);
                    target.invulnerableTime = 0;
                    target.hurt(source, 0.5F);
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("Radius")) setRadius(nbt.getFloat("Radius"));
        if (nbt.contains("MaxLife")) setMaxLife(nbt.getInt("MaxLife"));
        if (nbt.hasUUID("Owner")) this.ownerUUID = nbt.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putFloat("Radius", getRadius());
        nbt.putInt("MaxLife", getMaxLife());
        if (this.ownerUUID != null) nbt.putUUID("Owner", this.ownerUUID);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}