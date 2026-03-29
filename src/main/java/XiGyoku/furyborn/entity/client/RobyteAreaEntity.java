package XiGyoku.furyborn.entity.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RobyteAreaEntity extends Entity {
    public RobyteAreaEntity(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.noCulling = true;
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
