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

public class SortedDynamicListTest {
	
	private SortedDynamicList<Integer> actual;
	
	@Before
	public void init() {
		System.gc();
		actual = new SortedDynamicList<Integer>();
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
		assertArrayEquals(new Integer[] {0, 2, 2}, actual.toArray());
		try {
			actual.removeAll(Arrays.asList(1, null, 0));
		}
		catch (Throwable t) {
			fail();
		};
		assertArrayEquals(new Integer[] {2, 2}, actual.toArray());
	}
	
	// - basic tests
	
	@Test(timeout = 200)
	public void toArrayReturnsEmptyArray() {
		assertEquals(actual.toArray().length, 0);
	}
	
	@Test(timeout = 200)
	public void addKeepsElementsInCorrectOrder() {
		Integer[] elementsToAdd = new Integer[] {3, 2, 4, 1, -1, 0};
		for (Integer v : elementsToAdd) {
			actual.add(v);
			for (int i = 1; i < actual.size(); i++)
				assertTrue(actual.get(i) >= actual.get(i-1));
		}
	}
	
	@Test(timeout = 200)
	public void addContainsCheck() {
		Integer[] elementsToAdd = new Integer[] {3, 2, 4, 1, -1, 0};
		for (Integer v : elementsToAdd) {
			actual.add(v);
			assertTrue(actual.contains(v));
		}		
	}

	@Test(timeout = 200)
	public void addReturnsCorrectValue() {
		Integer[] elementsToAdd = new Integer[] {3, 2, 4, 1, -1, 0};
		for (Integer v : elementsToAdd) {
			assertTrue(actual.add(v));
		}
	}

	@Test(timeout = 200)
	public void indexOfCheck() {
		assertTrue(actual.indexOf(1) == -1);
		actual.add(1);
		assertTrue(actual.indexOf(1) ==  0);
		assertTrue(actual.indexOf(0) == -1);
		actual.add(-1);
		assertTrue(actual.indexOf(1) ==  1);
	}

	@Test(timeout = 200)
	public void addAllCheck() {
		actual.addAll(Arrays.asList(new Integer[] {-1, 3, 2, 4, 1, 5, 1, 5, 6, 0}));
		assertArrayEquals(new Integer[] {-1, 0, 1, 1, 2, 3, 4, 5, 5, 6}, actual.toArray());
	}

	@Test(timeout = 200)
	public void lastIndexOfCheck() {
		actual.add(1);
		actual.addAll(Arrays.asList(0, 1, 2, 3, 0, 3, 4));
		assertTrue(actual.lastIndexOf(1) ==  3);
		assertTrue(actual.lastIndexOf(5) == -1);
	}
	
	@Test(timeout = 200)
	public void removeCheck() {
		actual.addAll(Arrays.asList(new Integer[] {3, 2, 4, 1, 5, 1, 5, 6, 0}));
		actual.remove((Integer) 5);
		actual.remove((Integer) 1);
		actual.remove((Integer) 2);
		actual.remove((Integer) 5);
		assertArrayEquals(new Integer[] {0, 1, 3, 4, 6}, actual.toArray());
	}
	
	@Test(timeout = 200)
	public void getCheck() {
		Integer[] valuesToAdd = new Integer[] {3, 2, 4, 1, 5, 1, 5, 6, 0};
		actual.addAll(Arrays.asList(valuesToAdd));
		Arrays.sort(valuesToAdd);
		for (int i = 0; i < valuesToAdd.length; i++)
			assertEquals(valuesToAdd[i], actual.get(i));
	}
	
	@Test(timeout = 200)
	public void removeAllCheck() {
		actual.addAll(Arrays.asList(new Integer[] {3, 2, 4, 1, 5, 1, 5, 6, 0}));
		actual.removeAll(Arrays.asList(1, 2, 5, 7));
		assertArrayEquals(new Integer[] {0, 3, 4, 6}, actual.toArray());
	}

