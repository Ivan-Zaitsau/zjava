package zjava.collection.primitive;

import java.util.Arrays;

import zjava.system.Const;

/**
 * Simple implementation of array of boolean values which
 * requires only 1 bit of memory per boolean value (rounded up to multiple of 64).
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public class BooleanArray implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 201611081745L;

	private static final int ADDRESS_BITS = Const.ADDRESS_BITS_PER_LONG;
	private static final long MAX_SIZE = (long) Const.MAX_ARRAY_SIZE << ADDRESS_BITS;
	
	private final long length;
	private long[] data;

    /**
     * Constructs a BooleanArray with the specified size.
     *
     * @param  size size of the BooleanArray
     * @throws NegativeArraySizeException if the specified size
     *         is negative
     */
	public BooleanArray(long size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
		if (size > MAX_SIZE)
			throw new OutOfMemoryError("Required array size too large");
		
		this.length = size;
		this.data = new long[(int)(1 + ((size-1) >> ADDRESS_BITS))];
	}

    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + length;
    }
	
	private void rangeCheck(final long index) {
		if (index < 0 | index >= length)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	/**
	 * Sets value at specified position to <b>true</b>
	 * 
	 * @param index index of value to set to <b>true</b>
     * @return the value previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public boolean setTrue(final long index) {
		rangeCheck(index);
		final int di = (int)(index >>> ADDRESS_BITS);
		final long beforeUpdate = data[di];
		data[di] = PrimitiveBitSet.add(data[di], index);
		return beforeUpdate == data[di];
	}

	/**
	 * Sets value at specified position to <b>false</b>
	 * 
	 * @param index index of value to set to <b>false</b>
     * @return the value previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public boolean setFalse(final long index) {
		rangeCheck(index);
		final int di = (int)(index >>> ADDRESS_BITS);
		final long beforeUpdate = data[di];
		data[di] = PrimitiveBitSet.remove(data[di], index);
		return beforeUpdate != data[di];
	}
	
	/**
	 * Replaces the value at specified position with method argument
	 * 
	 * @param index index of value to change
	 * @param value new value
     * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public boolean set(final long index, final boolean value) {
		return value ? setTrue(index) : setFalse(index);
	}
	
	/**
	 * Returns boolean value at specified position
	 * 
	 * @param index index of returned value
	 * @return value at specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */	
	public boolean get(long index) {
		rangeCheck(index);
		return PrimitiveBitSet.contains(data[(int)(index >>> ADDRESS_BITS)], index);
	}
	
	/**
	 * Returns size of the array
	 * 
	 * @return size of the array
	 */
	public long length() {
		return length;
	}
	
    /**
     * Returns a copy of this <tt>BooleanArray</tt> instance.
     *
     * @return a clone of this <tt>BooleanArray</tt> instance
     */
	public Object clone() {
		try {
			BooleanArray clone = (BooleanArray) super.clone();
			clone.data = Arrays.copyOf(data, data.length);
			return clone;
		} catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
	}
	
    /**
     * Returns a string representation of this array. The string representation
     * consists of a list of the array's elements separated by commas
     * in index ascending order. List enclosed in square brackets (<tt>"[]"</tt>).
     * <br>
     * If list is too large, only first elements will be shown, followed by
     * three-dot (<tt>"..."</tt>).
     */
	public String toString() {
        if (length == 0)
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < length; ) {
            sb.append(Boolean.toString(get(i)));
            if (++i < length) {
                if (sb.length() > 1000) {
                	sb.append(',').append(" ...");
                	break;
                }            	
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
	}
}
