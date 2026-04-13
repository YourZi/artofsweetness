package ender_bayunzi.art_of_sweetness.item;

import java.util.Arrays;
import java.util.List;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.api.MagicAPI;
import ender_bayunzi.art_of_sweetness.init.ModAttributes;
import ender_bayunzi.art_of_sweetness.init.ModDataComponentTypes;
import ender_bayunzi.art_of_sweetness.init.ModMagic;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import ender_bayunzi.art_of_sweetness.magic.MagicAspect;
import ender_bayunzi.art_of_sweetness.magic.MagicType;
import ender_bayunzi.art_of_sweetness.util.ArrayUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class MagicItem extends Item {

    public static final ResourceLocation BASE_POWER_ID = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "base_power");
    public static final ResourceLocation BASE_COOLDOWN_RATE_ID = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "base_cooldown_rate");
	
    public final int slots;
	public final MagicAspect[] goodAt;
	public final MagicAspect[] notGoodAt;
    
	public MagicItem(int slots, float power, float cooldownRate, MagicAspect[] goodAt, MagicAspect[] notGoodAt) {
		super(MagicItem.createProperties(slots, power, cooldownRate));
		
		this.slots = slots;
		this.goodAt = goodAt;
		this.notGoodAt = notGoodAt;
	}
	
	//此处禁止注册ModDataComponentTypes中的MAGICLIST！Magic还没注册
	public static Properties createProperties(int slots, float power, float cooldownRate) {
		Properties properties = new Properties();
		
		properties.attributes(ItemAttributeModifiers.builder()
				.add(ModAttributes.POWER, new AttributeModifier(BASE_POWER_ID, power, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(ModAttributes.COOLDOWN_RATE, new AttributeModifier(BASE_COOLDOWN_RATE_ID, cooldownRate, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build());
		
		return properties;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!stack.has(ModDataComponentTypes.MAGICLIST))
			stack.set(ModDataComponentTypes.MAGICLIST, Arrays.asList(ArrayUtil.fill(new Magic[slots], ModMagic.EMPTY.get())));
		if (!stack.has(ModDataComponentTypes.MAGICINDEX))
			stack.set(ModDataComponentTypes.MAGICINDEX, 0);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.slots", ChatFormatting.YELLOW + String.valueOf(this.slots)));
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.goodAt").append(MagicAspect.toComponent(this.goodAt)));
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.notGoodAt").append(MagicAspect.toComponent(this.notGoodAt)));
		
		if (tooltipFlag.hasShiftDown()) {
			tooltipComponents.add(Component.literal(""));
			int index = MagicAPI.getMagicIndex(stack);
			
			Magic[] list = MagicAPI.getMagicList(stack);
			for (int i = 0; i < this.slots; i++) {
				Magic magic = list[i];
				
				MutableComponent name = Component.empty();
				if (index == i) name.append(">");
				name.append(magic.getName(stack));
				MagicAspect[] aspects = magic.aspects();
				if (aspects.length > 0) {
					name.append("[");
					name.append(MagicAspect.toComponent(aspects));
					name.append("]");
				}
				
				tooltipComponents.add(name);
			}
		}
		
		/*int power = (int) this.power();
		int cooldownRate = (int) ((1 - this.cooldownRate()) * 100);
		
		tooltipComponents.add(Component.empty().append(power > 0 ? "+" : "-").append(String.valueOf(power)).append(" ").append(Component.translatable("art_of_sweetness.item.power")).withStyle(ChatFormatting.DARK_BLUE));
		tooltipComponents.add(Component.empty().append(cooldownRate > 0 ? "-" : "+").append(String.valueOf((int) cooldownRate)).append("% ").append(Component.translatable("art_of_sweetness.item.cooldownRate")).withStyle(ChatFormatting.DARK_BLUE));*/
	}
	
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
		Magic magic = MagicAPI.getCurrentMagic(livingEntity, stack);
		if (magic != null) {
			if (magic.type() == MagicType.Channeled) {
				int useTime = this.getUseDuration(stack, livingEntity) - timeCharged;
				magic.action(stack, level, livingEntity, useTime);
			} else if (magic.type() == MagicType.Sustained)
				if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), magic.cooldown());
		}
	}
	
	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
		Magic magic = MagicAPI.getCurrentMagic(livingEntity, stack);
		if (magic != null && magic.type() == MagicType.Channeled) {
			int useTime = this.getUseDuration(stack, livingEntity) - remainingUseDuration;
			magic.action(stack, level, livingEntity, useTime);
		}
	}
	
	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		Magic magic = MagicAPI.getCurrentMagic(entity, stack);
		if (magic != null && magic.type() != MagicType.Instant) return 72000;
		else return 0;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		Magic magic = MagicAPI.getCurrentMagic(player, stack);
		
		if (magic != null && magic.type() != MagicType.Unknown) {
			if (magic.type() == MagicType.Instant) {
				magic.action(stack, level, player, 0);
				player.getCooldowns().addCooldown(stack.getItem(), magic.cooldown());
			} else {
	            player.startUsingItem(usedHand);
				return InteractionResultHolder.consume(stack);
			}
		}
		
		return InteractionResultHolder.success(stack);
	}
	
}
