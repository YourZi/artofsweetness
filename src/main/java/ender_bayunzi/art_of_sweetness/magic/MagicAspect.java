package ender_bayunzi.art_of_sweetness.magic;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum MagicAspect {

	WHITE("white", 0XFFFFFF, 0, 1f),
	RED("red", 0XFF0000, 1, 0f),
	YELLOW("yellow", 0XFFFF00, 2, 0f),
	BLUE("blue", 0X0000FF, 3, 0f),
	GREEN("green", 0X00FF00, 4, 0f);
	
	public String id;
	public int color;
	public int level;
	public float amplification;
	
	private MagicAspect(String id, int color, int level, float amplification) {
		this.id = id;
		this.color = color;
		this.level = level;
		this.amplification = amplification;
	}
	
	public Component toComponent() {
		return Component.translatable("art_of_sweetness.aspect." + this.id).withColor(this.color);
	}
	
	public static Component toComponent(MagicAspect[] aspects) {
		MutableComponent component = Component.empty();
		for (int i = 0; i < aspects.length; i++) {
			if (i != 0) component.append(" ");
			component.append(aspects[i].toComponent());
		}
		return component;
	}
	
}
