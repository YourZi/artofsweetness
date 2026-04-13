package ender_bayunzi.art_of_sweetness.client.init;

import ender_bayunzi.art_of_sweetness.client.model.ModelIcingShotProjectile;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ModModels {

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelIcingShotProjectile.LAYER_LOCATION, ModelIcingShotProjectile::createBodyLayer);
	}
	
}
