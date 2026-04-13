package ender_bayunzi.art_of_sweetness.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IFMessage {

	MessageType getType();
	
	void fromBytes(FriendlyByteBuf buf);
	
    void toBytes(FriendlyByteBuf buf);
	
	void run(IPayloadContext ctx);
	
}
