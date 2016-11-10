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
	
	@Test(timeout=500)
	public void initializationOfArrayOfHugeSizeDoesNotFail() {
		actual = new LazyArray<Integer>(100000000000L);
	}
	
	@Test(timeout=200)
	public void getDoesNotTriggerUnnecesseryInitialization() {
		actual = new LazyArray<Integer>(4000000000L);
		for (long i = 123; i < actual.size(); i += 4096)
			actual.get(i);
	}

	@Test(timeout=200)
	public void outOfBoundsThrownOnArrayIndexOutsideOfBoundaries() {
		actual = new LazyArray<Integer>(2500000001L);
		try {
			actual.get(2500000001L);
		}
		catch(IndexOutOfBoundsException e) {
		}
		catch (Throwable t) {
			fail();
		}
		assertNull(actual.get(0));
		try {
			actual.get(-1);
		}
		catch(IndexOutOfBoundsException e) {
		}
		catch (Throwable t) {
			fail();
		}
	}
	
	@Test(timeout=200)
	public void outOfBoundsThrownForAnyIndexForArrayOfSizeZero() {
		actual = new LazyArray<Integer>(0);
		try {
			actual.get(0);
		}
		catch(IndexOutOfBoundsException e) {
		}
		catch (Throwable t) {
			fail();
		}		
		try {
			actual.get(1);
		}
		catch(IndexOutOfBoundsException e) {
		}
		catch (Throwable t) {
			fail();
		}		
	}
	
	@Test(timeout=200)
	public void toStringDoesNotReturnNull() {
		actual = new LazyArray<Integer>(0);
		assertNotNull(actual.toString());
		actual = new LazyArray<Integer>(5000);
		actual.set(1, 1);
		assertNotNull(actual.toString());
	}
	
	// - basic operations tests
		
	@Test(timeout=200)
	public void getSetCheck() {
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
	
	@Test(timeout=200)
	public void setReturnsCorrectValue() {
		actual = new LazyArray<Integer>(500);
		for (int i = 0; i < 500; i++) {
			assertNull(actual.set(i, i));
		}
		for (int i = 0; i < 500; i++) {
			assertEquals(i, (int) actual.set(i, i));
		}
	}
}
