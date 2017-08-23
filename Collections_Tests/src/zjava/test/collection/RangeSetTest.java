package zjava.test.collection;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import static zjava.collection.Collectionz.range;



public class RangeSetTest {

	// - General tests
	
	@Test(timeout = 200)
	public void emptyRangeSizeIsZero() {
		assertEquals(0, range(-1L, -1L).size());;
		assertEquals(0, range(Long.MAX_VALUE, Long.MAX_VALUE).size());
		assertEquals(0, range(Long.MIN_VALUE, Long.MIN_VALUE).size());
		assertEquals(0, range(Integer.MIN_VALUE, Integer.MIN_VALUE).size());
		assertEquals(0, range(Integer.MAX_VALUE, Integer.MAX_VALUE).size());
		assertEquals(0, range(Short.MIN_VALUE, Short.MIN_VALUE).size());
		assertEquals(0, range(Short.MAX_VALUE, Short.MAX_VALUE).size());
		assertEquals(0, range(Byte.MIN_VALUE, Byte.MIN_VALUE).size());
		assertEquals(0, range(Byte.MAX_VALUE, Byte.MAX_VALUE).size());
	}
	
	// - evaluates hash code of interval of long values following
	// - contract from the java.util.Set interface
	private int setHashCode(long fromInclusive, long toExclusive) {
		int h = 0;
		for (long v = fromInclusive; v < toExclusive; v++)
			h += (int) (v ^ (v >>> 32));
		return h;
	}

	@Test(timeout = 20000)
	public void hashCodeIsConsistentWithSetInterfaceContractForLongRangeSet() {
		// - empty interval (edge cases)
		assertEquals(0, range(-1L, -1L).hashCode());
		assertEquals(0, range(0L, 0L).hashCode());
		assertEquals(0, range(Long.MIN_VALUE, Long.MIN_VALUE).hashCode());
		assertEquals(0, range(Long.MAX_VALUE, Long.MAX_VALUE).hashCode());
		
		// - checks against actual set
		Set<Long> set;
		set = new HashSet<Long>(range(-17L, 314L));
		assertEquals(set.hashCode(), range(-17L, 314L).hashCode());
		assertEquals(set.hashCode(), range(-17L, 313L).closed().hashCode());
		assertEquals(set.hashCode(), range(313L, -18L).hashCode());
		set = new HashSet<Long>(range(24L, 1018L));
		assertEquals(set.hashCode(), range(24L, 1018L).hashCode());
		set = new HashSet<Long>(range(-3477L, -17L));
		assertEquals(set.hashCode(), range(-3477L, -17L).hashCode());
		set = new HashSet<Long>(range(0L, -133L));
		assertEquals(set.hashCode(), range(0L, -133L).hashCode());
		set = new HashSet<Long>(range(0L, 166L));
		assertEquals(set.hashCode(), range(0L, 166L).hashCode());
		set = null;
		
		// - checks without actual set using contract definition from java.util.Set interface
		assertEquals(setHashCode(-36342L, -16899L), range(-16900L, -36343L).hashCode());
		assertEquals(setHashCode(-16899L, 36342L), range(-16899L, 36342L).hashCode());
		assertEquals(setHashCode((-1L << 31) - 21, (-1L << 31) + 17), range((-1L << 31) - 21, (-1L << 31) + 17).hashCode());
		assertEquals(setHashCode(-37L, 177L + (1L << 32)), range(-37L, 177L + (1L << 32)).hashCode());
	}
	
	@Test//(timeout = 200)
	public void checkEqualsAgainstOtherRangeSet() {
		assertEquals(range(0, 0), range(100, 100));
		assertEquals(range(0, 0).closed(), range(0, 1));
		assertEquals(range(Long.MIN_VALUE, Long.MAX_VALUE).closed(), range(Long.MAX_VALUE, Long.MIN_VALUE).closed());
		assertEquals(range(-344, 360), range(359, -345));
	}
	
	@Test(timeout = 200)
	public void checkEqualsAgainstHashSet() {
		assertEquals(new HashSet<Integer>(range(0, 0)), range(100, 100));
		assertEquals(new HashSet<Integer>(range(0, 0).closed()), range(0, 1));
		assertEquals(new HashSet<Short>(range(Short.MIN_VALUE, Short.MAX_VALUE).closed()), range(Short.MAX_VALUE, Short.MIN_VALUE).closed());
		assertEquals(new HashSet<Integer>(range(-344, 360)), range(359, -345));
	}
	
	@Test(timeout = 200)
	public void unsupportedMethodCallsThrowUnsupportedOperationException() {
		Set<Integer> set = range(0, 100);
		boolean isUnsupported;
		// - add
		isUnsupported = false;
		try {
			set.add(1);
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - addAll
		try {
			set.addAll(Arrays.asList(1, 2, 3));
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - clear
		try {
			set.clear();
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - iterator().remove
		Iterator<Integer> iter = set.iterator();
		iter.next();
		try {
			iter.remove();
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - remove
		try {
			set.remove(200);
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - removeAll
		try {
			set.removeAll(Arrays.asList(100, 200, 300));
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
		isUnsupported = false;
		// - retainAll
		try {
			set.retainAll(range(0, 100));
		}
		catch (UnsupportedOperationException isOk) {
			isUnsupported = true;
		}
		catch (Exception isNotGood) {
			fail();
		}
		assertTrue(isUnsupported);
	}
	
	// - Performance tests
	
	@Test (timeout = 200)
	public void performanceTestHashCodeImplementedEfficiently() {
		range(Long.MAX_VALUE, Long.MIN_VALUE).closed().hashCode();
		range(-294892384208340283L, 29382038502385L).hashCode();
		range(0, Long.MIN_VALUE).hashCode();
		range(-1, Long.MAX_VALUE).closed().hashCode();
	}
	
	@Test (timeout = 200)
	public void performanceTestToStringImplementedEfficiently() {
		range(Long.MAX_VALUE, Long.MIN_VALUE).closed().toString();
		range(-294892384208340283L, 29382038502385L).toString();
		range(0, Long.MIN_VALUE).toString();
		range(-1, Long.MAX_VALUE).closed().toString();
	}
}
