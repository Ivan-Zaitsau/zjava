package zjava.test.collection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import zjava.collection.HugeArray;
import zjava.collection.LazyArray;

public class LazyArrayTest {

	HugeArray<Integer> actual;
	
	@Before
	public void init() {
		System.gc();
	}
	
	// - edge cases
	
	@Test(timeout=200)
	public void outOfBounds01() {
		actual = new LazyArray<Integer>(2500000001L);
		assertNull(actual.get(2500000000L));
		int exceptionsCount = 0;
		try {
			actual.get(2500000001L);
		}
		catch(IndexOutOfBoundsException e) {
			exceptionsCount++;
		}
		catch (Throwable t) {
			fail();
		}
		assertTrue(exceptionsCount == 1);
		assertNull(actual.get(0));
		try {
			actual.get(-1);
		}
		catch(IndexOutOfBoundsException e) {
			exceptionsCount++;
		}
		catch (Throwable t) {
			fail();
		}
		assertTrue(exceptionsCount == 2);
		assertNotNull(actual.toString());
	}
	
	@Test(timeout=200)
	public void outOfBounds02() {
		actual = new LazyArray<Integer>(0);
		boolean catched;
		catched = false;
		try {
			actual.get(0);
		}
		catch(IndexOutOfBoundsException e) {
			catched = true;
		}
		catch (Throwable t) {
			fail();
		}		
		assertTrue(catched);
		catched = false;
		try {
			actual.get(1);
		}
		catch(IndexOutOfBoundsException e) {
			catched = true;
		}
		catch (Throwable t) {
			fail();
		}		
		assertTrue(catched);
	}
	
	// - basic operations tests
	
	@Test(timeout=200)
	public void getsetTest01() {
		actual = new LazyArray<Integer>(2500000001L);
		actual.set(2500000000L, 1);
		assertEquals(actual.get(2500000000L), (Integer) 1);
		actual.set(2049854082, 1489230);
		assertEquals(actual.get(2049854082), (Integer) 1489230);
		actual.set(0, 1489230);
		assertEquals(actual.get(0), actual.get(0));
		for (long i = 1; i < 10000000; i++) {
			assertNull(actual.get(i));
		}
	}
}
