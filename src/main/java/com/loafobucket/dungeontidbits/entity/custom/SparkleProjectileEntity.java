package com.loafobucket.dungeontidbits.entity.custom;

import com.loafobucket.dungeontidbits.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;

public class SparkleProjectileEntity extends Projectile {
    private int life;
    private int lifetime;
    private float damage;

    public SparkleProjectileEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public SparkleProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.SPARKLE.get(), level);
        this.life = 0;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return false;
    }

    public void setLifetime(int time) {
        this.lifetime = time;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        this.setPos(d0, d1, d2);
        this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
        ++this.life;
        if (this.level().isClientSide && this.life % 4 < 2) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.02, 0);
        }

        if (!this.level().isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    private void explode() {
        ServerLevel serverLevel = (ServerLevel) level();
        serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 5, 0.3, 0.3, 0.3, 0);
        serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 5, 0, 0, 0, 0.06);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SHULKER_BULLET_HURT, SoundSource.PLAYERS);
        this.level().broadcastEntityEvent(this, (byte)17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.dealExplosionDamage();
        this.discard();
    }

    private void dealExplosionDamage() {
        Vec3 vec3 = this.position();
        Iterator var6 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.0)).iterator();
        while(true) {
            LivingEntity livingentity;
            do {
                if (!var6.hasNext()) {
                    return;
                }
                livingentity = (LivingEntity)var6.next();
            } while(this.distanceToSqr(livingentity) > 25.0);

            boolean flag = false;

            for(int i = 0; i < 2; ++i) {
                Vec3 vec31 = new Vec3(livingentity.getX(), livingentity.getY(0.5 * (double)i), livingentity.getZ());
                HitResult hitresult = this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (hitresult.getType() == HitResult.Type.MISS) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                livingentity.hurt(this.damageSources().explosion(this, this), damage);
            }
        }
    }
}
