package ender_bayunzi.art_of_sweetness.magic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class MagicModifier {
	
	public abstract float amplification(ItemStack stakck, LivingEntity living);
	public abstract void action(ItemStack stack, Level level, LivingEntity living, int usetime);
	
}
