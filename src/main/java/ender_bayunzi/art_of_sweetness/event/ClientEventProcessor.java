package ender_bayunzi.art_of_sweetness.event;

import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEventProcessor {

	public static final Minecraft mc = Minecraft.getInstance();
	public static Magic currentMagic = null;
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		if (mc.player == null) ClientEventProcessor.currentMagic = null;
		else {
			LocalPlayer player = mc.player;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (stack.isEmpty() || !(stack.getItem() instanceof MagicItem)) ClientEventProcessor.currentMagic = null;
			else {
				Magic currentMagic = MagicAPI.getCurrentMagic(player, stack);
				if (ClientEventProcessor.currentMagic != currentMagic) {
					player.displayClientMessage(currentMagic.getName(stack), true);
					ClientEventProcessor.currentMagic = currentMagic;
				}
			}
		}
	}
	
}
