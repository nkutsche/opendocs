package de.janosch.commons.collections;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

/**
 * 
 * @author Janos
 * @version 28.09.2011 | 19:44:34
 * 
 */
public class RingTest {
	
	@Test
	public void setStartTest() throws Exception {

		final Ring<Integer> ring = new Ring<Integer>();

		assertTrue(ring.isEmpty());
		
		ring.setStart(23);
		
		assertEquals(0, ring.getStart());
	}
	
	@Test
	public void addTest() throws Exception {

		final Ring<Integer> ring = new Ring<Integer>();

		assertTrue(ring.isEmpty());

		ring.add(0);
		ring.add(11);
		ring.add(22);
		ring.add(33);
		ring.add(44);
		ring.add(55);
		ring.add(66);
		
		assertEquals(7, ring.size());
		
		assertEquals(44, (int) ring.get(4));
		
		ring.setStart(3);
		assertEquals(3, ring.getStart());
		assertEquals(33, (int) ring.getFirst());
		assertEquals(22, (int) ring.getLast());
		
		ring.add(225);
		assertEquals(8, ring.size());
		assertEquals(4, ring.getStart());
		
		assertEquals(0, (int) ring.get(0));
		assertEquals(11, (int) ring.get(1));
		assertEquals(22, (int) ring.get(2));
		assertEquals(225, (int) ring.get(3));
		assertEquals(33, (int) ring.get(4));
		assertEquals(44, (int) ring.get(5));
		assertEquals(55, (int) ring.get(6));
		assertEquals(66, (int) ring.get(7));
	}

	@Test
	public void test() throws Exception {

		final Ring<Integer> ring = new Ring<Integer>();

		assertTrue(ring.isEmpty());

		ring.add(0);
		ring.add(1);
		ring.add(2);
		ring.add(3);
		ring.add(4);
		ring.add(5);
		ring.add(6);

		assertFalse(ring.isEmpty());
		assertEquals(7, ring.size());

		assertEquals(0, (int) ring.getFirst());
		assertEquals(6, (int) ring.getLast());
		
		Iterator<Integer> iterator;

		int k = 0;
		iterator = ring.iterator();
		while (iterator.hasNext()) {
			final int i = iterator.next();
			assertEquals(k, i);
			k++;
		}

		ring.setStart(2);
		iterator = ring.iterator();
		assertEquals(2, (int) iterator.next());
		assertEquals(3, (int) iterator.next());
		assertEquals(4, (int) iterator.next());
		assertEquals(5, (int) iterator.next());
		assertEquals(6, (int) iterator.next());
		assertEquals(0, (int) iterator.next());
		assertEquals(1, (int) iterator.next());
		assertFalse(iterator.hasNext());
		
		assertEquals(2, (int) ring.getFirst());
		assertEquals(1, (int) ring.getLast());

		for (int i = 0; i < ring.size(); i++) {
			ring.setStart(i);
			iterator = ring.iterator();
			int k2 = 0;
			while (iterator.hasNext()) {
				final int value = iterator.next();
				assertEquals((k2 + i) % ring.size(), value);
				k2++;
			}
		}

	}

	@Test
	public void randomTest() throws Exception {
		
		final Random r = new Random();
		final Ring<Integer> ring = new Ring<Integer>();
		
		// Fill the ring with a random amount of random numbers
		final int size = r.nextInt(100);
		for (int i = 0; i < size; i++) {
			ring.add(r.nextInt(100000));
		}
		
		for (int i = 0; i < ring.size(); i++) {
			// Choose a random ring-start index
			final int start = r.nextInt(ring.size());
			ring.setStart(start);
			
			final Iterator<Integer> iterator = ring.iterator();
			int k2 = 0;
			while (iterator.hasNext()) {
				final Integer value = iterator.next();
				assertEquals(ring.get((k2 + start) % ring.size()), value);
				k2++;
			}
		}
		
	}

}
