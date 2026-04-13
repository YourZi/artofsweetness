package ender_bayunzi.art_of_sweetness.magic;

import java.util.List;

import ender_bayunzi.art_of_sweetness.init.ModRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface Magic {

	default float spellPwoerAmplification(ItemStack stack, LivingEntity living) {
		float maxAmplification = 0;
		
		MagicAspect[] aspects = this.aspects();
		for (int i = 0; i < aspects.length; i++)
			maxAmplification = Math.max(aspects[i].amplification, maxAmplification);
		
		MagicModifier modifier = this.modifier(stack);
		if (modifier != null)
			maxAmplification += modifier.amplification(stack, living);
		
		return maxAmplification;
	}
	
	default float spellPower(ItemStack stack, LivingEntity living) {
		return (1 + this.spellPwoerAmplification(stack, living)) * this.basePower(stack, living);
	}
	
	default MagicModifier modifier(ItemStack stack) {
		return null;
	}
	
	float basePower(ItemStack stakck, LivingEntity living);
	ResourceLocation icon();
	int sm();
	int cooldown();
	MagicType type();
	MagicAspect[] aspects();
	
	default void action(ItemStack stack, Level level, LivingEntity living, int usetime) {
		MagicModifier modifier = this.modifier(stack);
		if (modifier != null)
			modifier.action(stack, level, living, usetime);
	}
	
	default Component getName(ItemStack stack) {
		return Component.translatable("magic." + ModRegistries.MagicAddCallback.nameMap.getOrDefault(this, "unknown") + ".name");
	}
	
	default void tooltip(ItemStack stack, LivingEntity living, List<Component> tooltip) {
		tooltip.add(this.getName(stack));
		tooltip.add(MagicAspect.toComponent(this.aspects()));
		tooltip.add(Component.translatable("magic." + ModRegistries.MagicAddCallback.nameMap.getOrDefault(this, "unknown") + ".tooltip"));
		tooltip.add(Component.literal(ChatFormatting.RED + String.valueOf(this.sm()) + "sm" + ChatFormatting.WHITE + "/" + ChatFormatting.DARK_BLUE + String.valueOf(this.cooldown() / 20) + "s(cd)"));
	}
	
}
