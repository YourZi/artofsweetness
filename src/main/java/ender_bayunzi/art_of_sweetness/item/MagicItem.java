package ender_bayunzi.art_of_sweetness.item;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
import net.minecraft.world.entity.EquipmentSlot;
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
    
    public final MagicItemProperties properties;
    
	public MagicItem(int slots, float power, float cooldownRate, MagicAspect[] goodAt, MagicAspect[] notGoodAt) {
		this(new MagicItemProperties(slots, ModMagic.EMPTY, goodAt, notGoodAt).createAttributes(power, cooldownRate));
	}
	
	public MagicItem(MagicItemProperties properties) {
		super(properties);
		this.properties = properties;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!stack.has(ModDataComponentTypes.MAGICLIST))
			stack.set(ModDataComponentTypes.MAGICLIST, Arrays.asList(this.properties.getMagicInstances()));
		if (!stack.has(ModDataComponentTypes.MAGICINDEX))
			stack.set(ModDataComponentTypes.MAGICINDEX, 0);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.slots", ChatFormatting.YELLOW + String.valueOf(this.properties.slots)));
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.goodAt").append(MagicAspect.toComponent(this.properties.goodAt)));
		tooltipComponents.add(Component.translatable("art_of_sweetness.item.notGoodAt").append(MagicAspect.toComponent(this.properties.notGoodAt)));
		
		if (tooltipFlag.hasShiftDown()) {
			tooltipComponents.add(Component.literal(""));
			int index = MagicAPI.getMagicIndex(stack);
			
			Magic[] list = MagicAPI.getMagicList(stack);
			for (int i = 0; i < this.properties.slots; i++) {
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
		
	}
	
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
		Magic magic = MagicAPI.getCurrentMagic(livingEntity, stack);
		if (magic != null) {
			if (magic.type() == MagicType.Channeled) {
				int useTime = this.getUseDuration(stack, livingEntity) - timeCharged;
				magic.action(stack, level, livingEntity, useTime);
				if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), (int) (magic.cooldown() * this.properties.cooldrate));
			} else if (magic.type() == MagicType.Sustained)
				if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), (int) (magic.cooldown() * this.properties.cooldrate));
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
				player.getCooldowns().addCooldown(stack.getItem(), (int) (magic.cooldown() * this.properties.cooldrate));
			} else {
	            player.startUsingItem(usedHand);
				return InteractionResultHolder.consume(stack);
			}
		}
		
		return InteractionResultHolder.success(stack);
	}
	
	//此处禁止注册ModDataComponentTypes中的MAGICLIST！Magic还没注册
	public static class MagicItemProperties extends Properties {
		
		public float power = 0;
		public float cooldrate = 1;
		private final Supplier<Magic>[] defaultList;
		
		public final int slots;
		public final boolean[] locked;
		public final MagicAspect[] goodAt;
		public final MagicAspect[] notGoodAt;
		public final List<MagicAspect> goodAtList;
		public final List<MagicAspect> notGoodAtList;
		
		private Magic[] magicInstances;
		
		@SuppressWarnings("unchecked")
		public MagicItemProperties(int slots, Supplier<Magic> defaultMagic, MagicAspect[] goodAt, MagicAspect[] notGoodAt) {
			if (slots > 7) throw new RuntimeException("slots > 7");
			this.slots = slots;
			this.locked = ArrayUtil.fill(new boolean[slots], false);
			this.defaultList = ArrayUtil.fill(new Supplier[slots], defaultMagic);
			this.goodAt = goodAt;
			this.notGoodAt = notGoodAt;
			this.goodAtList = Arrays.asList(this.goodAt);
			this.notGoodAtList = Arrays.asList(this.notGoodAt);
		}
		
		public MagicItemProperties setSlotMagic(int index, Supplier<Magic> magic, boolean lock) {
			if (this.magicInstances != null) throw new RuntimeException(this + " was locked");
			this.defaultList[index] = magic;
			this.locked[index] = lock;
			return this;
		}
		
		public MagicItemProperties lock(int index) {
			if (this.magicInstances != null) throw new RuntimeException(this + " was locked");
			this.locked[index] = true;
			return this;
		}
		
		public MagicItemProperties unlock(int index) {
			if (this.magicInstances != null) throw new RuntimeException(this + " was locked");
			this.locked[index] = false;
			return this;
		}
		
		public Magic[] getMagicInstances() {
			if (this.magicInstances == null) {
				Magic[] instance = new Magic[this.defaultList.length];
				for (int i = 0; i < instance.length; i++) instance[i] = this.defaultList[i].get();
				this.magicInstances = instance;
			}
			return this.magicInstances.clone();
		}
		
		@Override
		public Properties attributes(ItemAttributeModifiers attributes) {
			attributes.forEach(EquipmentSlot.MAINHAND, (var1, var2) -> {
				if (var1 == ModAttributes.COOLDOWN_RATE)
					this.cooldrate += var2.amount();
				if (var1 == ModAttributes.POWER)
					this.power += var2.amount();
			});
			
			return super.attributes(attributes);
		}
		
		public MagicItemProperties createAttributes(float power, float cooldownRate) {
			this.power += power;
			this.cooldrate += cooldownRate;
			
			super.attributes(ItemAttributeModifiers.builder()
					.add(ModAttributes.POWER, new AttributeModifier(BASE_POWER_ID, power, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.add(ModAttributes.COOLDOWN_RATE, new AttributeModifier(BASE_COOLDOWN_RATE_ID, cooldownRate, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.build());
			
			return this;
		}
		
		public boolean goodAt(Magic magic) {
			MagicAspect[] aspects = magic.aspects();
			for (int i = 0; i < aspects.length; i++)
				if (this.goodAtList.contains(aspects[i]))
					return true;
			
			return false;
		}
		
		public boolean notGoodAt(Magic magic) {
			MagicAspect[] aspects = magic.aspects();
			for (int i = 0; i < aspects.length; i++)
				if (this.goodAtList.contains(aspects[i]))
					return true;
			
			return false;
		}
		
		public boolean canSetSlotTo(int slot, Magic magic) {
			return slot >= 0 && slot < this.slots && !this.locked[slot] && (!this.notGoodAt(magic) || this.goodAt(magic));
		}
		
	}
	
}
