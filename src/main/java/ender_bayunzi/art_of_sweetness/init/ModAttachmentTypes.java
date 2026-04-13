package ender_bayunzi.art_of_sweetness.init;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachmentTypes {

	public static final DeferredRegister<AttachmentType<?>> REGISTRY = 
			DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ArtOfSweetness.MODID);
	
	public static final Supplier<AttachmentType<Integer>> SMVALUE = 
			REGISTRY.register("sm", 
					() -> AttachmentType.<Integer>builder(() -> 0)
					.serialize(Codec.INT)
					.sync(ByteBufCodecs.VAR_INT)
					.copyOnDeath()
					.build()
					);
	
	public static final Supplier<AttachmentType<List<Magic>>> KNOWN_MAGIC = 
			REGISTRY.register("known_magic", 
					() -> AttachmentType.<List<Magic>>builder(() -> new ArrayList<Magic>())
					.serialize(Codec.list(ModCodec.magic))
					.sync(ModCodec.MAGIC_STREAM_CODEC.apply(ByteBufCodecs.list()))
					.copyOnDeath()
					.build()
					);
	
}
