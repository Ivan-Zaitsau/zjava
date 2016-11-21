package zjava.collection;

import java.util.Arrays;

import static zjava.system.Const.MAX_ARRAY_SIZE;

/**
 * Array which supports more than Integer.MAX_VALUE elements
 * and allocates memory blocks dynamically when needed.
 * 
 * @param <E> - the type of elements in this array
 *
 * @since Zjava 1.0
 *
 * @author Ivan Zaitsau
 */
public class LazyArray<E> implements HugeArray<E>, Cloneable, java.io.Serializable {
	
	private static final long serialVersionUID = 201412031800L;
	
	private static final int DEFAULT_BLOCK_ADDRESS_BITS = 10;
	
	private final long size;
	private E[][] data;
	private final int blockAddressBits;
	private final int blockMask;
	
    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + size;
    }
	
	private void rangeCheck(long index) {
		if (index < 0 | index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	/** Null-safe access to data block with lazy initialization.*/
	@SuppressWarnings("unchecked")
	private E[] data(int i) {
		if (data[i] == null)
			data[i] = (E[]) new Object[1 << blockAddressBits];
		return data[i];
	}

    /**
     * Constructs a HugeArray with the specified size.
     *
     * @param  size size of the HugeArray
     * @throws NegativeArraySizeException if the specified size
     *         is negative
     */
	@SuppressWarnings("unchecked")
	public LazyArray(long size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
        if (size > (long)MAX_ARRAY_SIZE << 30)
        	throw new OutOfMemoryError("Required array size too large");
		this.size = size;
		
		// - initializes data array
		int blockAddressBits = DEFAULT_BLOCK_ADDRESS_BITS;
		while (MAX_ARRAY_SIZE <= (size-1) >> blockAddressBits)
			blockAddressBits++;
		this.blockAddressBits = blockAddressBits;
		blockMask = (1 << this.blockAddressBits) - 1;
		data = (E[][]) new Object[(int)((size + blockMask) >>> this.blockAddressBits)][];
		if ((this.size & blockMask) > 0)
			data[data.length-1] = (E[]) new Object[(int)(this.size & blockMask)];
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
		E[] dataBlock = data[(int)(index >>> blockAddressBits)];
		return (dataBlock == null) ? null : dataBlock[(int)(index & blockMask)];
	}
	
	/**
	 * Replaces the value at specified position with method argument
	 * 
	 * @param index index of value to change
	 * @param value new value
	 * @return replaced value
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
	 */
	public E set(long index, E value) {
		rangeCheck(index);
		E[] block = data((int)(index >>> blockAddressBits));
		E replaced = block[(int)(index & blockMask)];
		block[(int)(index & blockMask)] = value;
		return replaced;
	}
	
	/**
	 * Returns size of the array
	 * 
	 * @return size of the array
	 */
	public long size() {
		return size;
	}
	
    /**
     * Returns a copy of this <tt>HugeArray</tt> instance.
     *
     * @return a clone of this <tt>HugeArray</tt> instance
     */
	public Object clone() {
		try {
			@SuppressWarnings("unchecked")
			LazyArray<E> clone = (LazyArray<E>) super.clone();
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
        if (size == 0)
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (long i = 0; i < size; ) {
            E e = get(i);
            sb.append(e == this ? "(this Array)" : e);
            if (++i < size) {
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
