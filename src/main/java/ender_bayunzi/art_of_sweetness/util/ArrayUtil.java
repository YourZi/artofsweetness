package ender_bayunzi.art_of_sweetness.util;

public class ArrayUtil {

	public static <T> T[] fill(T[] arr, T t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
}
