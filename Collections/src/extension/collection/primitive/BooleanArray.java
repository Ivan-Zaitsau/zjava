package extension.collection.primitive;

import java.util.Arrays;

/**
 * Simple implementation of array of boolean values which
 * requires only 1 bit of memory per boolean value
 * 
 * @author Ivan Zaitsau
 */
public class BooleanArray implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 2014_12_03_1500L;

	private static int ADDRESS_BITS = 5;
	private static int BITS = 1 << ADDRESS_BITS;
	private static int MASK = BITS - 1;
	
	private final int length;
	private int[] data;

    /**
     * Constructs a BooleanArray with the specified size.
     *
     * @param  size size of the BooleanArray
     * @throws NegativeArraySizeException if the specified size
     *         is negative
     */
	public BooleanArray(int size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
		this.length = size;
		this.data = new int[1 + ((size-1) >>> ADDRESS_BITS)];
	}

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + length;
    }
	
	private void rangeCheck(int index) {
		if (index < 0 | index >= length)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	/**
	 * Sets value at specified position to <b>true</b>
	 * 
	 * @param index index of value to set to <b>true</b>
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public void setTrue(int index) {
		rangeCheck(index);
		data[index >>> ADDRESS_BITS] |= 1 << (index & MASK);
	}

	/**
	 * Sets value at specified position to <b>false</b>
	 * 
	 * @param index index of value to set to <b>false</b>
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public void setFalse(int index) {
		rangeCheck(index);
		data[index >>> ADDRESS_BITS] &= ~(1 << (index & MASK));
	}
	
	/**
	 * Replaces the value at specified position with method argument
	 * 
	 * @param index index of value to change
	 * @param value new value
     * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void set(int index, boolean value) {
		if (value)
			setTrue(index);
		else
			setFalse(index);
	}
	
	/**
	 * Returns boolean value at specified position
	 * 
	 * @param index index of returned value
	 * @return value at specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */	
	public boolean get(int index) {
		rangeCheck(index);
		return (data[index >>> ADDRESS_BITS] & (1 << (index & MASK))) > 0;
	}
	
	/**
	 * Returns size of the array
	 * 
	 * @return size of the array
	 */
	public int length() {
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
}
