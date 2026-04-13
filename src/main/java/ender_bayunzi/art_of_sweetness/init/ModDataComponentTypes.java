package ender_bayunzi.art_of_sweetness.init;

import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponentTypes {

	public static final DeferredRegister<DataComponentType<?>> REGISTRY = 
			DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ArtOfSweetness.MODID);
	
	public static final Supplier<DataComponentType<List<Magic>>> MAGICLIST = 
			REGISTRY.register("item_magic", 
					() -> DataComponentType.<List<Magic>>builder()
					.persistent(ModCodec.magic_list(0, 7))
					.networkSynchronized(ModCodec.MAGIC_STREAM_CODEC.apply(ByteBufCodecs.list()))
					.cacheEncoding()
					.build());
	
	public static final Supplier<DataComponentType<Integer>> MAGICINDEX = 
			REGISTRY.register("magic_index", 
					() -> DataComponentType.<Integer>builder()
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT)
					.cacheEncoding()
					.build());
	
}
