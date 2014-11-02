package extension.collection.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import extension.collection.DynamicList;

public class DynamicListTest {
	
	private List<Integer> actual;
	private List<Integer> expected;
	
	@Before
	public void init() {
		System.gc();
		expected = new ArrayList<Integer>();
		actual = new DynamicList<Integer>();
	}
	
	// - edge cases
	
	@Test(timeout = 200)
	public void iteratorTest01() {
		try {
			actual.iterator().next();
		}
		catch(NoSuchElementException e) {
			return;
		}
		catch (Throwable e) {}
		assertTrue(false);
	}
	
	@Test(timeout = 200)
	public void nullTest01() {
		int exceptionsCount = 0;
		// - 1
		try {
			actual.add(null);
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 2
		try {
			actual.addAll(Arrays.asList(new Integer[] {1, null, 1}));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 3
		try {
			assertTrue(actual.contains(null));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 4
		try {
			assertEquals(actual.indexOf(null), 0);
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 5
		try {
			assertEquals(actual.lastIndexOf(null), 2);
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 6
		try {
			actual.retainAll(Arrays.asList(new Integer[] {null}));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 7
		try {
			actual.removeAll(Arrays.asList(new Integer[] {null}));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		assertTrue(actual.isEmpty());
		assertTrue(exceptionsCount == 0 || exceptionsCount == 7);
	}

	// - basic operations tests
	
	@Test(timeout = 200)
	public void addRemoveTest01() {
		for (int i = 0; i < 1000; i++) {
			Integer value = i;
			expected.add(value);
			actual.add(value);
		}
		assertEquals(expected, actual);
		for (int i = 0; i < 1000; i++) {
			int index = 1000-i;
			Integer value = i;
			expected.add(index, value);
			actual.add(index, value);
		}
		assertEquals(expected, actual);
		for (int i = 0; i < 2000; i += 12) {
			Integer value = i ^ (i >> 1);
			expected.add(i, value);
			actual.add(i, value);
			assertEquals(expected, actual);
		}
		for (int i = 0; i < 2000; i += 3) {
			int index = 2000 - i;
			assertEquals(expected.remove(index), actual.remove(index));
		}
		assertArrayEquals(expected.toArray(), actual.toArray());
		for (int i = 0, l = expected.size(); i < l; i++) {
			assertEquals(expected.remove(0), actual.remove(0));
		}
		assertTrue(actual.isEmpty());
	}
	
	@Test(timeout = 200)
	public void addRemoveTest02() {
		List<Integer> sample = Arrays.asList(new Integer[] {2, 3, 5, null, 7, 13, 1, 8, null});
		expected.addAll(sample);
		actual.addAll(sample);
		assertEquals(expected, actual);
		expected.remove(1);
		actual.remove(1);
		assertEquals(expected, actual);
		expected.add(1);
		actual.add(1);
		assertEquals(expected, actual);
		expected.addAll(sample);
		actual.addAll(sample);
		assertEquals(expected, actual);
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}
	
	// - performance tests
	
	@Test(timeout = 1000)
	public void performanceTest01() {
		for (int i = 0; i < 1000; i++) {
			actual.add(i);
		}
		for (int i = 0; i < 500000; i++) {
			actual.add(32 + (i & 1), i & 63);
		}
		for (int i = 0; i < 250000; i++) {
			actual.remove(250000 - i);
		}
		for (int i = 0; i < 250000; i++) {
			actual.remove(i & 511);
		}
		assertTrue(actual.size() == 1000);
	}
	
	@Test(timeout = 1000)
	public void performanceTest02() {
		for (int i = 0; i < 65536; i++) {
			actual.add(i & 63);
		}
		for (int i = 0; i < 100000000; i++) {
			actual.get(i & 65535);
			actual.get((i + 40000) & 65535);
		}
	}
}
