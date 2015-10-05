package de.janosch.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import de.janosch.commons.collections.iterator.AbstractIteratorAdapter;

/**
 * 
 * Ring data structure for read-accesses.
 * 
 * TODO: Possibly provide write-access in a ring-like fashion too. <br>
 * TODO: See {@link PointsUtil#insert(Ring, Ring, int)} as an example.
 * 
 * @author Janos
 * @version 28.09.2011 | 19:42:13
 * 
 */
public class Ring<T> extends LinkedList<T> {

	private static final long serialVersionUID = 2607986679825750976L;

	protected int ringStartIndex = 0;

	/**
	 * Sets the starting point of the ring to the specified position in the ring. <br>
	 * <br>
	 * This causes the ring to start iterating at the specified index the next time
	 * {@link #iterator()} is invoked. <br>
	 * <br>
	 * Note that the new start index is "modulo'ed", so that it reaches around the ring if the value
	 * is greater than the size of the ring.
	 * 
	 * @param ringStartIndex
	 *            The ringStartIndex where the start position is set.
	 */
	public void setStart(final int index) {
		this.ringStartIndex = mod(index);
	}

	/**
	 * Adds an element at the end of this ring. <br>
	 * <br>
	 * Note that this increases the ring start index with the only exception, that the ring start
	 * index is zero.
	 */
	@Override
	public boolean add(final T e) {
		if (this.ringStartIndex == 0) {
			return super.add(e);
		}
		super.add(this.ringStartIndex, e);
		this.ringStartIndex++;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (this.ringStartIndex == 0) {
			return super.addAll(c);
		}
		boolean b = false;
		for (T t : c) {
			b |= add(t);
		}
		return b;
	}

	/**
	 * Returns the current ring start index, as set by {@link #setStart(int)} or 0 (default).
	 * 
	 * @return The current ring start index.
	 */
	public int getStart() {
		return this.ringStartIndex;
	}

	/**
	 * Returns the first element from the ring, regarding the current start position.
	 * 
	 * @return The element at the start position.
	 */
	@Override
	public T getFirst() {
		if (this.isEmpty()) {
			throw new NoSuchElementException();
		}
		return this.get(this.ringStartIndex);
	}

	/**
	 * Returns the last element from the ring, regarding the current start position.
	 * 
	 * @return The element prior to the start position, or put differently, the element that would
	 *         be returned last by the ring iterator.
	 */
	@Override
	public T getLast() {
		if (this.isEmpty()) {
			throw new NoSuchElementException();
		}
		return this.get(mod(this.size() + this.ringStartIndex - 1));
	}

	private int mod(final int index) {
		final int size = this.size();
		if (size == 0) {
			return 0;
		}
		return index % size;
	}

	@Override
	public Iterator<T> iterator() {
		return new AbstractIteratorAdapter<T>(this.ringStartIndex) {
			@Override
			protected boolean checkIndex(final int index) {
				return index >= 0 && ((index - Ring.this.ringStartIndex) < Ring.this.size());
			}

			@Override
			protected T get(final int index) {
				return Ring.this.get(mod(index));
			}

		};
	}
}
