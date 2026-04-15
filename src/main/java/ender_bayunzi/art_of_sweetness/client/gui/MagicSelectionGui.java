package ender_bayunzi.art_of_sweetness.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.init.ModMagic;
import ender_bayunzi.art_of_sweetness.init.ModRegistries;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import ender_bayunzi.art_of_sweetness.magic.MagicAspect;
import ender_bayunzi.art_of_sweetness.network.MessageCreater;
import ender_bayunzi.art_of_sweetness.network.MessageSyncMagicList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class MagicSelectionGui extends Screen {

	public static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/gui/selection_gui.png");
	public static final Minecraft mc = Minecraft.getInstance();
	
	private final boolean[] options = new boolean[5];
	private final Button[] button = new Button[5 * 3];
	private final Magic[] showMagicList = new Magic[5 * 3];
	
	private final LocalPlayer player;
	private final MagicItem.MagicItemProperties properties;
	private final Magic[] magicList;
	
	private int selectIndex = -1;
	
	private int backgroundX;
	private int backgroundY;
	
	private float scroll = 0;
	
	public MagicSelectionGui() {
		this(mc.player, mc.player.getItemInHand(InteractionHand.MAIN_HAND));
	}
	
	public MagicSelectionGui(LocalPlayer player, ItemStack stack) {
		this(Component.translatable("gui.art_of_sweetness.selection_gui"), player, stack);
	}
	
	public MagicSelectionGui(Component title, LocalPlayer player, ItemStack stack) {
		super(title);
		this.player = player;
		this.properties = ((MagicItem) stack.getItem()).properties;
		this.magicList = MagicAPI.getMagicList(stack);
		
		Arrays.fill(this.options, true);
		Arrays.fill(this.showMagicList, ModMagic.empty);
	
		MagicAspect[] notGoddAt = this.properties.notGoodAt;
		
		for (MagicAspect aspect : notGoddAt) {
			switch (aspect) {
			case WHITE:
				this.options[0] = false;
				break;
			case RED:
				this.options[1] = false;
				break;
			case YELLOW:
				this.options[2] = false;
				break;
			case BLUE:
				this.options[3] = false;
				break;
			case GREEN:
				this.options[4] = false;
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + aspect);
			}
		}
	}
	
	public void update() {
		List<Magic> magicList = new ArrayList<Magic>();
		List<Magic> knownMagic = MagicAPI.getLearnedMagic(player);
		
		for (Magic magic : ModRegistries.MagicAddCallback.magicMap.values()) {
			if (magic != ModMagic.empty 
					&& magic != ModMagic.unknown) {
				List<MagicAspect> aspects = Arrays.asList(magic.aspects());
				
				if (!options[0] && aspects.contains(MagicAspect.WHITE)) continue;
				if (!options[1] && aspects.contains(MagicAspect.RED)) continue;
				if (!options[2] && aspects.contains(MagicAspect.YELLOW)) continue;
				if (!options[3] && aspects.contains(MagicAspect.BLUE)) continue;
				if (!options[4] && aspects.contains(MagicAspect.GREEN)) continue;
				
				if (knownMagic.contains(magic) || player.isCreative()) magicList.add(magic);
				else magicList.add(ModMagic.unknown);
			}
		}
		
		int firstLine = (int) (magicList.size() * scroll / 5);
		int firstIndex = firstLine * 5;
		
		for (int i = 0; i < this.showMagicList.length; i++) {
			int index = firstIndex + i;
			Magic magic;
			if (index >= 0 && index < magicList.size()) magic = magicList.get(index);
			else magic = ModMagic.empty;
			this.showMagicList[i] = magic;
		}
		
	}
	
	@Override
	protected void init() {
		super.init();

		this.backgroundX = this.width / 2 - 90;
		this.backgroundY = this.height / 2 - 87;
		
		this.showMagicList[0] = ModMagic.ICING_SHOT.get();
		
		for (int i = 0; i < this.button.length; i++) {
			int index = i;
			this.addWidget(button[i] = Button.builder(Component.empty(), (btn) -> {
				if (this.showMagicList[index] != ModMagic.empty && this.showMagicList[index] != ModMagic.unknown && this.selectIndex >= 0 && this.selectIndex < 7 && this.properties.canSetSlotTo(this.selectIndex, this.showMagicList[index])) {
					this.magicList[this.selectIndex] = this.showMagicList[index];
					this.selectIndex = -1;
				}
			}).bounds(this.backgroundX + 6 + 34 * ((i % 5)), this.backgroundY + 15 + (34 * (i / 5)), 26, 26).build());
		}
		
		for (int i = 0; i < 7; i++) {
			if (i >= this.magicList.length || this.properties.locked[i]) continue;
			int x = this.backgroundX + 10 + 24 * i;
			int y = this.backgroundY + (i % 2 == 0 ? 143 : 133);
			int index = i;
			this.addWidget(Button.builder(Component.empty(), (btn) -> {
				this.selectIndex = index;
			}).bounds(x, y, 16, 16).build());
		}
		
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
				this.update();
			}).pos(this.backgroundX + 180, this.backgroundY + i * 9).build();
			if (!MagicSelectionGui.this.options[index]) button.setSize(7, 7);
			else button.setSize(24, 7);
			this.addRenderableWidget(button);	
		}
		
		this.update();
		
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		boolean tooltipRendered = false;
		
		for (int i = 0; i < this.options.length; i++) {
			if (this.options[i]) guiGraphics.blit(GUI, this.backgroundX + 180, this.backgroundY + i * 9, 180, 9 * i, 25, 8, 256, 256);
			else guiGraphics.blit(GUI, this.backgroundX + 180, this.backgroundY + i * 9, 205, 9 * i, 8, 8, 256, 256);
		}
		
		for (int i = 0; i < this.button.length; i++) {
			Button button = this.button[i];
			if (button == null) continue;
			int x = button.getX();
			int y = button.getY();
			
			Magic magic = this.showMagicList[i];
			if (magic != ModMagic.empty) {
				guiGraphics.blit(GUI, x, y, 180, 44, 26, 26, 256, 256);
				ResourceLocation icon = magic.icon();
				if (icon != null) guiGraphics.blit(icon, x + 5, y + 5, 0, 0, 16, 16, 16, 16);
				else guiGraphics.blit(GUI, x + 5, y + 5, 211, 49, 16, 16, 256, 256);
				
				if (x <= mouseX && mouseX <= x + button.getWidth()
				&& y <= mouseY && mouseY <= y + button.getHeight()
				&& !tooltipRendered) {
					renderMagicTooltip(magic, guiGraphics, mouseX, mouseY);
					tooltipRendered = true;
				}
			}
		}
		
		for (int i = 0; i < 7; i++) {
			int x = this.backgroundX + 10 + 24 * i;
			int y = this.backgroundY + (i % 2 == 0 ? 143 : 133);
			
			if (this.selectIndex == i)
				guiGraphics.blit(GUI, x - 3, y - 3, 234, 23, 22, 22, 256, 256);
			
			if (i >= this.magicList.length) {
				guiGraphics.blit(GUI, x + 3, y + 2, 188, 103, 10, 12, 256, 256);
			} else {
				Magic magic = this.magicList[i];
				if (magic != ModMagic.empty) {
					ResourceLocation icon = magic.icon();
					if (icon != null) guiGraphics.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
					else guiGraphics.blit(GUI, x, y, 211, 49, 16, 16, 256, 256);
					
					if (x <= mouseX && mouseX <= x + 16
					&& y <= mouseY && mouseY <= y + 16
					&& !tooltipRendered) {
						renderMagicTooltip(magic, guiGraphics, mouseX, mouseY);
						tooltipRendered = true;
					}
				}
				if (this.properties.locked[i]) guiGraphics.blit(GUI, x - 1, y - 1, 184, 126, 18, 18, 256, 256);
			}
		}
		
		RenderSystem.disableBlend();
	}
	
	public void renderMagicTooltip(Magic magic, GuiGraphics guiGraphics, int mouseX, int mouseY) {
		List<Component> components = new ArrayList<Component>();
		magic.tooltip(this.player.getItemInHand(InteractionHand.MAIN_HAND), this.player, components);
		guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
	}
	
	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(GUI, this.backgroundX, this.backgroundY, 0, 0, 180, 174, 256, 256);
		RenderSystem.disableBlend();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 1) {
			for (int i = 0; i < 7; i++) {
				int x = this.backgroundX + 10 + 24 * i;
				int y = this.backgroundY + (i % 2 == 0 ? 143 : 133);
				if (mouseX >= x && mouseY >= y && mouseX < x + 16 && mouseY < y + 16) {
					this.magicList[i] = ModMagic.empty;
					return true;
				}
			}
		}
		
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public void onClose() {
		super.onClose();
		PacketDistributor.sendToServer(new MessageCreater(new MessageSyncMagicList(Arrays.asList(this.magicList))));
	}
	
}
