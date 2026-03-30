package XiGyoku.furyborn.entity.client;

import XiGyoku.furyborn.entity.RobyteEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class RobyteAreaEntity extends Entity {
    private UUID bossId = null;

    public RobyteAreaEntity(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    public void setRobyte(RobyteEntity robyte) {
        this.bossId = robyte.getUUID();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.bossId != null) {
                Entity boss = serverLevel.getEntity(this.bossId);
                if (boss != null) {
                    if (boss.isRemoved() || !boss.isAlive()) {
                        this.discard();
                    }
                }
            } else {
                if (this.tickCount > 10) {
                    this.discard();
                }
            }
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("BossId")) {
            this.bossId = compoundTag.getUUID("BossId");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.bossId != null) {
            compoundTag.putUUID("BossId", this.bossId);
        }
    }
}
