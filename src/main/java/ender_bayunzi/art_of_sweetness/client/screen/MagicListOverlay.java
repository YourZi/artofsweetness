package ender_bayunzi.art_of_sweetness.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(Dist.CLIENT)
public class MagicListOverlay {

	private static final ResourceLocation UP = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/up.png");
	private static final ResourceLocation DOWN = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/down.png");
	
	public static final Minecraft mc = Minecraft.getInstance();
	
	@SubscribeEvent
	public static void eventHandler(RenderGuiEvent.Pre event) {
		if (mc.player != null) {
			LocalPlayer player = mc.player;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof MagicItem) {
				Magic[] magicList = MagicAPI.getMagicList(stack);
				int index = MagicAPI.getMagicIndex(stack);
				
				int w = event.getGuiGraphics().guiWidth();
				int h = event.getGuiGraphics().guiHeight();
				
				RenderSystem.disableDepthTest();
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				
				event.getGuiGraphics().setColor(1F, 1F, 1F, 1F);
				event.getGuiGraphics().blit(UP, w / 2 + 91, h - 16 * magicList.length - 16, 0, 0, 16, 8, 16, 8);
				
				for (int i = 0; i < magicList.length; i++) {
					Magic magic = magicList[i];
					ResourceLocation icon = magic.icon();
					if (index == i) event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
					else event.getGuiGraphics().setColor(1f, 1f, 1f, 0.5f);
					event.getGuiGraphics().fill(w / 2 + 91, h - 16 * magicList.length + i * 16 - 8, w / 2 + 91 + 16, h - 16 * magicList.length + i * 16 + 16 - 8, 0XAAAAAAAA);
					if (icon != null) event.getGuiGraphics().blit(icon, w / 2 + 91, h - 16 * magicList.length + i * 16 - 8, 0, 0, 16, 16, 16, 16);
				}
				

				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				
				event.getGuiGraphics().blit(DOWN, w / 2 + 91, h - 8, 0, 0, 16, 8, 16, 8);

				RenderSystem.depthMask(true);
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1, 1, 1, 1);

			}
		}
	}
	
}
