package de.janosch.commons.collections.iterator;

/**
 * 
/**
 * Adapter class to adapt generic arrays to the {@link java.util.Iterator} interface.
 * 
 * 
 * 
 * @author Janos
 * @version 30.07.2011 | 18:19:29
 * 
 * @param <T> The generic type of the array.
 */
public class ArrayIteratorAdapter<T> extends AbstractIteratorAdapter<T> {

	protected final T[] array;

	public ArrayIteratorAdapter(final T[] array) {
		this.array = array;
	}

	@Override
	protected boolean checkIndex(final int index) {
		return index >= 0 && index < this.array.length;
	}

	@Override
	protected T get(final int index) {
		if (checkIndex(index)) {
			return this.array[index];
		}
		return null;
	}

}
