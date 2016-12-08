package zjava.system;

/**
 * This class contains general JVM level constants.
 * 
 * @since Zjava 1.0
 *
 * @author Ivan Zaitsau
 */
public final class Const {
	
    /**
     * The maximum size of array to allocate.<br>
     * Some VMs reserve some header words in an array.<br>
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
	static public final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	static public final int BITS_BYTES_BITWISE_SHIFT = 3;
	static public final int BITS_PER_BYTE = 8;
	
	static public final int ADDRESS_BITS_PER_BOOLEAN = 3;	
	
	static public final int ADDRESS_BITS_PER_BYTE    = 3;
	static public final int ADDRESS_BITS_PER_SHORT   = 4;
	static public final int ADDRESS_BITS_PER_INT     = 5;
	static public final int ADDRESS_BITS_PER_LONG    = 6;
	
	static public final int ADDRESS_BITS_PER_CHAR    = 4;
	
	static public final int ADDRESS_BITS_PER_FLOAT   = 5;	
	static public final int ADDRESS_BITS_PER_DOUBLE  = 6;	

	static public final int BYTES_PER_BOOLEAN = (1 << ADDRESS_BITS_PER_BOOLEAN) >>> BITS_BYTES_BITWISE_SHIFT;	

	static public final int BYTES_PER_BYTE    = (1 << ADDRESS_BITS_PER_BYTE  ) >>> BITS_BYTES_BITWISE_SHIFT;
	static public final int BYTES_PER_SHORT   = (1 << ADDRESS_BITS_PER_SHORT ) >>> BITS_BYTES_BITWISE_SHIFT;
	static public final int BYTES_PER_INT     = (1 << ADDRESS_BITS_PER_INT   ) >>> BITS_BYTES_BITWISE_SHIFT;
	static public final int BYTES_PER_LONG    = (1 << ADDRESS_BITS_PER_LONG  ) >>> BITS_BYTES_BITWISE_SHIFT;
	
	static public final int BYTES_PER_CHAR    = (1 << ADDRESS_BITS_PER_CHAR  ) >>> BITS_BYTES_BITWISE_SHIFT;
	
	static public final int BYTES_PER_FLOAT   = (1 << ADDRESS_BITS_PER_FLOAT ) >>> BITS_BYTES_BITWISE_SHIFT;
	static public final int BYTES_PER_DOUBLE  = (1 << ADDRESS_BITS_PER_DOUBLE) >>> BITS_BYTES_BITWISE_SHIFT;

	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final boolean v) {
		return BYTES_PER_BOOLEAN;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final byte v) {
		return BYTES_PER_BYTE;
	}

	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final short v) {
		return BYTES_PER_SHORT;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final int v) {
		return BYTES_PER_INT;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final long v) {
		return BYTES_PER_LONG;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final char v) {
		return BYTES_PER_CHAR;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final float v) {
		return BYTES_PER_FLOAT;
	}
	
	/**
	 * Returns size of given item in bytes;
	 * @param v - item to get size for
	 * @return size of the given item in bytes
	 */
	public static int sizeOf(final double v) {
		return BYTES_PER_DOUBLE;
	}
	
	private Const() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
