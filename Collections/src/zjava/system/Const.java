package zjava.system;

/**
 * This class contains general JVM level constants.
 * 
 * @author Ivan Zaitsau
 * 
 * @since Zjava 1.0
 *
 */
final public class Const {
	
    /**
     * The maximum size of array to allocate.<br>
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
	static public final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	static public final int BITS_PER_BYTE  = 8;
	static public final int BITS_PER_SHORT = 16;
	static public final int BITS_PER_INT   = 32;
	static public final int BITS_PER_LONG  = 64;
	static public final int ADDRESS_BITS_PER_BYTE  = 3;
	static public final int ADDRESS_BITS_PER_SHORT = 4;
	static public final int ADDRESS_BITS_PER_INT   = 5;
	static public final int ADDRESS_BITS_PER_LONG  = 6;

	private Const() {
		throw new AssertionError("Instantiaion of utility class " + getClass().getName() + " is prohibited");
	}
}
