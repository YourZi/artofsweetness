package ender_bayunzi.art_of_sweetness.client.init;

import com.mojang.blaze3d.platform.InputConstants;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.client.gui.MagicSelectionGui;
import ender_bayunzi.art_of_sweetness.network.MessageCreater;
import ender_bayunzi.art_of_sweetness.network.MessageKeyDown;
import ender_bayunzi.art_of_sweetness.network.MessageKeyUP;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ArtOfSweetness.MODID, value = Dist.CLIENT)
public class ModKeyMapping {

	public static final String category = "key.art_of_sweetness.category";
	public static final Minecraft mc = Minecraft.getInstance();
	
	public static final KeyMapping up = new KeyMapping("key.art_of_sweetness.up", InputConstants.KEY_I, category);
	public static final KeyMapping down = new KeyMapping("key.art_of_sweetness.down", InputConstants.KEY_O, category);
	public static final KeyMapping ui = new KeyMapping("key.art_of_sweetness.ui", InputConstants.KEY_P, category);
	
	@SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
		if (mc.level == null || mc.screen != null) return;
		int key = event.getKey();
		if (event.getAction() == 1) {
			if (key == up.getKey().getValue()) PacketDistributor.sendToServer(new MessageCreater(new MessageKeyUP()));
			if (key == down.getKey().getValue()) PacketDistributor.sendToServer(new MessageCreater(new MessageKeyDown()));
			if (key == ui.getKey().getValue()) mc.setScreen(new MagicSelectionGui());
		}
	}
	
	@SubscribeEvent
	public static void onKeyRegister(RegisterKeyMappingsEvent event) {
		event.register(up);
		event.register(down);
		event.register(ui);
	}
	
}
