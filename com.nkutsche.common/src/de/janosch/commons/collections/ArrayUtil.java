package de.janosch.commons.collections;

/**
 * 
 * @author Janos
 * @version 22.10.2011 | 19:49:34
 * 
 */
public class ArrayUtil {

	/**
	 * Find the first index of the searched value in the given array. Returns -1, if any input is
	 * <code>null</code>, of if the searched item isn't found.
	 */
	public static <T> int find(T[] array, T value) {
		if (value == null) {
			return -1;
		}
		for (int i = 0; i < array.length; i++) {
			if (value.equals(array[i])) {
				return i;
			}
		}
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] castArray(Object[] array, T[] targetArray){
		for (int i = 0; i < array.length; i++) {
			targetArray[i] = (T) array[i];
		}
		return targetArray;
	}

}
