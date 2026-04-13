package ender_bayunzi.art_of_sweetness.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import ender_bayunzi.art_of_sweetness.init.ModMagic;
import ender_bayunzi.art_of_sweetness.init.ModRegistries;
import ender_bayunzi.art_of_sweetness.magic.Magic;

public class MagicCodec implements Codec<Magic> {

	@Override
	public <T> DataResult<T> encode(Magic input, DynamicOps<T> ops, T prefix) {
		return DataResult.success(ops.createString(ModRegistries.MagicAddCallback.nameMap.getOrDefault(input, "null")));
	}

	@Override
	public <T> DataResult<Pair<Magic, T>> decode(DynamicOps<T> ops, T input) {
		return DataResult.success(Pair.of(ModRegistries.MagicAddCallback.magicMap.getOrDefault(ops.getStringValue(input).getOrThrow(), ModMagic.EMPTY.get()), input));
	}

}
