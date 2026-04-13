package ender_bayunzi.art_of_sweetness.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class IcingShotProjectile extends ThrowableProjectile {

	private static final EntityDataAccessor<Float> POWER = SynchedEntityData.defineId(IcingShotProjectile.class, EntityDataSerializers.FLOAT);
	
	public IcingShotProjectile(EntityType<? extends IcingShotProjectile> type, Level world) {
		super(type, world);
	}

	public void setPower(float power) {
		this.getEntityData().set(POWER, power);
	}
	
	public float getPower() {
		return this.getEntityData().get(POWER);
	}
	
	@Override
	protected void defineSynchedData(Builder builder) {
		builder.define(POWER, 0F);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (tickCount % 3 == 0) this.level().broadcastEntityEvent(this, (byte) 3);
	}

	@Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; i++) 
            	this.level().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }
	
	 @Override
	 protected void onHit(HitResult result) {
		 super.onHit(result);
		 if (!this.level().isClientSide) {
			 this.level().broadcastEntityEvent(this, (byte) 3);
			 this.discard();
		 }
	 }

	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity entity = result.getEntity();
		if (entity instanceof LivingEntity living)
			living.hurt(this.damageSources().magic(), this.getPower());
	}
	
}
