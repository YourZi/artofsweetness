package ender_bayunzi.art_of_sweetness.magic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface MagicAction {

	void action(ItemStack stack, Level level, LivingEntity living, int usetime);
	
}
