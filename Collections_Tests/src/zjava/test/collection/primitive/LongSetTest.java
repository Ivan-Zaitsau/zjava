package zjava.test.collection.primitive;

import static org.junit.Assert.*;

import java.util.Arrays;

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
	
	@Test(timeout = 200)
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
	
	@Test(timeout = 200)
	public void basicNextCheck() {
		LongSet set = new LongSet();
		set.add(Long.MIN_VALUE);
		assertEquals((Long) Long.MIN_VALUE, set.next(Long.MIN_VALUE));
		assertEquals(null, set.next(Long.MIN_VALUE+1));
		set.remove(Long.MIN_VALUE);
		set.add(Long.MAX_VALUE);
		assertEquals((Long) Long.MAX_VALUE, set.next(Long.MAX_VALUE));
		assertEquals((Long) Long.MAX_VALUE, set.next(Long.MIN_VALUE));
		set.add(1);
		assertEquals((Long) 1L, set.next(-1));
		assertEquals((Long) 1L, set.next( 0));
		assertEquals((Long) 1L, set.next( 1));
		assertEquals((Long) Long.MAX_VALUE, set.next(2));
		set.remove(Long.MAX_VALUE);
		for (long v : new long[] {3, 63, 64, 1020, 1023, 1024})
			set.add(v);
		assertEquals((Long) 3L, set.next(2));	
		assertEquals((Long) 63L, set.next(4));
		assertEquals((Long) 64L, set.next(64));
		assertEquals((Long) 1020L, set.next(65));
		assertEquals((Long) 1023L, set.next(1021));
		assertEquals((Long) 1024L, set.next(1024));
		for (long v : new long[] {127, 130, 160, 199, 200})
			set.add(v);
		assertEquals((Long) 64L, set.next(64));
		assertEquals((Long) 127L, set.next(65));
		assertEquals((Long) 130L, set.next(129));
		assertEquals((Long) 160L, set.next(131));
		assertEquals((Long) 199L, set.next(180));
		assertEquals((Long) 200L, set.next(200));
		assertEquals(null, set.next(1025));
	}
	
	@Test(timeout = 200)
	public void compareWorksAsExpectedInAscendingSignedSortMode() {
		LongSet set = new LongSet(true, true);
		assertTrue(set.compare(-1, 0) < 0);
		assertTrue(set.compare(Long.MAX_VALUE, Long.MIN_VALUE) > 0);
		assertTrue(set.compare(Long.MAX_VALUE, -1) > 0);
		assertTrue(set.compare(77, 77) == 0);
		assertTrue(set.compare(66, 77) < 0);		
		assertTrue(set.compare(1, -1) > 0);
	}
	
	@Test(timeout = 200)
	public void compareWorksAsExpectedInDescendingSignedSortMode() {
		LongSet set = new LongSet(false, true);
		assertTrue(set.compare(-1, 0) > 0);
		assertTrue(set.compare(Long.MAX_VALUE, Long.MIN_VALUE) < 0);
		assertTrue(set.compare(Long.MAX_VALUE, -1) < 0);
		assertTrue(set.compare(77, 77) == 0);
		assertTrue(set.compare(67, 77) > 0);
		assertTrue(set.compare(1, -1) < 0);
	}
	
	@Test(timeout = 200)
	public void compareWorksAsExpectedInAscendingUnsignedSortMode() {
		LongSet set = new LongSet(true, false);
		assertTrue(set.compare(-1, 0) > 0);
		assertTrue(set.compare(Long.MAX_VALUE, Long.MIN_VALUE) < 0);
		assertTrue(set.compare(Long.MAX_VALUE, -1) < 0);
		assertTrue(set.compare(77, 77) == 0);
		assertTrue(set.compare(67, 77) < 0);
		assertTrue(set.compare(1, -1) < 0);
	}
	
	@Test(timeout = 200)
	public void compareWorksAsExpectedInDescendingUnsignedSortMode() {
		LongSet set = new LongSet(false, false);
		assertTrue(set.compare(-1, 0) < 0);
		assertTrue(set.compare(Long.MAX_VALUE, Long.MIN_VALUE) > 0);
		assertTrue(set.compare(Long.MAX_VALUE, -1) > 0);
		assertTrue(set.compare(77, 77) == 0);
		assertTrue(set.compare(67, 77) > 0);
		assertTrue(set.compare(1, -1) > 0);
	}
	
	@Test(timeout = 200)
	public void checkNextOperationOnLongSetWithComplexInternalStructure() {
		LongSet set = new LongSet();
		long[] valuesToAdd = new long[] {
				-1025, -999, -995, -990, -639, -630, -625,
				1200, 1300, 1500, 1425, 1777, 1025, 66000,
				4100000, 4100005, 4100010, 4100011, 4100015, 4100019,
				4100025, 4100030, 4100033, 4100037, 4100200, 4100201, 4200000};
		for (long v : valuesToAdd)
			set.add(v);
		long[] holes = new long[] {Long.MIN_VALUE, -100000, 2500, 4170000, 2000000000, Long.MAX_VALUE};
		for (long v : holes) {
			set.add(v); set.remove(v);
		}
		Arrays.sort(valuesToAdd);
		long nv = Long.MIN_VALUE;
		for (long v : valuesToAdd) {
			nv = set.next(nv);
			assertEquals(v, nv);
			nv++;
		}
		assertEquals(null, set.next(nv));
	}
}
