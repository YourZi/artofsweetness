package ender_bayunzi.art_of_sweetness.init;

import java.util.List;

import com.mojang.serialization.Codec;

import ender_bayunzi.art_of_sweetness.codec.MagicCodec;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ModCodec {

	public static final StreamCodec<RegistryFriendlyByteBuf, Magic> MAGIC_STREAM_CODEC = StreamCodec.of((buffer, magic) -> buffer.writeUtf(ModRegistries.MagicAddCallback.nameMap.getOrDefault(magic, "null")), (buffer) -> ModRegistries.MagicAddCallback.magicMap.get(buffer.readUtf()));
	
	public static final MagicCodec magic = new MagicCodec();
	
	public static Codec<List<Magic>> magic_list() {
		return Codec.list(magic, 0, Integer.MAX_VALUE);
	}
	
	public static Codec<List<Magic>> magic_list(int min, int max) {
		return Codec.list(magic, min, max);
	}
	
}
