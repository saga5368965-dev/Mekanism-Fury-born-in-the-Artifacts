package XiGyoku.furyborn.entity;

import XiGyoku.furyborn.entity.AI.SpinAttackGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class RobyteEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(RobyteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENTERED_FINAL_PHASE = SynchedEntityData.defineId(RobyteEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int attackCooldown = 0;
    public int phaseTransitionTick = 0;

    public final int START_DUR = 20;
    public final int LOOP_DUR = 200;
    public final int END_DUR = 20;
    public final int TOTAL_ATTACK_DUR = START_DUR + LOOP_DUR + END_DUR;
    public final int PHASE_TRANSITION_DUR = 60;

    public RobyteEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_TICK, 0);
        this.entityData.define(ENTERED_FINAL_PHASE, false);
    }

    public int getAttackTick() {
        return this.entityData.get(ATTACK_TICK);
    }

    public void setAttackTick(int tick) {
        this.entityData.set(ATTACK_TICK, tick);
    }

    public boolean hasEnteredFinalPhase() {
        return this.entityData.get(ENTERED_FINAL_PHASE);
    }

    public void setEnteredFinalPhase(boolean phase) {
        this.entityData.set(ENTERED_FINAL_PHASE, phase);
    }

    public static AttributeSupplier createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SpinAttackGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private <E extends RobyteEntity> PlayState spawnPredicate(AnimationState<E> state) {
        if (this.tickCount < 40) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay("getup"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends RobyteEntity> PlayState phasePredicate(AnimationState<E> state) {
        if (!this.isDeadOrDying() && this.phaseTransitionTick > 0) {
            state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("FinalAttackStart"));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends RobyteEntity> PlayState deathPredicate(AnimationState<E> state) {
        if (this.isDeadOrDying()) {
            if (this.hasEnteredFinalPhase()) {
                state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("Defeated"));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private <E extends RobyteEntity> PlayState predicate(AnimationState<E> state) {
        if (this.tickCount < 40) return PlayState.STOP;
        if (this.phaseTransitionTick > 0) return PlayState.STOP;
        if (this.isDeadOrDying()) return PlayState.STOP;

        int currentAttackTick = this.getAttackTick();

        if (currentAttackTick > 0) {
            if (currentAttackTick <= START_DUR) {
                state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("RotationAttackStart"));
            } else if (currentAttackTick <= START_DUR + LOOP_DUR) {
                if (state.isMoving()) {
                    state.getController().setAnimation(RawAnimation.begin().thenLoop("RotationAttack"));
                } else {
                    state.getController().setAnimation(RawAnimation.begin().thenLoop("RotationAttackIdle"));
                }
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("RotationAttackEnd"));
            }
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            if (this.hasEnteredFinalPhase()) {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("FinalAttackWalking"));
            } else {
                state.getController().setAnimation(RawAnimation.begin().thenLoop("Walking"));
            }
            return PlayState.CONTINUE;
        }

        if (this.hasEnteredFinalPhase()) {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("FinalAttackIdle"));
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllers.add(new AnimationController<>(this, "spawn_controller", 0, this::spawnPredicate));
        controllers.add(new AnimationController<>(this, "phase_controller", 10, this::phasePredicate));
        controllers.add(new AnimationController<>(this, "death_controller", 0, this::deathPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    protected float getSoundVolume() {
        return 0.7F;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isDeadOrDying() && !this.hasEnteredFinalPhase() && this.getHealth() <= this.getMaxHealth() * 0.2F) {
            this.setEnteredFinalPhase(true);
            this.phaseTransitionTick = 1;
        }

        if (this.phaseTransitionTick > 0) {
            this.phaseTransitionTick++;
            this.getNavigation().stop();

            if (this.phaseTransitionTick > PHASE_TRANSITION_DUR) {
                this.phaseTransitionTick = 0;
            }
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.hasEnteredFinalPhase()) {
            if (this.deathTime == 140 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte)60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        } else {
            if (this.deathTime == 1 && !this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte)60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }
}