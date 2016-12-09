package zjava.test.system;

import static org.junit.Assert.*;

import org.junit.Test;

import zjava.system.Const;

public class ConstTest {

	@Test(timeout=200)
	public void constantsAreValid() {
		assertEquals(1 << Const.ADDRESS_BITS_PER_BYTE  , Byte.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_SHORT , Short.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_INT   , Integer.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_LONG  , Long.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_CHAR  , Character.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_FLOAT , Float.SIZE);
		assertEquals(1 << Const.ADDRESS_BITS_PER_DOUBLE, Double.SIZE);
		final int bitsInByte = Const.BITS_PER_BYTE;
		assertEquals(bitsInByte * Const.BYTES_PER_BYTE  , Byte.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_SHORT , Short.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_INT   , Integer.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_LONG  , Long.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_CHAR  , Character.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_FLOAT , Float.SIZE);
		assertEquals(bitsInByte * Const.BYTES_PER_DOUBLE, Double.SIZE);
	}
	
	@Test(timeout=200)
	public void sizeOfReturnsExpectedResults() {
		boolean booleanValue = true;
		assertEquals(Const.sizeOf(booleanValue), Const.BYTES_PER_BOOLEAN);
		byte byteValue = 0;
		assertEquals(Const.sizeOf(byteValue), Const.BYTES_PER_BYTE);
		short shortValue = 0;
		assertEquals(Const.sizeOf(shortValue), Const.BYTES_PER_SHORT);
		int intValue = 0;
		assertEquals(Const.sizeOf(intValue), Const.BYTES_PER_INT);
		long longValue = 0;
		assertEquals(Const.sizeOf(longValue), Const.BYTES_PER_LONG);
		char charValue = ' ';
		assertEquals(Const.sizeOf(charValue), Const.BYTES_PER_CHAR);
		float floatValue = 0;
		assertEquals(Const.sizeOf(floatValue), Const.BYTES_PER_FLOAT);
		double doubleValue = 0;
		assertEquals(Const.sizeOf(doubleValue), Const.BYTES_PER_DOUBLE);
	}
}
