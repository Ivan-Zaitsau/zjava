package zjava.collection.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import zjava.collection.HugeArray;

public class HugeArrayTest {

	HugeArray<Integer> actual;
	
	@Before
	public void init() {
		System.gc();
	}
	
	// - edge cases
	
	@Test(timeout=200)
	public void outOfBoundsTest01() {
		actual = new HugeArray<>(8000000001L);
		assertNull(actual.get(8000000000L));
		int exceptionsCount = 0;
		try {
			actual.get(8000000001L);
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
	
	// - basic operations tests
	
	@Test(timeout=200)
	public void getsetTest01() {
		actual = new HugeArray<>(8000000001L);
		actual.set(8000000000L, 1);
		assertEquals(actual.get(8000000000L), (Integer) 1);
		actual.set(2049854082, 1489230);
		assertEquals(actual.get(2049854082), (Integer) 1489230);
		actual.set(0, 1489230);
		assertEquals(actual.get(0), actual.get(0));
		for (long i = 1; i < 10000000; i++) {
			assertNull(actual.get(i));
		}
	}
}
