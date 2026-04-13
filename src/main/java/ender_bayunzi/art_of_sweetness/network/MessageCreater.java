package ender_bayunzi.art_of_sweetness.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCreater implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<MessageCreater> TYPE = new Type<MessageCreater>(ResourceLocation.fromNamespaceAndPath("ezapi", "creater"));
	public static final StreamCodec<RegistryFriendlyByteBuf, MessageCreater> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, MessageCreater message) -> {
		MessageCreater.toBuf(message, buffer);
	}, (RegistryFriendlyByteBuf buffer) -> MessageCreater.fromBuf(buffer));
	
	public static void toBuf(MessageCreater creater, FriendlyByteBuf buf) {
		buf.writeEnum(creater.message.getType());
		creater.message.toBytes(buf);
	}
	
	public static MessageCreater fromBuf(FriendlyByteBuf buf) {
		IFMessage message;
		try {
			message = (IFMessage) buf.readEnum(MessageType.class).callable.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		message.fromBytes(buf);
		return new MessageCreater(message);
	}
	
	public static void run(MessageCreater message, IPayloadContext context) {
		context.enqueueWork(() -> {
			message.message.run(context);
		}).exceptionally(e -> {
			context.connection().disconnect(Component.literal(e.getMessage()));
			return null;
		});
	}
	
	final IFMessage message;
	
	public MessageCreater(IFMessage message) {
		this.message = message;
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
	
}