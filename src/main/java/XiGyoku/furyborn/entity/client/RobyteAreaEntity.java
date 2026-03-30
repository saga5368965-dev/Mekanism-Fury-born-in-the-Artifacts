package XiGyoku.furyborn.entity.client;

import XiGyoku.furyborn.entity.RobyteEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RobyteAreaEntity extends Entity {
    private RobyteEntity robyte = null;

    public RobyteAreaEntity(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    public void setRobyte(RobyteEntity robyte) {
        this.robyte = robyte;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {

            if (this.robyte != null) {
                if (this.robyte.isRemoved() || !this.robyte.isAlive()) {
                    this.discard();
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

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
