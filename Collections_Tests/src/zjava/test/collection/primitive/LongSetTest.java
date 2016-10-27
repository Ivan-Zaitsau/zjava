package zjava.test.collection.primitive;

import static org.junit.Assert.*;

import org.junit.Test;

import zjava.collection.primitive.LongSet;

public class LongSetTest {

	@Test(timeout = 200)
	public void basicAddContainsCheck() {
		LongSet set = new LongSet();
		long[] valuesToAdd = new long[] {0, 1, 3, 7, 11, 13, 6, 8, 64, 63, 61, 57, -1, -2, 1023, 1024, Long.MIN_VALUE, Long.MAX_VALUE};
		for (long v : valuesToAdd) {
			assertFalse(set.contains(v));
			assertTrue(set.add(v));
			assertTrue(set.contains(v));
		}
		for (int i = valuesToAdd.length-1; i >= 0; i--) {
			assertTrue(set.contains(valuesToAdd[i]));
		}
	}
	
	@Test(timeout = 200)
	public void addReturnsCorrectValue() {
		LongSet set = new LongSet();
		assertTrue(set.add(3));
		assertTrue(set.add(0));
		assertFalse(set.add(0));
		assertFalse(set.add(3));
		assertTrue(set.add(1));
		assertTrue(set.add(-153));
		assertFalse(set.add(-153));
		assertTrue(set.add(-13));
	}
	
	@Test(timeout = 200)
	public void basicRemoveCheck() {
		LongSet set = new LongSet();
		assertFalse(set.remove(0));
		long[] valuesToAdd = new long[] {0, 1, 3, 7, 11, 13, 6, 8, 64, 63, 61, 57, -1, -2, 1023, 1024, Long.MIN_VALUE, Long.MAX_VALUE};
		for (long v : valuesToAdd)
			set.add(v);
		assertFalse(set.remove(12));

		long[] valuesToCheckRemoveOn = new long[] {11, 0, -2, 64, 63, 1023, 1024, Long.MIN_VALUE, Long.MAX_VALUE};
		for (long v : valuesToCheckRemoveOn) {
			assertTrue(set.remove(v));
			assertFalse(set.contains(v));
			assertFalse(set.remove(v));
		}
	}
	
	@Test//(timeout = 200)
	public void sizeIsUpdatedProperlyAfterAddRemoveClear() {
		LongSet set = new LongSet();
		long[] valuesToAdd = new long[] {0, 1, 3, 7, 11, 13, 6, 8, 64, 63, 61, 57, -1, -2, 1023, 1024, Long.MIN_VALUE, Long.MAX_VALUE};
		int size = 0;
		for (long v : valuesToAdd) {
			set.add(v);
			size++;
			assertEquals(size, set.size());
		}
		long[] valuesNotInTheSet = new long[] {-5, -1024, -1025, -2000, -999, 62, 60, 59, 100};
		for (long v : valuesNotInTheSet) {
			set.remove(v);
			assertEquals(size, set.size());
		}
		long[] valuesToRemove = new long[] {1023, 0, 1024, Long.MIN_VALUE, Long.MAX_VALUE, -1, -2, 11, 13, 1, 3, 6, 7, 8};
		for (long v : valuesToRemove) {
			set.remove(v);
			size--;
			assertEquals(size, set.size());
		}
		set.clear();
		assertEquals(0, set.size());
	}
}
