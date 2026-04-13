package ender_bayunzi.art_of_sweetness.client.gui;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MagicSelectionGui extends Screen {

	private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/gui/selection_gui.png");
	public static final Minecraft mc = Minecraft.getInstance();
	
	private final boolean[] options = new boolean[5];
	
	public MagicSelectionGui() {
		this(Component.translatable("gui.art_of_sweetness.selection_gui"));
	}
	
	public MagicSelectionGui(Component title) {
		super(title);
		
		Arrays.fill(this.options, true);
	}
	
	@Override
	protected void init() {
		super.init();
	
		for (int i = 0; i < this.options.length; i++) {
			final int index = i;
			Button button = Button.builder(Component.empty(), (btn) -> {
				if (MagicSelectionGui.this.options[index]) {
					this.options[index] = false;
					btn.setSize(7, 7);
				} else {
					this.options[index] = true;
					btn.setSize(24, 7);
				}
			}).pos(this.width / 2 - 90 + 180, this.height / 2 - 87 + i * 9).size(24, 7).build();
			this.addRenderableWidget(button);
		}
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		for (int i = 0; i < this.options.length; i++) {
			if (this.options[i]) guiGraphics.blit(GUI, this.width / 2 - 90 + 180, this.height / 2 - 87 + i * 9, 180, 9 * i, 25, 8, 256, 256);
			else guiGraphics.blit(GUI, this.width / 2 - 90 + 180, this.height / 2 - 87 + i * 9, 205, 9 * i, 8, 8, 256, 256);
		}
		
		RenderSystem.disableBlend();
	}
	
	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(GUI, guiGraphics.guiWidth() / 2 - 90, guiGraphics.guiHeight() / 2 - 87, 0, 0, 180, 174, 256, 256);
		RenderSystem.disableBlend();
	}
	
}
