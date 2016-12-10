package zjava.collection;

/**
 * Array which supports more than Integer.MAX_VALUE elements.<br>
 * 
 * The <tt>HugeArray</tt> interface defines basic methods (size/get/set).<br>
 * These methods operate on <tt>long</tt> indexes and are able to access
 * elements beyond <tt>Integer.MAX_VALUE</tt> limit.<br>
 * 
 * @param <E> - the type of elements in this array
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public interface HugeArray<E> extends HugeCapacity {

	/**
	 * Returns size of the array.
	 * 
	 * @return size of the array
	 */
	long size();
	
	/**
	 * Replaces the value at specified position with method argument
	 * 
	 * @param index index of value to change
	 * @param value new value
	 * @return replaced value
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 */
	E set(long index, E value);
	
	/**
	 * Returns value at specified position
	 * 
	 * @param index index of returned value
	 * @return value at specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	E get(long index);
}
