package zjava.collection;

import java.util.Arrays;

/**
 * Array which supports more than Integer.MAX_VALUE elements
 * 
 * @param <E> the type of elements in this array
 *
 * @author Ivan Zaitsau
 */
public class HugeArray<E> implements Cloneable, java.io.Serializable {
		
	private static final long serialVersionUID = 2014_12_03_1800L;
	
	private E[][] data;
	private final long length;
	private final int blockBitsize;
	private final int blockMask;

    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + length;
    }
	
	private void rangeCheck(long index) {
		if (index < 0 | index >= length)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	/** Null-safe access to data block with lazy initialization.*/
	@SuppressWarnings("unchecked")
	private E[] data(int i) {
		if (data[i] == null)
			data[i] = (E[]) new Object[1 << blockBitsize];
		return data[i];
	}

    /**
     * The maximum size of array to allocate.<br>
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
	static private final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Constructs a HugeArray with the specified size.
     *
     * @param  size size of the HugeArray
     * @throws NegativeArraySizeException if the specified size
     *         is negative
     */
	@SuppressWarnings("unchecked")
	public HugeArray(long size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
        if (size > (long)MAX_ARRAY_SIZE << 30)
        	throw new OutOfMemoryError("Required array size too large");
		this.length = size;
		
		// - initializes data array
		int blockBitsize = 10;
		while (MAX_ARRAY_SIZE <= (this.length-1) >> blockBitsize)
			blockBitsize++;
		this.blockBitsize = blockBitsize;
		this.blockMask = (1 << blockBitsize) - 1;
		data = (E[][]) new Object[(int)((this.length + this.blockMask) >>> blockBitsize)][];
		if ((this.length & blockMask) > 0)
			data[data.length-1] = (E[]) new Object[(int)(this.length & blockMask)];
	}

	/**
	 * Returns value at specified position
	 * 
	 * @param index index of returned value
	 * @return value at specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
	public E get(long index) {
		rangeCheck(index);
		E[] dataBlock = data[(int)(index >>> blockBitsize)];
		return (dataBlock == null) ? null : dataBlock[(int)(index & blockMask)];
	}
	
	/**
	 * Replaces the value at specified position with method argument
	 * 
	 * @param index index of value to change
	 * @param value new value
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 */
	public void set(long index, E value) {
		rangeCheck(index);
		data((int)(index >>> blockBitsize))[(int)(index & blockMask)] = value;
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
     * Returns a copy of this <tt>HugeArray</tt> instance.
     *
     * @return a clone of this <tt>HugeArray</tt> instance
     */
	public Object clone() {
		try {
			@SuppressWarnings("unchecked")
			HugeArray<E> clone = (HugeArray<E>) super.clone();
			clone.data = Arrays.copyOf(data, data.length);
			for (int i = 0; i < data.length; i++)
				if (data[i] != null)
					clone.data[i] = Arrays.copyOf(data[i], data[i].length);
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
        for (long i = 0; i < length; ) {
            E e = get(i);
            sb.append(e == this ? "(this Array)" : e);
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
