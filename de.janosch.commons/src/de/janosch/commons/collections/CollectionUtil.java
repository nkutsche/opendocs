package de.janosch.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Janos
 * @version 23.09.2011 | 22:36:30
 * 
 */
public class CollectionUtil {

	/**
	 * 
	 /** Get or create the element in the map.
	 * 
	 * If the element to be returned is <code>null</code>, a new element is created using the
	 * provided {@link Creator} and put into the map.
	 */
	public static <K, V> V getOrNew(final Map<K, V> map, final K key, final Creator<V> creator) {
		V value = map.get(key);
		if (value == null) {
			value = creator.create();
			map.put(key, value);
		}
		return value;
	}

	public static interface Creator<V> {
		abstract public V create();
	}

	public static <T> List<T> toList(final Iterable<T> iterable) {
		final List<T> list = new ArrayList<T>();
		for (final T t : iterable) {
			list.add(t);
		}
		return list;
	}

	public static interface DoForEach<T> {
		public void action(final T e);
	}

	public static <T> void foreach(final Collection<T> collection, final DoForEach<T> action) {
		if (collection == null || action == null) {
			return;
		}
		for (final T element : collection) {
			action.action(element);
		}
	}

}
