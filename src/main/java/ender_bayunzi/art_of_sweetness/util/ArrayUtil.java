package ender_bayunzi.art_of_sweetness.util;

public class ArrayUtil {

	public static <T> T[] fill(T[] arr, T t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static boolean[] fill(boolean[] arr, boolean t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static int[] fill(int[] arr, int t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static long[] fill(long[] arr, long t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static byte[] fill(byte[] arr, byte t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static char[] fill(char[] arr, char t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static short[] fill(short[] arr, short t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static float[] fill(float[] arr, float t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static double[] fill(double[] arr, double t) {
		for (int i = 0; i < arr.length; i++) arr[i] = t;
		return arr;
	}
	
	public static <T> T[] handle(T[] arr, Handler<T> handler) {
		for (int i = 0; i < arr.length; i++) arr[i] = handler.handle(arr[i]);
		return arr;
	}
	
	@FunctionalInterface
	public static interface Handler<T> {
		T handle(T t);
	}
	
}
