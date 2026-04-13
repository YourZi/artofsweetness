package ender_bayunzi.art_of_sweetness.client.init;

import ender_bayunzi.art_of_sweetness.client.renderer.IcingShotProjectileRenderer;
import ender_bayunzi.art_of_sweetness.init.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ModEntityRenderers {

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.ICINGSHOTPROJECTILE.get(), IcingShotProjectileRenderer::new);
	}
	
}
