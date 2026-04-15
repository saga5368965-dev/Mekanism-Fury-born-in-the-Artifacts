package XiGyoku.furyborn.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HaloProjectorBlockEntity extends BaseEntityBlock {
    protected HaloProjectorBlockEntity(Properties pBlockEntity) {
        super(pBlockEntity);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return null;
    }
}
