package zjava.test.collection;

import static org.junit.Assert.*;

import java.util.*;
import org.junit.*;
import zjava.collection.Collectionz;
import static zjava.collection.Collectionz.*;

public class CollectionzTest {

	@Test(timeout = 200)
	public void toStringWorksOnNestedIterablesAndArrays() {
		List<Object> root = new ArrayList<Object>();
		root.add(root);
		root.add(new Object[] {root});
		root.add(Arrays.asList(new Object[][]{new Object[] {root}}));
		Collectionz.toString(root);
	}

	@Test(timeout = 200)
	public void toStringWorksForNull() {
		assertEquals(Collectionz.toString(null), "null");
	}

	@Test(timeout = 200)
	public void toStringWorksOnIterablesAndArraysWithNullValues() {
		List<Object> root = new ArrayList<Object>();
		root.add(null);
		root.add(new Object[] {null, 3});
		root.add(Arrays.asList(new Object[][]{new Object[] {null, 1, 2}}));
		Collectionz.toString(root);
	}

	@Test(timeout = 200)
	public void toStringWorksEfficientlyOnLargeIterables() {
		final int size = 1000000;
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
			list.add(0);
		Collectionz.toString(list);
	}

	@Test(timeout = 200)
	public void containsNullCheck() {
		Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
		hashtable.put(1, 1);
		hashtable.put(2, 2);
		assertFalse(containsNull(hashtable.keySet()));
		Set<Long> set = new HashSet<Long>();
		set.add(1L);
		set.add(2L);
		assertFalse(containsNull(set));
		set.add(null);
		assertTrue(containsNull(set));
	}
}
