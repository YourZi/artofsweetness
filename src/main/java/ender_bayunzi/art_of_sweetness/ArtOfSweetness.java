package ender_bayunzi.art_of_sweetness;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import ender_bayunzi.art_of_sweetness.init.ModAttachmentTypes;
import ender_bayunzi.art_of_sweetness.init.ModAttributes;
import ender_bayunzi.art_of_sweetness.init.ModDataComponentTypes;
import ender_bayunzi.art_of_sweetness.init.ModEntities;
import ender_bayunzi.art_of_sweetness.init.ModItems;
import ender_bayunzi.art_of_sweetness.init.ModMagic;
import ender_bayunzi.art_of_sweetness.init.ModRegistries;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import ender_bayunzi.art_of_sweetness.network.MessageCreater;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@Mod(ArtOfSweetness.MODID)
public class ArtOfSweetness {

	public static final String MODID = "art_of_sweetness";
    public static final Logger LOGGER = LogUtils.getLogger();
	
	public ArtOfSweetness(IEventBus eventBus, ModContainer modContainer) {
		eventBus.addListener(this::newRegistry);
		eventBus.addListener(this::registerNetworking);
		
		ModAttachmentTypes.REGISTRY.register(eventBus);
		ModMagic.REGISTRY.register(eventBus);
		ModDataComponentTypes.REGISTRY.register(eventBus);
		ModAttributes.REGISTRY.register(eventBus);
		ModItems.REGISTRY.register(eventBus);
		ModEntities.REGISTRY.register(eventBus);
		
	}
	
	private void registerNetworking(RegisterPayloadHandlersEvent event) {
		event.registrar(ArtOfSweetness.MODID).playBidirectional(MessageCreater.TYPE, MessageCreater.STREAM_CODEC, MessageCreater::run);
	}
	
	private void newRegistry(NewRegistryEvent event) {
		ModRegistries.magic = event.create(new RegistryBuilder<Magic>(ModRegistries.ModResourceKeys.MAGIC_REGISTRY_KEY).onAdd(new ModRegistries.MagicAddCallback()));
	}
	
}
