package ender_bayunzi.art_of_sweetness.network;

import java.util.List;

import ender_bayunzi.art_of_sweetness.init.ModCodec;
import ender_bayunzi.art_of_sweetness.init.ModDataComponentTypes;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageSyncMagicList implements IFMessage {

	private List<Magic> list;
	
	public MessageSyncMagicList() {}
	
	public MessageSyncMagicList(List<Magic> list) { this.list = list; }
	
	@Override
	public MessageType getType() {
		return MessageType.syncMagicList;
	}

	@Override
	public void fromBytes(FriendlyByteBuf buf) {
		list = buf.readJsonWithCodec(ModCodec.magic_list(0, 7));
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeJsonWithCodec(ModCodec.magic_list(0, 7), this.list);
		
	}

	@Override
	public void run(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!stack.isEmpty() && stack.getItem() instanceof MagicItem)
			stack.set(ModDataComponentTypes.MAGICLIST, this.list);
	}

}
