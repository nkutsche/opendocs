package de.janosch.commons.collections.iterator;

import java.util.Iterator;

/**
 * Abstract iterator, that can be used to adapt data sets to the {@link java.util.Iterator}
 * interface. <br>
 * <br>
 * To adapt any data set to the {@link Iterator} interface, an implementing class has to be defined
 * that provides ringStartIndex-oriented access to the sets elements.
 * 
 * @author Janos
 * @version 30.07.2011 | 16:42:42
 * 
 * @param <T> The type of the data set elements.
 * 
 */
public abstract class AbstractIteratorAdapter<T> implements Iterator<T> {

	protected int nextIndex = 0;

	protected AbstractIteratorAdapter() {
	}
	
	protected AbstractIteratorAdapter(final int startIndex) {
		this.nextIndex = startIndex;
	}
	
	/**
	 * Checks that the given ringStartIndex is valid in the iteratable data set.
	 */
	protected abstract boolean checkIndex(int index);

	/**
	 * Accesses the i'th element in the underlaying data set. <br>
	 * <br>
	 * Implementors should make sure, that the ringStartIndex is valid by invoking {@link #checkIndex(int)}.
	 */
	protected abstract T get(int i);

	@Override
	public boolean hasNext() {
		return checkIndex(nextIndex);
	}

	@Override
	public T next() {
		final T next = get(nextIndex);
		nextIndex++;
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
