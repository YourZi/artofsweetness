package ender_bayunzi.art_of_sweetness.init;

import java.util.function.Supplier;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.entity.IcingShotProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
	
	public static final DeferredRegister<EntityType<?>> REGISTRY = 
			DeferredRegister.create(Registries.ENTITY_TYPE, ArtOfSweetness.MODID);
	
	public static final Supplier<EntityType<IcingShotProjectile>> ICINGSHOTPROJECTILE = REGISTRY.register("icing_shot_projectile", 
			() -> EntityType.Builder.<IcingShotProjectile>of(IcingShotProjectile::new, MobCategory.MISC).clientTrackingRange(4).updateInterval(10).build("icing_shot_projectile"));
	
}
