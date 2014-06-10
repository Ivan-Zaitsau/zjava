package extension.collection;

/**
 * Simple implementation of array of boolean values which
 * requires only 1 bit of memory per boolean value.
 * 
 * @author Ivan Zaitsau
 */
public class BooleanArray {
	
	private static int ADDRESS_BITS = 5;
	private static int BITS = 1 << ADDRESS_BITS;
	private static int MASK = BITS - 1;
	
	private int length;
	private int[] data;

    /**
     * Constructs an BooleanArray with the specified size.
     *
     * @param  size size of the BooleanArray
     * @throws NegativeArraySizeException if the specified size
     *         is negative
     */
	public BooleanArray(int size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
		this.length = size;
		data = new int[1 + ((size-1) >>> ADDRESS_BITS)];
	}

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + length;
    }
	
	private void rangeCheck(int index) {
		if (index >= length)
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
		data[index >>> ADDRESS_BITS] |= 1L << (index & MASK);
	}

	/**
	 * Sets value at specified position to <b>false</b>
	 * 
	 * @param index index of value to set to <b>false</b>
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public void setFalse(int index) {
		rangeCheck(index);
		data[index >>> ADDRESS_BITS] &= ~(1L << (index & MASK));
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
		return (data[index >>> ADDRESS_BITS] & (1L << (index & MASK))) > 0;
	}
	
	/**
	 * Returns size of the array
	 * 
	 * @return size of the array
	 */
	public int length() {
		return length;
	}
}