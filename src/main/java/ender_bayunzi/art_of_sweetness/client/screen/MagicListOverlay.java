package ender_bayunzi.art_of_sweetness.client.screen;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(Dist.CLIENT)
public class MagicListOverlay {

	private static final int UNKNOWN_ICON_U = 211;
	private static final int UNKNOWN_ICON_V = 49;
	private static final ResourceLocation UP = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/up.png");
	private static final ResourceLocation DOWN = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/screen/down.png");
	
	public static final Minecraft mc = Minecraft.getInstance();
	
	// 提取动画参数为常量
	private static final double MIN_SCROLL_SPEED = 160D;
	private static final double FOLLOW_SCROLL_SPEED = 12D;
	private static final double SNAP_DISTANCE = 0.05D;
	private static final double UNINITIALIZED = Double.NaN;

	private static double currentMidRenderX = UNINITIALIZED;
	private static double targetMidRenderX = UNINITIALIZED;
	private static double currentCycleLength = 0D;
	private static long lastFrameTimeNanos = -1L;
	
	@SubscribeEvent
	public static void onClientEvent(ClientTickEvent.Pre event) {
		if (mc.player != null) {
			LocalPlayer player = mc.player;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof MagicItem) {
				Magic[] magicList = MagicAPI.getMagicList(stack);
				// 法术栏只显示已装配法术，跳过空槽位
				Magic[] equippedMagicList = getEquippedMagicList(magicList);
				if (equippedMagicList.length > 0) {
					int equippedIndex = getEquippedMagicIndex(magicList, MagicAPI.getMagicIndex(stack));
					double normalizedMidRenderX = equippedIndex >= 0 ? equippedIndex * 16D : 0D;
					double cycleLength = equippedMagicList.length * 16D;
					MagicListOverlay.currentCycleLength = cycleLength;
					if (!isInitialized()) {
						MagicListOverlay.currentMidRenderX = normalizedMidRenderX;
					} else {
						// 首尾切换改为环形衔接避免大跨越
						MagicListOverlay.currentMidRenderX = getClosestWrappedPosition(MagicListOverlay.currentMidRenderX, cycleLength, normalizedMidRenderX);
					}
					if (equippedIndex >= 0) {
						MagicListOverlay.targetMidRenderX = getClosestWrappedPosition(normalizedMidRenderX, cycleLength, MagicListOverlay.currentMidRenderX);
					} else {
						MagicListOverlay.targetMidRenderX = MagicListOverlay.currentMidRenderX;
					}
				} else {
					resetAnimation();
				}
			} else {
				resetAnimation();
			}
		} else {
			resetAnimation();
		}
	}
	
	@SubscribeEvent
	public static void onRenderGui(RenderGuiEvent.Post event) {
		if (mc.player != null) {
			LocalPlayer player = mc.player;
			if (player.isSpectator()) return;
			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof MagicItem) {
				Magic[] magicList = MagicAPI.getMagicList(stack);
				Magic[] equippedMagicList = getEquippedMagicList(magicList);
				if (equippedMagicList.length == 0) return;
				// 选中索引同样按“已装配列表”计算，跳过空槽位
				int index = getEquippedMagicIndex(magicList, MagicAPI.getMagicIndex(stack));
				
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
				
				int width = Math.min(equippedMagicList.length, 5) * 16;
				
				event.getGuiGraphics().setColor(1f, 1f, 1f, 0.5f);
				//箭头横过来
				//event.getGuiGraphics().blit(UP, (w - width) / 2 - 16, y + 4, 0, 0, 16, 8, 16, 8);
				//event.getGuiGraphics().blit(DOWN, (w + width) / 2, y + 4, 0, 0, 16, 8, 16, 8);
				event.getGuiGraphics().blit(UP, (w - width) / 2 - 12, y, 0, 0, 8, 16, 8, 16);
				event.getGuiGraphics().blit(DOWN, (w + width) / 2 + 4, y, 0, 0, 8, 16, 8, 16);
				
				int top = (w - width) / 2;
				int bottom = (w + width) / 2;
				
				double animatedMidRenderX = getAnimatedMidRenderX();
				// 屏幕中心减去图标半宽作为固定锚点，保证水平居中
				int renderX = Mth.floor(w / 2D - 8D - animatedMidRenderX);
				
				for (int i = -equippedMagicList.length; i < equippedMagicList.length * 2; i++) {
					if (i == index) event.getGuiGraphics().setColor(1f, 1f, 1f, 1f);
					else event.getGuiGraphics().setColor(1f, 1f, 1f, 0.2f);
					
					int magicIndex = i % equippedMagicList.length;
					if (magicIndex < 0) magicIndex += equippedMagicList.length;
					
					Magic magic = equippedMagicList[magicIndex];
					ResourceLocation icon = magic.icon();
					
					int drawX = renderX + i * 16;
					
					if (drawX >= top && drawX + 16 <= bottom) {
						drawMagicIcon(event.getGuiGraphics(), magic, icon, drawX, y, 0, 16);
					} else if (drawX < top && drawX + 16 > top) {
						int drawW = drawX + 16 - top;
						drawX += top - drawX;
						
						drawMagicIcon(event.getGuiGraphics(), magic, icon, drawX, y, 16 - drawW, drawW);
					} else if (drawX < bottom && drawX + 16 > bottom) {
						int drawW = bottom - drawX;
						
						drawMagicIcon(event.getGuiGraphics(), magic, icon, drawX, y, 0, drawW);
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
	
	public static void shiftAnimation(double renderXOffset) {
		if (Double.isNaN(renderXOffset)) return;
		if (MagicListOverlay.currentCycleLength > 0D && renderXOffset != 0D) {
			renderXOffset = Math.copySign(MagicListOverlay.currentCycleLength, renderXOffset);
		}
		if (isInitialized()) {
			MagicListOverlay.currentMidRenderX += renderXOffset;
		}
		if (!Double.isNaN(MagicListOverlay.targetMidRenderX)) {
			MagicListOverlay.targetMidRenderX += renderXOffset;
		}
	}
	
	private static boolean isInitialized() {
		return !Double.isNaN(MagicListOverlay.currentMidRenderX);
	}
	
	private static void resetAnimation() {
		MagicListOverlay.currentMidRenderX = UNINITIALIZED;
		MagicListOverlay.targetMidRenderX = UNINITIALIZED;
		MagicListOverlay.currentCycleLength = 0D;
		MagicListOverlay.lastFrameTimeNanos = -1L;
	}
	
	private static double getClosestWrappedPosition(double basePosition, double cycleLength, double referencePosition) {
		if (cycleLength <= 0D || Double.isNaN(referencePosition)) return basePosition;
		// 在环形列表上找离参考点最近的等价坐标，用于首尾相接动画
		double wrappedCycles = Math.rint((referencePosition - basePosition) / cycleLength);
		return basePosition + wrappedCycles * cycleLength;
	}
	
	private static double getAnimatedMidRenderX() {
		if (!isInitialized()) return 0D;
		if (Double.isNaN(MagicListOverlay.targetMidRenderX)) return MagicListOverlay.currentMidRenderX;
		
		long now = System.nanoTime();
		if (MagicListOverlay.lastFrameTimeNanos < 0L) {
			MagicListOverlay.lastFrameTimeNanos = now;
			return MagicListOverlay.currentMidRenderX;
		}
		
		// 改为渲染帧间隔推进动画而不是按 tick 固定挪动，每秒20帧太卡了
		double deltaSeconds = Math.min((now - MagicListOverlay.lastFrameTimeNanos) / 1_000_000_000D, 0.05D);
		MagicListOverlay.lastFrameTimeNanos = now;
		
		double distance = MagicListOverlay.targetMidRenderX - MagicListOverlay.currentMidRenderX;
		if (Math.abs(distance) <= SNAP_DISTANCE) {
			MagicListOverlay.currentMidRenderX = MagicListOverlay.targetMidRenderX;
			return MagicListOverlay.currentMidRenderX;
		}
		
		double speed = Math.max(MIN_SCROLL_SPEED, Math.abs(distance) * FOLLOW_SCROLL_SPEED);
		double step = Math.min(Math.abs(distance), speed * deltaSeconds);
		MagicListOverlay.currentMidRenderX += Math.copySign(step, distance);
		return MagicListOverlay.currentMidRenderX;
	}
	
	private static void drawMagicIcon(net.minecraft.client.gui.GuiGraphics guiGraphics, Magic magic, ResourceLocation icon, int drawX, int drawY, int sourceX, int drawWidth) {
		if (drawWidth <= 0 || magic == ModMagic.empty) return;
		if (icon != null) {
			guiGraphics.blit(icon, drawX, drawY, sourceX, 0, drawWidth, 16, 16, 16);
			return;
		}
		// 未知法术与普通图标共用同一套横向裁剪逻辑，避免边界处出现错位和叠层。
		guiGraphics.blit(MagicSelectionGui.GUI, drawX, drawY, UNKNOWN_ICON_U + sourceX, UNKNOWN_ICON_V, drawWidth, 16, 256, 256);
	}
	
	private static Magic[] getEquippedMagicList(Magic[] magicList) {
		// 跳过空槽位
		List<Magic> equippedMagicList = new ArrayList<Magic>();
		for (Magic magic : magicList) {
			if (magic != ModMagic.empty) {
				equippedMagicList.add(magic);
			}
		}
		return equippedMagicList.toArray(new Magic[0]);
	}
	
	private static int getEquippedMagicIndex(Magic[] magicList, int currentIndex) {
		if (currentIndex < 0 || currentIndex >= magicList.length || magicList[currentIndex] == ModMagic.empty) return -1;
		int equippedIndex = 0;
		for (int i = 0; i < magicList.length; i++) {
			if (magicList[i] == ModMagic.empty) continue;
			if (i == currentIndex) return equippedIndex;
			equippedIndex++;
		}
		return -1;
	}
	
}
