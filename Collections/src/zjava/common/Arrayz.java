package zjava.common;

/**
 * This class contains number of useful methods that operate on arrays.
 *
 *
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
final public class Arrayz {

	/**  An empty <tt>boolean</tt> array. */
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[] {};
	
	/**  An empty <tt>byte</tt> array. */
	public static final byte[]    EMPTY_BYTE_ARRAY    = new byte[] {};
	
	/**  An empty <tt>char</tt> array. */
	public static final char[]    EMPTY_CHAR_ARRAY    = new char[] {};
	
	/**  An empty <tt>double</tt> array. */
	public static final double[]  EMPTY_DOUBLE_ARRAY  = new double[] {};
	
	/**  An empty <tt>float</tt> array. */
	public static final float[]   EMPTY_FLOAT_ARRAY   = new float[] {};
	
	/**  An empty <tt>int</tt> array. */
	public static final int[]     EMPTY_int_ARRAY     = new int[] {};
	
	/**  An empty <tt>long</tt> array. */
	public static final long[]    EMPTY_LONG_ARRAY    = new long[] {};
	
	/**  An empty <tt>short</tt> array. */
	public static final short[]   EMPTY_SHORT_ARRAY   = new short[] {};

	/**  An empty <tt>Object</tt> array. */
	public static final Object[]  EMPTY_OBJECT_ARRAY  = new Object[] {};

	private Arrayz() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
