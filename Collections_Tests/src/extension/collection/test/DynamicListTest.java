package extension.collection.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import extension.collection.DynamicList;

public class DynamicListTest {

	List<Integer> expected;
	List<Integer> actual;
	
	@Before
	public void init() {
		System.gc();
		expected = new ArrayList<Integer>();
		actual = new DynamicList<Integer>();
	}
	
	// - basic operations tests
	
	@Test
	public void addRemoveTest01() {
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
	
	@Test
	public void addRemoveTest02() {
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
	
	// - performance tests
	
	@Test(timeout = 1000)
	public void performanceTest01() {
		for (int i = 0; i < 1000; i++) {
			actual.add(i);
		}
		for (int i = 0; i < 200000; i++) {
			int index = i / 2;
			actual.add(index, i);
		}
		for (int i = 0; i < 100000; i++) {
			int index = 100000-i;
			actual.remove(index);
		}
		for (int i = 0; i < 100000; i++) {
			int index = 720;
			actual.remove(index);
		}
		assertTrue(actual.size() == 1000);
	}
	
	@Test(timeout = 300)
	public void performanceTest02() {
		for (int i = 0; i < 65536; i++) {
			actual.add(i);
		}
		for (int i = 0; i < 100000000; i++) {
			actual.get(i & 65535);
		}
	}
}
