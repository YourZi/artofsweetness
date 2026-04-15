package ender_bayunzi.art_of_sweetness.init;

import java.util.function.Supplier;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.entity.IcingShotProjectile;
import ender_bayunzi.art_of_sweetness.init.ModRegistries.ModResourceKeys;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import ender_bayunzi.art_of_sweetness.magic.MagicAspect;
import ender_bayunzi.art_of_sweetness.magic.MagicType;
import ender_bayunzi.art_of_sweetness.magic.SimpleMagic;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMagic {

	public static final DeferredRegister<Magic> REGISTRY = 
			DeferredRegister.create(ModResourceKeys.MAGIC_REGISTRY_KEY, ArtOfSweetness.MODID);

	public static final Magic empty = new SimpleMagic(MagicType.Unknown, new MagicAspect[0]);
	public static final Magic unknown = new SimpleMagic(MagicType.Unknown, new MagicAspect[0]);
	
	public static final Supplier<Magic> EMPTY = REGISTRY.register("empty", () -> empty);
	public static final Supplier<Magic> UNKNOWN = REGISTRY.register("unknown", () -> unknown);
	
	public static final Supplier<Magic> ICING_SHOT = REGISTRY.register("icing_shot", 
			() -> new SimpleMagic(MagicType.Instant, new MagicAspect[] {MagicAspect.WHITE}).power(5).cooldown(30).sm(5).icon(ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/magic/icing_shot.png")).action((stack, level, living, time) -> {
				if (!level.isClientSide) {
					float power = ModMagic.ICING_SHOT.get().spellPower(stack, living);
					IcingShotProjectile projectile = new IcingShotProjectile(ModEntities.ICINGSHOTPROJECTILE.get(), level);
					projectile.setPower(power);
					projectile.setOwner(living);
					projectile.setSilent(true);
					projectile.setPos(living.getEyePosition().add(living.getLookAngle()));
					projectile.shoot(living.getViewVector(1).x, living.getViewVector(1).y, living.getViewVector(1).z, Math.min(power / 4F, 2), 0);
					level.addFreshEntity(projectile);
				}
			}));
	
}
