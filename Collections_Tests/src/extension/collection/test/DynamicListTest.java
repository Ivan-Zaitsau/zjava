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
	
	@Test//(timeout = 200)
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
			actual.addAll(Arrays.asList(1, null, 1));
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
			actual.retainAll(Arrays.asList((Integer)null));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		// - 7
		try {
			actual.removeAll(Arrays.asList((Integer)null));
		}
		catch (NullPointerException npe) {
			exceptionsCount++;
		}
		assertTrue(actual.isEmpty());
		assertTrue(exceptionsCount == 0 || exceptionsCount == 7);
	}

	// - basic operations tests
	
	@Test//(timeout = 200)
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
		List<Integer> sample = Arrays.asList(2, 3, 5, null, 7, 13, 1, 8, null);
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
	
	@Test//(timeout = 200)
	public void addRemoveTest03() {
		List<Integer> sample0 = Arrays.asList(7, 7, 7, 7, 7, 7, 7);
		List<Integer> sample1 = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2);
		List<Integer> sample2 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
		List<Integer> sample3 = Arrays.asList(85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99);
		expected.add(null);
		actual.add(null);
		assertEquals(expected, actual);
		expected.addAll(1, sample0);
		actual.addAll(1, sample0);
		assertEquals(expected, actual);
		expected.addAll(0, sample1);
		actual.addAll(0, sample1);
		assertEquals(expected, actual);
		expected.remove(15);
		actual.remove(15);
		assertEquals(expected, actual);
		expected.addAll(14, sample2);
		actual.addAll(14, sample2);	
		assertEquals(expected, actual);
		expected.addAll(4, sample3);
		actual.addAll(4, sample3);			
		assertEquals(expected, actual);
		expected.addAll(8, sample3);
		actual.addAll(8, sample3);			
		assertEquals(expected, actual);
		expected.addAll(16, sample3);
		actual.addAll(16, sample3);			
		assertEquals(expected, actual);
		expected.addAll(32, sample3);
		actual.addAll(32, sample3);
		assertEquals(expected, actual);
		expected.addAll(64, sample3);
		actual.addAll(64, sample3);
		assertEquals(expected, actual);
		expected.addAll(65, sample3);
		actual.addAll(65, sample3);
		assertEquals(expected, actual);
	}
	
	@Test//(timeout = 200)
	public void sublistTest01() {
		List<Integer> sample0 = Arrays.asList(7, 7, 7, 7, 7, 7, 7);
		List<Integer> sample1 = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2);
		List<Integer> sample2 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
		List<Integer> sample3 = Arrays.asList(85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99);
		
		expected.subList(0, 0).add(null);
		actual.subList(0, 0).add(null);
		assertEquals(expected, actual);
		expected.subList(0, 0).addAll(sample0);
		actual.subList(0, 0).addAll(sample0);
		assertEquals(expected, actual);
		expected.addAll(sample1.subList(1, 7));
		actual.addAll(sample1.subList(1, 7));
		assertEquals(expected, actual);
		expected.addAll(sample1.subList(1, 3));
		actual.addAll(sample1.subList(1, 3));
		assertEquals(expected, actual);
		expected.subList(15, 16).remove(0);
		actual.subList(15, 16).remove(0);
		assertEquals(expected, actual);
		expected.subList(0, 14).addAll(0, sample2);
		actual.subList(0, 14).addAll(0, sample2);
		assertEquals(expected, actual);
		expected.addAll(8, expected.subList(3, 8));
		actual.addAll(8, actual.subList(3, 8));
		assertEquals(expected, actual);
		expected.addAll(16, sample3.subList(0, 15));
		actual.addAll(16, sample3.subList(0, 15));
		assertEquals(expected, actual);
		expected.addAll(32, sample3.subList(0, 15));
		actual.addAll(32, sample3.subList(0, 15));
		assertEquals(expected, actual);
		expected.addAll(65, sample3.subList(0, 15));
		actual.addAll(65, sample3.subList(0, 15));
		assertEquals(expected, actual);
		expected.subList(0, 66).clear();
		actual.subList(0, 66).clear();
		assertEquals(expected, actual);

		assertEquals(actual, actual.subList(0, expected.size()));
		assertTrue(actual.subList(5, 5).isEmpty());
	}
	
	// - performance tests
	
	@Test(timeout = 1500)
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
