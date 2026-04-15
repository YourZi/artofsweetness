package ender_bayunzi.art_of_sweetness.network;

import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageKeyUP implements IFMessage {

	@Override
	public MessageType getType() {
		return MessageType.keyUp;
	}

	@Override
	public void fromBytes(FriendlyByteBuf buf) {}

	@Override
	public void toBytes(FriendlyByteBuf buf) {}

	@Override
	public void run(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!stack.isEmpty() && stack.getItem() instanceof MagicItem magic) {
			int index = MagicAPI.getMagicIndex(stack) - 1;
			if (index < 0) {
				index += magic.properties.slots;
				if (player instanceof ServerPlayer sp) PacketDistributor.sendToPlayer(sp, new MessageCreater(new MessageOverlayRenderX(magic.properties.slots * 16)));
			}
			MagicAPI.setMagicIndex(stack, index);
		}
	}

}
