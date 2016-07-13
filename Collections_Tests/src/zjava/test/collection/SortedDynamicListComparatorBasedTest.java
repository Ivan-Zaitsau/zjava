package zjava.test.collection;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import zjava.collection.SortedDynamicList;
import zjava.collection.SortedList;

public class SortedDynamicListComparatorBasedTest {

	private SortedList<Integer> actual;
	
	@Before
	public void init() {
		System.gc();
		actual = new SortedDynamicList<Integer>(new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				long v1 = o1.longValue(), v2 = o2.longValue();
				return (v1 < v2) ? 1 : (v1 == v2) ? 0 : -1;
			}
		});
	}
	
	// - edge cases
	
	@Test(timeout = 200)
	public void iteratorAtEndThrowsNoSuchElementException() {
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
	public void addingNullThrowsNullPointerException() {
		try {
			actual.add(null);
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Throwable e) {}
		fail();
	}
	
	@Test(timeout = 200)
	public void containsNullThrowsNullPointerException() {
		try {
			actual.contains(null);
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Throwable e) {}
		fail();
	}
	
	@Test(timeout = 200)
	public void removeNullThrowsNullPointerException() {
		try {
			actual.remove(null);
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Throwable e) {}
		fail();
	}

	@Test(timeout = 200)
	public void addAllThrowsNullPointerExceptionIfSpecifiedCollectionContainsNull() {
		try {
			actual.addAll(Arrays.asList(1, null, 0));
		}
		catch (NullPointerException npe) {
			return;
		}
		catch (Throwable e) {}
		fail();
	}

	@Test(timeout = 200)
	public void retainAllremoveAllDoNotThrowExceptionEvenIfSpecifiedCollectionContainsNull() {
		actual.addAll(Arrays.asList(2, 0, 1, 2));
		try {
			actual.retainAll(Arrays.asList(0, null, 2));
		}
		catch (Throwable t) {
			fail();
		};
		assertArrayEquals(new Integer[] {2, 2, 0}, actual.toArray());
		try {
			actual.removeAll(Arrays.asList(1, null, 0));
		}
		catch (Throwable t) {
			fail();
		};
		assertArrayEquals(new Integer[] {2, 2}, actual.toArray());
	}

	// - basic operations tests
	
	@Test(timeout = 200)
	public void generalPurposeTest01() {
		assertTrue(actual.indexOf(1) == -1);
		actual.add(1);
		assertTrue(actual.indexOf(1) ==  0);
		assertTrue(actual.indexOf(0) == -1);
		assertTrue(actual.iterator().hasNext());
		actual.addAll(Arrays.asList(0, 1, 2, 3, 0, 3, 4));
		assertTrue(actual.indexOf(1) == 4);
		assertTrue(actual.lastIndexOf(1) ==  5);
		assertTrue(actual.lastIndexOf(5) == -1);
		assertTrue(actual.remove((Integer) 3));
		assertFalse(actual.remove((Integer) 5));
		actual.removeAll(Arrays.asList(0));
		assertFalse(actual.remove((Integer) 0));
		actual.retainAll(Arrays.asList(1, 3, 4));
		assertTrue(actual.get(2) == 1);
		assertTrue(actual.get(3) == 1);
		assertArrayEquals(actual.toArray(), new Integer[] {4, 3, 1, 1});
		assertTrue(actual.contains(1));
		assertTrue(actual.contains(3));
		assertTrue(actual.contains(4));
		actual.clear();
		assertTrue(actual.size() == 0);
		assertTrue(actual.isEmpty());
		assertFalse(actual.iterator().hasNext());
	}
	
	@Test(timeout = 200)
	public void addRemoveGetContainsTest01() {
		actual.addAll(Arrays.asList(new Integer[] {3, 2, 4, 1, 5, 1, 5, 6, 0}));
		assertArrayEquals(actual.toArray(), new Integer[] {6, 5, 5, 4, 3, 2, 1, 1, 0});
		assertTrue(actual.get(2) == 5);
		assertTrue(actual.contains(6));
		assertTrue(actual.remove((Integer) 5));
		assertTrue(actual.contains(5));
		assertTrue(actual.removeAll(Arrays.asList(1)));
		assertFalse(actual.contains(1));
	}

	@Test(timeout = 200)
	public void cloneTest01() {
		actual.addAll(Arrays.asList(new Integer[] {100, 200, 300, 500, 800, 1300, 2000, 3500, 5500}));
		@SuppressWarnings("unchecked")
		SortedList<Integer> clone = (SortedList<Integer>) ((SortedDynamicList<?>) actual).clone();
		assertTrue(actual.hashCode() == clone.hashCode());
		assertTrue(actual.equals(clone) && clone.equals(actual));
		Iterator<Integer> it1 = actual.iterator(), it2 = clone.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			assertTrue(it1.next() == it2.next());
		}
		assertFalse(it1.hasNext() || it2.hasNext());
	}
	
	// - performance tests
	
	@Test(timeout = 500)
	public void performanceCheckForAddingElementsInSortedOrder() {
		Integer v = 1;
		for (int i = 0; i < 2000000; i++)
			assertTrue(actual.add(v));
	}

	@Test(timeout = 500)
	public void performanceCheckForAdditionOf100kElements() {
		for (int i = 0; i < 100000; i++)
			assertTrue(actual.add(i));
	}
}
