package ender_bayunzi.art_of_sweetness.magic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SimpleMagic implements Magic {

	private final MagicType type;
	private final MagicAspect[] aspects;
	
	private float power;
	private ResourceLocation icon;
	private int sm;
	private int cooldown;
	private MagicAction action;
	
	public SimpleMagic(MagicType type, MagicAspect[] aspects) {
		this.type = type;
		this.aspects = aspects;
	}
	
	public SimpleMagic power(float power) {
		this.power = power;
		return this;
	}
	
	public SimpleMagic icon(ResourceLocation icon) {
		this.icon = icon;
		return this;
	}
	
	public SimpleMagic sm(int sm) {
		this.sm = sm;
		return this;
	}
	
	public SimpleMagic cooldown(int cooldown) {
		this.cooldown = cooldown;
		return this;
	}
	
	public SimpleMagic action(MagicAction action) {
		this.action = action;
		return this;
	}
	
	@Override
	public float basePower(ItemStack stakck, LivingEntity living) {
		return this.power;
	}

	@Override
	public int sm() {
		return this.sm;
	}

	@Override
	public int cooldown() {
		return this.cooldown;
	}

	@Override
	public MagicType type() {
		return this.type;
	}

	@Override
	public MagicAspect[] aspects() {
		return this.aspects;
	}

	@Override
	public void action(ItemStack stack, Level level, LivingEntity living, int usetime) {
		this.action.action(stack, level, living, usetime);
	}

	@Override
	public ResourceLocation icon() {
		return this.icon;
	}

}
