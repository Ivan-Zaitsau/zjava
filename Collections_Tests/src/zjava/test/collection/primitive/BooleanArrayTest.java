package zjava.test.collection.primitive;

import static org.junit.Assert.*;

import java.util.BitSet;

import org.junit.Test;

import zjava.collection.primitive.BooleanArray;

public class BooleanArrayTest {

	// - edge cases

	@Test(timeout=200)
	public void outOfBoundsThrownOnGetWithIndexEqualOrMoreThanSize() {
		BooleanArray actual = new BooleanArray(800);
		for (int i = 800; i < 1025; i++) {
			boolean exceptionThrown = false;
			try {
				actual.get(i);
			}
			catch(IndexOutOfBoundsException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);			
		}
	}
	
	@Test(timeout=200)
	public void outOfBoundsThrownOnSetWithIndexEqualOrMoreThanSize() {
		BooleanArray actual = new BooleanArray(800);
		for (boolean v = false, pv = false; !pv; pv = v, v = true)
			for (int i = 800; i < 1025; i++) {
				boolean exceptionThrown = false;
				try {
					actual.set(i, true);
				}
				catch(IndexOutOfBoundsException e) {
					exceptionThrown = true;
				}
				assertTrue(exceptionThrown);			
			}
	}
	
	@Test(timeout=200)
	public void outOfBoundsThrownOnGetWithIndexLessThanZero() {
		BooleanArray actual = new BooleanArray(1000);
		for (int i = -65; i < 0; i++) {
			boolean exceptionThrown = false;
			try {
				actual.get(i);
			}
			catch(IndexOutOfBoundsException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);			
		}
	}

	@Test(timeout=200)
	public void outOfBoundsThrownOnSetWithIndexLessThanZero() {
		BooleanArray actual = new BooleanArray(1000);
		for (boolean v = false, pv = false; !pv; pv = v, v = true)
			for (int i = -65; i < 0; i++) {
				boolean exceptionThrown = false;
				try {
					actual.set(i, v);
				}
				catch(IndexOutOfBoundsException e) {
					exceptionThrown = true;
				}
				assertTrue(exceptionThrown);			
			}
	}

	@Test(timeout=200)
	public void outOfBoundsThrownForAnyKindOfAccessForArrayOfSizeZero() {
		BooleanArray actual = new BooleanArray(0);
		for (int i = -65; i < 65; i++) {
			boolean exceptionThrown = false;
			try {
				actual.get(i);
			}
			catch(IndexOutOfBoundsException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);			
		}
		for (boolean v = false, pv = false; !pv; pv = v, v = true)
			for (int i = -65; i < 65; i++) {
				boolean exceptionThrown = false;
				try {
					actual.set(i, v);
				}
				catch(IndexOutOfBoundsException e) {
					exceptionThrown = true;
				}
				assertTrue(exceptionThrown);			
			}
	}
	
	// - basic tests

	@Test(timeout=200)
	public void allValuesAreSetToFalseAfterCreation() {
		BooleanArray actual = new BooleanArray(1000);
		for (int i = 0; i < 1000; i++)
			assertFalse(actual.get(i));
	}

	@Test(timeout=200)
	public void getAndSetOperationsWorkProperlyOnIndexesEqualToHighestAndLowestBitsOfMachineWords() {
		int n = 100;
		int[] indexesToCheck = new int[] {0, 7, 8, 15, 16, 31, 32, 63, 64};
		
		BooleanArray actual = new BooleanArray(n);

		for (int index : indexesToCheck) {
			actual.set(index, true);
			for (int i = 0; i < n; i++)
				assertEquals(actual.get(i), i == index);
			actual.set(index, false);
			for (int i = 0; i < n; i++)
				assertFalse(actual.get(i));
		}
	}
	
	@Test(timeout=200)
	public void getAndSetOperationsWorkProperly() {
		BooleanArray actual = new BooleanArray(1000);
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
