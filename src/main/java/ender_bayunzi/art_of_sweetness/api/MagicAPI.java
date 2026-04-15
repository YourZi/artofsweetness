package ender_bayunzi.art_of_sweetness.api;

import java.util.Arrays;
import java.util.List;

import ender_bayunzi.art_of_sweetness.init.ModAttachmentTypes;
import ender_bayunzi.art_of_sweetness.init.ModDataComponentTypes;
import ender_bayunzi.art_of_sweetness.init.ModMagic;
import ender_bayunzi.art_of_sweetness.item.MagicItem;
import ender_bayunzi.art_of_sweetness.magic.Magic;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MagicAPI {

	public static enum APIResult {
		PASS,
		FAIL,
		SUCESS
	}
	
	public static int getSMValue(LivingEntity living) {
		if (living == null)
			throw new NullPointerException("living is null");
		
		return living.getData(ModAttachmentTypes.SMVALUE);
	}
	
	public static APIResult setSMValue(LivingEntity living, int value) {
		if (living == null) return APIResult.FAIL;
		
		living.setData(ModAttachmentTypes.SMVALUE, value);
		return APIResult.SUCESS;
	}
	
	public static APIResult learnMagic(LivingEntity living, Magic magic) {
		if (living == null || magic == null) return APIResult.FAIL;

		List<Magic> magicList = living.getData(ModAttachmentTypes.KNOWN_MAGIC);
		if (magicList.contains(magic)) return APIResult.PASS;
		magicList.add(magic);
		living.setData(ModAttachmentTypes.KNOWN_MAGIC, magicList);
		return APIResult.SUCESS;
	}
	
	public static List<Magic> getLearnedMagic(LivingEntity living) {
		if (living == null)
			throw new NullPointerException("living is null");
		
		List<Magic> magicList = living.getData(ModAttachmentTypes.KNOWN_MAGIC);
		magicList.forEach((magic) -> {
			if (magic != null) magicList.add(magic);
		});
		return magicList;
	}
	
	public static APIResult setMagic(ItemStack stack, int slot, Magic magic) {
		if (stack == null || magic == null || (stack.getItem() instanceof MagicItem item && !item.properties.canSetSlotTo(slot, magic))) return APIResult.FAIL;
		Magic[] magicArray = MagicAPI.getMagicList(stack);
		if (slot < 0 || slot >= magicArray.length) return APIResult.FAIL;
		magicArray[slot] = magic;
		stack.set(ModDataComponentTypes.MAGICLIST, Arrays.asList(magicArray));
		return APIResult.SUCESS;
	}
	
	public static Magic getMagic(ItemStack stack, int slot, Magic magic) {
		if (stack == null || magic == null) throw new NullPointerException();
		Magic[] magicArray = MagicAPI.getMagicList(stack);
		if (slot < 0 || slot >= magicArray.length) throw new IndexOutOfBoundsException();
		return magicArray[slot];
	}
	
	public static Magic[] getMagicList(ItemStack stack) {
		if (!stack.has(ModDataComponentTypes.MAGICLIST) && stack.getItem() instanceof MagicItem magic) stack.set(ModDataComponentTypes.MAGICLIST, Arrays.asList(magic.properties.getMagicInstances()));
		return stack.get(ModDataComponentTypes.MAGICLIST).toArray(new Magic[0]);
	}
	
	public static int getMagicIndex(ItemStack stack) {
		return stack.get(ModDataComponentTypes.MAGICINDEX);
	}
	
	public static APIResult setMagicIndex(ItemStack stack, int index) {
		if (stack == null) return APIResult.FAIL;
		Magic[] magicArray = MagicAPI.getMagicList(stack);
		if (index < 0 || index >= magicArray.length) return APIResult.FAIL;
		stack.set(ModDataComponentTypes.MAGICINDEX, index);
		return APIResult.SUCESS;
	}
	
	// 向后切换时跳过空槽位
	public static int getNextEquippedMagicIndex(ItemStack stack, int currentIndex) {
		return getOffsetEquippedMagicIndex(stack, currentIndex, 1);
	}
	
	// 向前切换时跳过空槽位
	public static int getPreviousEquippedMagicIndex(ItemStack stack, int currentIndex) {
		return getOffsetEquippedMagicIndex(stack, currentIndex, -1);
	}
	
	private static int getOffsetEquippedMagicIndex(ItemStack stack, int currentIndex, int direction) {
		Magic[] magicArray = MagicAPI.getMagicList(stack);
		if (magicArray.length == 0) return -1;
		
		int normalizedIndex = currentIndex;
		if (normalizedIndex < 0 || normalizedIndex >= magicArray.length) normalizedIndex = 0;
		
		for (int step = 1; step <= magicArray.length; step++) {
			int index = Math.floorMod(normalizedIndex + step * direction, magicArray.length);
			if (magicArray[index] != ModMagic.empty) return index;
		}
		
		return magicArray[normalizedIndex] != ModMagic.empty ? normalizedIndex : -1;
	}
	
	public static Magic getCurrentMagic(LivingEntity living, ItemStack stack) {
		return MagicAPI.getMagicList(stack)[MagicAPI.getMagicIndex(stack)];
	}
	
}
