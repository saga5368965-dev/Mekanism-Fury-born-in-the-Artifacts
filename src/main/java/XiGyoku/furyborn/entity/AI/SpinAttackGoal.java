package XiGyoku.furyborn.entity.AI;

import XiGyoku.furyborn.entity.RobyteEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class SpinAttackGoal extends Goal {
    private final RobyteEntity mob;
    private LivingEntity target;

    public SpinAttackGoal(RobyteEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        return this.target != null && this.target.isAlive()
                && this.mob.attackCooldown <= 0
                && this.mob.phaseTransitionTick == 0
                && this.mob.distanceToSqr(this.target) <= 9.0D
                && !this.mob.hasEnteredFinalPhase();
    }

    @Override
    public void start() {
        this.mob.setAttackTick(1);
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        int currentTick = this.mob.getAttackTick();

        if (currentTick <= this.mob.START_DUR) {
            this.mob.getNavigation().stop();
        } else if (currentTick <= this.mob.START_DUR + this.mob.LOOP_DUR) {
            this.mob.getNavigation().moveTo(this.target, 1.2D);
            if (this.mob.distanceToSqr(this.target) <= 4.0D) {
                this.mob.doHurtTarget(this.target);
            }
        } else if (currentTick <= this.mob.TOTAL_ATTACK_DUR) {
            this.mob.getNavigation().stop();
        }

        this.mob.setAttackTick(currentTick + 1);

        if (this.mob.getAttackTick() >= this.mob.TOTAL_ATTACK_DUR) {
            this.mob.setAttackTick(0);
            this.mob.attackCooldown = 100;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getAttackTick() > 0;
    }

    @Override
    public void stop() {
        this.mob.setAttackTick(0);
    }
}