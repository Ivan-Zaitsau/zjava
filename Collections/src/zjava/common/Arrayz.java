package zjava.common;

import java.util.Iterator;
import java.util.Set;

import zjava.collection.HashedSet;
import zjava.collection.Hasher;

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

	private static final int TO_STRING_SIZE_THRESHOLD = 4000;
	
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


	/* - actual implementation of toString() method
	 * - keeps track of visited Iterable's and arrays of Object's
	 */
	private static StringBuilder toStringBuilder(Object[] array, String onSelf, Set<Object> visited, StringBuilder result) {
		if (!visited.add(array))
			return result.append("[...]");

		result.append('[');
        for (int i = 0; i < array.length; i++) {
        	Object e = array[i];
        	if (e == array)
        		result.append(onSelf);
        	else if (e instanceof Iterable<?>)
        		toStringBuilder((Iterable<?>) e, "(this)", visited, result);
        	else if (e instanceof Object[])
        		toStringBuilder((Object[]) e, "(this)", visited, result);
        	else 
        		result.append(e);

            if (result.length() > TO_STRING_SIZE_THRESHOLD) {
            	result.append(", ...");
            	break;
            }
            if (i+1 < array.length)
            	result.append(',').append(' ');
        }
        result.append(']');
        return result;
	}

	/* - actual implementation of toString() method
	 * - keeps track of visited Iterable's and arrays of Object's
	 */
	private static StringBuilder toStringBuilder(Iterable<?> c, String onSelf, Set<Object> visited, StringBuilder result) {
		if (!visited.add(c))
			return result.append("[...]");

		result.append('[');
        for (Iterator<?> iter = c.iterator(); iter.hasNext(); ) {
        	Object e = iter.next();
        	if (e == c)
        		result.append(onSelf);
        	else if (e instanceof Iterable<?>)
        		toStringBuilder((Iterable<?>) e, "(this)", visited, result);
        	else if (e instanceof Object[])
        		toStringBuilder((Object[]) e, "(this)", visited, result);
        	else 
        		result.append(e);

            if (result.length() > TO_STRING_SIZE_THRESHOLD) {
            	result.append(", ...");
            	break;
            }
            if (iter.hasNext())
            	result.append(',').append(' ');
        }
        result.append(']');
        return result;
	}

    /**
     * Returns a string representation of the given <tt>Object[]</tt>.<br>
     * The string representation consists of a list of the <tt>Object[]</tt>'s
     * elements separated by commas. <tt>Object[]</tt> is enclosed in square brackets
     * (<tt>"[]"</tt>).
     * 
     * <p>If <tt>Object[]</tt> contains too many elements, only first elements
     * will be shown, followed by three-dot (<tt>"..."</tt>).
     * 
     * <p> If given <tt>Object[]</tt> is null, string "null" is returned.
     * 
	 * @param array - given <tt>Object[]</tt>
	 * 
	 * @return a string representation of the given <tt>Object[]</tt>
     */
	public static String toString(Object[] array, String onSelf) {
		if (array == null)
			return "null";
		return toStringBuilder(array, onSelf, new HashedSet<Object>(Hasher.IDENTITY), new StringBuilder()).toString();
	}
	
    /**
     * Returns a string representation of the given <tt>Object[]</tt>.<br>
     * The string representation consists of a list of the <tt>Object[]</tt>'s
     * elements separated by commas. <tt>Object[]</tt> enclosed in square brackets
     * (<tt>"[]"</tt>).
     * 
     * <p>If <tt>Object[]</tt> contains too many elements, only first elements
     * will be shown, followed by three-dot (<tt>"..."</tt>).
     * 
     * <p> If given <tt>Object[]</tt> is null, string "null" is returned.
     * 
	 * @param array - given <tt>Object[]</tt>
	 * 
	 * @return a string representation of the given <tt>Object[]</tt>
     */
	public static String toString(Object[] array) {
		return toString(array, "(this)");
	}

	private Arrayz() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
