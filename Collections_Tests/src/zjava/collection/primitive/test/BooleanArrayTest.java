package zjava.collection.primitive.test;

import static org.junit.Assert.*;

import java.util.BitSet;

import org.junit.Test;

import zjava.collection.primitive.BooleanArray;

public class BooleanArrayTest {

	// - edge cases
	
	@Test(timeout=200)
	public void outOfBounds01() {
		BooleanArray actual = new BooleanArray(1000);
		int exceptionsCount = 0;
		try {
			actual.get(1000);
		}
		catch(IndexOutOfBoundsException e) {
			exceptionsCount++;
		}
		assertEquals(exceptionsCount, 1);
		try {
			actual.get(-1);
		}
		catch(IndexOutOfBoundsException e) {
			exceptionsCount++;
		}
		assertEquals(exceptionsCount, 2);
	}
	
	// - basic operations tests
	
	@Test(timeout=200)
	public void basicTest01() {
		BooleanArray actual = new BooleanArray(1000);
		actual.set(31, true);
		actual.set(32, true);
		assertTrue(actual.get(31));
		assertTrue(actual.get(32));
		actual.set(31, false);
		actual.set(32, false);
		BitSet setBits = new BitSet(1000);
		int index = 0;
		for (int i = 0; i < 300; i++) {
			index = (index * 31 + i * 337) % 1000;
			actual.set(index, true);
			setBits.set(index);
		}
		for (int i = 0; i < 1000; i++) {
			assertEquals(actual.get(i), setBits.get(i));
		}
		for (int i = 0; i < 1000; i++) {
			actual.set(index, true);
			setBits.set(index);
		}
		for (int i = 0; i < 300; i++) {
			index = (index * 31 + i * 337) % 1000;
			actual.set(index, false);
			setBits.clear(index);
		}
		for (int i = 0; i < 1000; i++) {
			assertEquals(actual.get(i), setBits.get(i));
		}
	}

}
