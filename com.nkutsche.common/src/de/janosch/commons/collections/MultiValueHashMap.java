package de.janosch.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MultiValueHashMap<K, V> implements Map<K, V> {
	private final HashMap<K, ArrayList<V>> map;
	public MultiValueHashMap(){
		this(false);
	}
	public MultiValueHashMap(boolean linked){
		if(linked){
			map = new LinkedHashMap<K, ArrayList<V>>();
		} else {
			map = new HashMap<K, ArrayList<V>>();
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.values().contains(value);
	}

	@Override
	@Deprecated
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}

	@Override
	public V get(Object key) {
		if(!map.containsKey(key)){
			return null;
		}
		return map.get(key).get(0);
	}
	
	public ArrayList<V> getAll(Object key) {
		if(!map.containsKey(key)){
			return null;
		}
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V put(K key, V value) {
		if(!map.containsKey(key)){
			map.put(key, new ArrayList<V>());
		}
		map.get(key).add(value);
		return null;
	}
	public void putAll(ArrayList<K> keys, V value){
		for (K key : keys) {
			this.put(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (K key : map.keySet()) {
			this.put(key, map.get(key));
		}
	}

	@Override
	public V remove(Object key) {
		if(!map.containsKey(key)){
			return null;
		}
		return map.remove(key).get(0);
	}
	
	public void removeAll(Collection<K> keys){
		for (Object key : keys) {
			remove(key);
		}
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		Collection<ArrayList<V>> lists = map.values();
		ArrayList<V> values = new ArrayList<V>();
		for (ArrayList<V> v : lists) {
			values.addAll(v);
		}
		return values;
	}
	public Collection<ArrayList<V>> groupedValues(){
		return map.values();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return map.toString();
	}

}
