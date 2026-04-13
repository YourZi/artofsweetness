package ender_bayunzi.art_of_sweetness.init;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModAttributes {

	public static final DeferredRegister<Attribute> REGISTRY = 
			DeferredRegister.create(Registries.ATTRIBUTE, ArtOfSweetness.MODID);
	
	public static final Holder<Attribute> POWER = REGISTRY.register("power", () -> new RangedAttribute("attribute.art_of_sweetness.power", 0, 0, 1024).setSyncable(true));
	public static final Holder<Attribute> COOLDOWN_RATE = REGISTRY.register("cooldown_rate", () -> new PercentageAttribute("attribute.art_of_sweetness.cooldown_rate", 1, -1, 1).setSyncable(true));
	
}
