package ender_bayunzi.art_of_sweetness.init;

import java.awt.Color;
import java.util.function.Supplier;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.MagicAspect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

	public static final DeferredRegister<Item> REGISTRY = 
			DeferredRegister.create(Registries.ITEM, ArtOfSweetness.MODID);

	public static final Supplier<Item> exampleMagicItem = REGISTRY.register("example_magic_item", 
			() -> new MagicItem(7, 25, -0.1f, new MagicAspect[] {MagicAspect.GREEN,MagicAspect.RED}, new MagicAspect[] {MagicAspect.BLUE}) {
			
				public static Component toColorComponent(String string) {
					MutableComponent component = Component.empty();
					char[] charArray = string.toCharArray();
					for (int i = 0; i < charArray.length; i++) component.append(Component.literal(String.valueOf(charArray[i])).withColor(Color.HSBtoRGB((System.currentTimeMillis() % 3600 / 10 + i) / 100f, 1, 1)));
					return component;
				}
				
				@Override
				public void appendHoverText(net.minecraft.world.item.ItemStack stack, TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltipComponents, net.minecraft.world.item.TooltipFlag tooltipFlag) {
					
					tooltipComponents.add(toColorComponent("by Ender Bayunzi"));
					
					super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
				}
				
			});
	
}
