package zjava.test.collection;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import zjava.collection.DynamicList;

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
	public void iteratorNextOnEmptyListThrowsNoSuchElementException() {
		try {
			actual.iterator().next();
		}
		catch(NoSuchElementException e) {
			return;
		}
		catch (Throwable e) {}
		fail();
	}
	
	@Test(timeout = 200)
	public void ensureNullSupportForAllBasicOperations() {
		try {
			actual.add(null);
			actual.add(null);
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			actual.set(0, null);
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			actual.remove(null);
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertNull(actual.get(0));
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertTrue(actual.contains(null));
		}
		catch (NullPointerException npe) {
			fail();
		}
	}
	
	@Test(timeout = 200)
	public void ensureNullSupportForAllPositionalOperations() {
		try {
			actual.add(null);
			actual.add(null);
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertEquals(0, actual.indexOf(null));
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertEquals(1, actual.lastIndexOf(null));
		}
		catch (NullPointerException npe) {
			fail();
		}
	}

	@Test(timeout = 200)
	public void ensureNullSupportForAllBulkOperations() {
		try {
			assertTrue(actual.addAll(Arrays.asList(null, null, 1)));
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertTrue(actual.containsAll(Arrays.asList(null, 1)));
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertTrue(actual.removeAll(Arrays.asList(null, 2)));
		}
		catch (NullPointerException npe) {
			fail();
		}
		try {
			assertTrue(actual.retainAll(Arrays.asList(null, 2)));
		}
		catch (NullPointerException npe) {
			fail();
		}
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void toStringOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1));
		assertNotNull(list.toString());
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void containsOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1));
		assertTrue(list.contains(list));
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void containsAllOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1));
		assertTrue(list.containsAll(Arrays.asList(list)));
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void indexOfOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1, list));
		assertEquals(1, list.indexOf(list));
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void lastIndexOfOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1, list));
		assertEquals(3, list.lastIndexOf(list));
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cloneOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1, list));
		List clone = (List)(((DynamicList)list).clone());
		assertFalse(list == clone);
		assertTrue(list.getClass() == clone.getClass());
		assertTrue(list.equals(clone));
		clone.add(0, -1);
		assertFalse(list.equals(clone));
		assertTrue(list.get(1) == clone.get(2));
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void retainAllOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1, list));
		assertTrue(list.retainAll(Arrays.asList(list)));
		assertEquals(Arrays.asList(list, list), list);
	}

	@Test(timeout = 200)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void removeAllOnListWhichContainsInself() {
		List list = new DynamicList();
		list.addAll(Arrays.asList(0, list, 1, list));
		assertTrue(list.removeAll(Arrays.asList(list)));
		assertEquals(Arrays.asList(0, 1), list);
	}

	// - basic operations tests
	
	@Test(timeout = 200)
	public void basicAddCheck() {
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
	}
	
	@Test(timeout=200)
	public void basicToArrayCheck() {
		for (int i = 0; i < 1000; i++) {
			Integer value = i;
			expected.add(value);
			actual.add(value);
		}
		for (int i = 0; i < 1000; i++) {
			int index = 1000-i;
			Integer value = i;
			expected.add(index, value);
			actual.add(index, value);
		}
		assertArrayEquals(expected.toArray(), actual.toArray());
	}

	
	@Test(timeout=200)
	public void basicRemoveCheck() {
		for (int i = 0; i < 1000; i++) {
			Integer value = i;
			expected.add(value);
			actual.add(value);
		}
		for (int i = 0; i < 1000; i++) {
			int index = 1000-i;
			Integer value = i;
			expected.add(index, value);
			actual.add(index, value);
		}
		for (int i = 1; i < 2000; i += 3) {
			int index = 2000 - i;
			assertEquals(expected.remove(index), actual.remove(index));
		}
		assertArrayEquals(expected.toArray(), actual.toArray());
	}
	
	@Test(timeout = 200)
	public void basicAddAllCheck() {
		List<Integer> valuesToAdd = Arrays.asList(2, 3, 5, null, 7, 13, 1, 8, null);
		expected.addAll(valuesToAdd);
		actual.addAll(valuesToAdd);
		assertEquals(expected, actual);
	}

	@Test(timeout = 200)
	public void basicRemoveAllCheck() {
		List<Integer> valuesToAdd = Arrays.asList(2, 3, 5, null, 7, 13, 1, 8, null);
		expected.addAll(valuesToAdd);
		actual.addAll(valuesToAdd);
		List<Integer> valuesToRemove = Arrays.asList(2, 5, 1, null, 2);
		expected.removeAll(valuesToRemove);
		actual.removeAll(valuesToRemove);
		assertEquals(expected, actual);
	}

	@Test(timeout = 200)
	public void basicRetainAllCheck() {
		List<Integer> valuesToAdd = Arrays.asList(2, 3, 5, null, 7, 13, 1, 8, null);
		expected.addAll(valuesToAdd);
		actual.addAll(valuesToAdd);
		List<Integer> valuesToRemove = Arrays.asList(2, 5, 1, null, 2);
		expected.removeAll(valuesToRemove);
		actual.removeAll(valuesToRemove);
		assertEquals(expected, actual);
	}

	@Test(timeout = 200)
	public void complexBulkOperationsCheck() {
		List<Integer> sample0 = Arrays.asList(7, 7, 7, 7, 7, 7, 7);
		List<Integer> sample1 = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2);
		List<Integer> sample2 = Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
		List<Integer> sample3 = Arrays.asList(85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99);
		List<Integer> sample4 = new ArrayList<Integer>();
		for (int i = 100; i < 200; i++) sample4.add(i);
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
		expected.addAll(50, sample4);
		actual.addAll(50, sample4);
		assertEquals(expected, actual);
		expected.removeAll(sample4);
		actual.removeAll(sample4);
		assertEquals(expected, actual);
		expected.retainAll(sample3);
		actual.retainAll(sample3);
		assertEquals(expected, actual);
	}
	
	@Test(timeout = 200)
	public void subListCheck() {
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
	
	@Test(timeout = 1000)
	public void performanceTestInsertionsAndRemovalsAtTheBeginningAndTheMid() {
		for (int i = 0; i < 1000; i++) {
			actual.add(i);
		}
		for (int i = 0; i < 200000; i++) {
			actual.add(32 + (i & 1), i & 63);
		}
		for (int i = 0; i < 100000; i++) {
			actual.remove(100000 - i);
		}
		for (int i = 0; i < 100000; i++) {
			actual.remove(i & 511);
		}
		assertTrue(actual.size() == 1000);
	}
	
	@Test(timeout = 1000)
	public void performanceTestGetOperation() {
		for (int i = 0; i < 65536; i++) {
			actual.add(i & 63);
		}
		for (int i = 0; i < 100000000; i++) {
			actual.get(i & 65535);
			actual.get((i + 40000) & 65535);
		}
	}
	
	@Test(timeout = 1000)
	public void performanceTestAssureThatThereAreNoExcessiveMemoryThrashingDuringRemovalsAtTheEndOfBlock() {
		Integer zero = 0;
		int almostFilledBlockOnIndex = 65536-1024-1;
		for (int i = 0; i < almostFilledBlockOnIndex; i++)
			actual.add(zero);
		for (int i = 0; i < 5000000; i++) {
			actual.add(zero);
			actual.add(zero);
			actual.remove(almostFilledBlockOnIndex);
			actual.remove(almostFilledBlockOnIndex);
		}
	}
}
