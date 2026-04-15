package ender_bayunzi.art_of_sweetness.network;

import ender_bayunzi.art_of_sweetness.client.screen.MagicListOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageOverlayRenderX implements IFMessage {

	private int renderX;
	
	public MessageOverlayRenderX() {}
	public MessageOverlayRenderX(int renderX) {
		this.renderX = renderX;
	}
	
	@Override
	public MessageType getType() {
		return MessageType.overlayRenderX;
	}

	@Override
	public void fromBytes(FriendlyByteBuf buf) {
		this.renderX = buf.readInt();
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(this.renderX);
	}

	@Override
	public void run(IPayloadContext ctx) {
		// 客户端统一通过 overlay 管理动画状态，网络只负责转发环绕补偿
		MagicListOverlay.shiftAnimation(this.renderX);
	}

}
