package ender_bayunzi.art_of_sweetness.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.client.gui.MagicSelectionGui;
import ender_bayunzi.art_of_sweetness.init.ModMagic;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(Dist.CLIENT)
public class MagicListOverlay {

	private static final ResourceLocation UP = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/up.png");
	private static final ResourceLocation DOWN = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/down.png");
	
	public static final Minecraft mc = Minecraft.getInstance();
	
	private static final int speed = 2;
	public static int lastMidRenderX = -1;
	public static int midRenderX = -1;
	
	@SubscribeEvent
	public static void onClientEvent(ClientTickEvent.Pre event) {
		if (mc.player != null) {
			LocalPlayer player = mc.player;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof MagicItem) {
				int midRenderX = MagicAPI.getMagicIndex(stack) * 16;
				if (MagicListOverlay.midRenderX == -1) {
					MagicListOverlay.midRenderX = midRenderX;
					MagicListOverlay.lastMidRenderX = midRenderX;
				} else {
					MagicListOverlay.lastMidRenderX = MagicListOverlay.midRenderX;
					if (MagicListOverlay.midRenderX > midRenderX) MagicListOverlay.midRenderX = Math.max(MagicListOverlay.midRenderX - MagicListOverlay.speed, midRenderX);
					else if (MagicListOverlay.midRenderX < midRenderX) MagicListOverlay.midRenderX = Math.min(MagicListOverlay.midRenderX + MagicListOverlay.speed, midRenderX);
				}
			} else
				MagicListOverlay.midRenderX = -1;
		} else
			MagicListOverlay.midRenderX = -1;
	}
	
	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		if (mc.player != null) {
			LocalPlayer player = mc.player;
			if (player.isSpectator()) return;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof MagicItem) {
				Magic[] magicList = MagicAPI.getMagicList(stack);
				int index = MagicAPI.getMagicIndex(stack);
				
				int w = event.getGuiGraphics().guiWidth();
				int h = event.getGuiGraphics().guiHeight();
				
				RenderSystem.disableDepthTest();
				RenderSystem.depthMask(false);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
				
				int y = h - Math.max(mc.gui.rightHeight, mc.gui.leftHeight);
				
				if (!player.isCreative()) y -= 8;
				
				int width = Math.min(magicList.length, 5) * 16;
				
				event.getGuiGraphics().setColor(1f, 1f, 1f, 0.5f);
				event.getGuiGraphics().blit(UP, (w - width) / 2 - 16, y + 4, 0, 0, 16, 8, 16, 8);
				event.getGuiGraphics().blit(DOWN, (w + width) / 2, y + 4, 0, 0, 16, 8, 16, 8);
				
				int width2 = magicList.length;
				
				int top = (w - width) / 2;
				int bottom = (w + width) / 2;
				
				int renderX = (int) (w / 2 - width2 - (MagicListOverlay.lastMidRenderX + (MagicListOverlay.midRenderX - MagicListOverlay.lastMidRenderX) * event.getPartialTick().getRealtimeDeltaTicks()));
				
				for (int i = -magicList.length; i < magicList.length * 2; i++) {
					if (i == index) event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
					else event.getGuiGraphics().setColor(1f, 1f, 1f, 0.2f);
					
					int magicIndex = i % magicList.length;
					if (magicIndex < 0) magicIndex += magicList.length;
					
					Magic magic = magicList[magicIndex];
					ResourceLocation icon = magic.icon();
					
					int drawX = renderX + i * 16;
					
					if (drawX >= top && drawX + 16 <= bottom) {
						if (icon != null) event.getGuiGraphics().blit(icon, drawX, y, 0, 0, 16, 16, 16, 16);
						else if (magic != ModMagic.empty) event.getGuiGraphics().blit(MagicSelectionGui.GUI, drawX, y, 211, 49, 16, 16, 256, 256);	
					} else if (drawX < top && drawX + 16 > top) {
						int drawW = drawX + 16 - top;
						drawX += top - drawX;
						
						if (icon != null) event.getGuiGraphics().blit(icon, drawX, y, 16 - drawW, 0, drawW, 16, 16, 16);
						else if (magic != ModMagic.empty) event.getGuiGraphics().blit(MagicSelectionGui.GUI, drawX, y, 211, 49 + drawW, 16, 16 - drawW, 256, 256);	
					} else if (drawX < bottom && drawX + 16 > bottom) {
						int drawW = bottom - drawX;
						
						if (icon != null) event.getGuiGraphics().blit(icon, drawX, y, 0, 0, drawW, 16, 16, 16);
						else if (magic != ModMagic.empty) event.getGuiGraphics().blit(MagicSelectionGui.GUI, drawX, y, 211, 49, drawW, 16, 256, 256);	
					}
						
				}
				
				event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
				event.getGuiGraphics().fill(top - 1, y, top, y + 17, 0xFFFFFFFF);
				event.getGuiGraphics().fill(bottom, y, bottom + 1, y + 17, 0xFFFFFFFF);
				
				event.getGuiGraphics().fill(top - 1, y - 1, bottom + 1, y, 0xFFFFFFFF);
				event.getGuiGraphics().fill(top, y + 16, bottom, y + 17, 0xFFFFFFFF);
				
				RenderSystem.depthMask(true);
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1, 1, 1, 1);

			}
		}
	}
	
}