	@Test(timeout = 200)
	public void isEmptyCheck() {
		assertTrue(actual.isEmpty());
		actual.add(1);
		assertFalse(actual.isEmpty());
		actual.remove((Integer) 1);
		assertTrue(actual.isEmpty());		
	}

	@Test(timeout = 200)
	public void cloneDoesNotReturnTheSameArray() {
		actual.addAll(Arrays.asList(new Integer[] {-1000000, -10, 100, 200, 300, 500, 800, 1300, 2000, 3500, 5500}));
		assertTrue(actual != actual.clone());
	}

	@Test(timeout = 200)
	public void cloneReturnsShallowCopyOfArray() {
		actual.addAll(Arrays.asList(new Integer[] {-1000000, -10, 100, 200, 300, 500, 800, 1300, 2000, 3500, 5500}));
		@SuppressWarnings("unchecked")
		SortedList<Integer> clone = (SortedList<Integer>) actual.clone();
		assertTrue(actual.hashCode() == clone.hashCode());
		assertTrue(actual.equals(clone) && clone.equals(actual));
		Iterator<Integer> it1 = actual.iterator(), it2 = clone.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			assertTrue(it1.next() == it2.next());
		}
		assertFalse(it1.hasNext() || it2.hasNext());
	}
	
	@Test(timeout = 200)
	public void cloneReturnsObjectOfCorrectTypeWhenSubclassed() {
		assertTrue(actual.clone().getClass() == SortedDynamicList.class);
		@SuppressWarnings("serial")
		SortedDynamicList<Integer> subclassedList = new SortedDynamicList<Integer>() {};
		assertTrue(subclassedList.clone().getClass() != SortedDynamicList.class);
		assertTrue(SortedDynamicList.class.isAssignableFrom(subclassedList.clone().getClass()));
	}
	
	@Test(timeout=200)
	public void comparatorChangesSortOrderAccordingly() {
		Comparator<Number> comparator = new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				long v1 = o1.longValue(), v2 = o2.longValue();
				return (v1 < v2) ? 1 : (v1 == v2) ? 0 : -1;
			}
		};
		actual = new SortedDynamicList<Integer>(comparator);
		Integer[] itemsToCheck = new Integer[] {3, 2, 4, 1, 5, 1, 5, 6, 0, -1, -7, 9};
		actual.addAll(Arrays.asList(itemsToCheck));
		Arrays.sort(itemsToCheck, comparator);
		assertArrayEquals(actual.toArray(), itemsToCheck);
	}
	
	// - performance tests
	
	@Test(timeout = 500)
	public void performanceTestForAddingElementsInSortedOrder() {
		Integer v = 1;
		for (int i = 0; i < 2000000; i++)
			assertTrue(actual.add(v));
	}

	@Test(timeout = 500)	
	public void performanceTestForAdditionOf100kElements() {
		for (int i = 0; i < 100000; i++)
			assertTrue(actual.add(-i));
	}

	@Test(timeout = 500)
	public void performanceTestForAddingElementsInSortedOrderUsingComparator() {
		actual = new SortedDynamicList<Integer>(new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				long v1 = o1.longValue(), v2 = o2.longValue();
				return (v1 < v2) ? 1 : (v1 == v2) ? 0 : -1;
			}
		});
		Integer v = 1;
		for (int i = 0; i < 2000000; i++)
			assertTrue(actual.add(v));
	}

	@Test(timeout = 500)	
	public void performanceTestForAdditionOf100kElementsUsingComparator() {
		actual = new SortedDynamicList<Integer>(new Comparator<Number>() {
			@Override
			public int compare(Number o1, Number o2) {
				long v1 = o1.longValue(), v2 = o2.longValue();
				return (v1 < v2) ? 1 : (v1 == v2) ? 0 : -1;
			}
		});
		for (int i = 0; i < 100000; i++)
			assertTrue(actual.add(i));
	}
}
