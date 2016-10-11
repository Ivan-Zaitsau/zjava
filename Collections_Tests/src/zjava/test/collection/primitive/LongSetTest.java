package zjava.test.collection.primitive;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import zjava.collection.primitive.LongSet;

public class LongSetTest {

	@Test
	public void basicAddContainsCheck() {
		LongSet set = new LongSet();
		long[] valuesToAdd = new long[] {0, 1, 3, 7, 11, 13, 6, 8, 64, 63, 61, 57, -1, -2};
		for (long v : valuesToAdd) {
			assertFalse(set.contains(v));
			assertTrue(set.add(v));
			assertTrue(set.contains(v));
		}
		for (int i = valuesToAdd.length-1; i >= 0; i--) {
			assertTrue(set.contains(valuesToAdd[i]));
		}
	}
}
