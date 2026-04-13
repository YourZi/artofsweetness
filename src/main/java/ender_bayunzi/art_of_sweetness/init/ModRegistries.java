package ender_bayunzi.art_of_sweetness.init;

import java.util.HashMap;
import java.util.Map;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.callback.AddCallback;

public class ModRegistries {

	public static Registry<Magic> magic;
	
	public static class ModResourceKeys {
		
		public static final ResourceKey<Registry<Magic>> MAGIC_REGISTRY_KEY = 
				ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "magic"));
	
	}
	
	public static class MagicAddCallback implements AddCallback<Magic> {

		public static final Map<String, Magic> magicMap = new HashMap<String, Magic>();
		public static final Map<Magic, String> nameMap = new HashMap<Magic, String>();
		
		@Override
		public void onAdd(Registry<Magic> registry, int id, ResourceKey<Magic> key, Magic value) {
			String name = key.location().toString();
			magicMap.put(name, value);
			nameMap.put(value, name);
			
			ArtOfSweetness.LOGGER.info("register " + name + " to " + value);
		}
		
	}
	
}
