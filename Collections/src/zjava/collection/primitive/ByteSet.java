package zjava.collection.primitive;

import zjava.system.Const;

/**
 * ByteSet represents set of primitive byte values.<br>
 * Implemented as a bitmap (where each possible value is mapped to one bit).<br>
 * All operations on this set complete in constant time.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 */
public class ByteSet {
	
	private static final int WORDS = 4;
	private static final int ADDRESS_BITS_PER_WORD = Const.ADDRESS_BITS_PER_LONG;
	private static final int BIT_INDEX_MASK = (1 << ADDRESS_BITS_PER_WORD) - 1;
	
	private int size;
	private long[] words = new long[WORDS];
	
	/**
	 * Returns number of elements in this set (it's size).
	 * 
	 * @return number of elements in this set
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns true if this set contains specified value.
	 * 
	 * @param value - value whose presence needs to be checked
	 * @return {@code true} if the set contains given value
	 */
	public boolean contains(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		return PrimitiveBitSet.contains(words[i >>> ADDRESS_BITS_PER_WORD], i);
	}

    /**
     * Adds the specified value to this set if it is not already present.<br>
     * 
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param v element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     */
	public boolean add(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		final int wi = i >>> ADDRESS_BITS_PER_WORD;
		long beforeUpdate = words[wi];
		words[wi] = PrimitiveBitSet.add(beforeUpdate, i);
		return words[wi] != beforeUpdate;
	}

    /**
     * Removes the specified value from this set if it is present.
     * Returns {@code true} if this set contained the element (or
     * equivalently, if this set changed as a result of the call).<br>
     * This set will not contain the element once the call returns.
     *
     * @param value element to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     */
	public boolean remove(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		final int wi = i >>> ADDRESS_BITS_PER_WORD;
		long beforeUpdate = words[wi];
		words[wi] = PrimitiveBitSet.remove(beforeUpdate, i);
		return words[wi] != beforeUpdate;
	}
	
	/**
	 * Returns next value after method argument <tt>v</tt> or <tt>v</tt> itself
	 * if such value doesn't exist in this set.
	 * 
	 * @param v - value to get next for
	 * @return value next to method parameter
	 */
	public byte next(byte v) {
		int i = (int) v - Byte.MIN_VALUE + 1;
		for (int wi = i >>> ADDRESS_BITS_PER_WORD; wi < WORDS; wi++) {
			long w = words[i];
			if (w > 0) {
				int j = PrimitiveBitSet.next(wi, i);
				if (j >= 0)
					return (byte) (Byte.MIN_VALUE + (wi << ADDRESS_BITS_PER_WORD) + j);
			}
			i = 0;
		}
		return v;
	}
	
    /**
     * Removes all the elements from this set.
     * The set will be empty after this call returns.
     */
	public void clear() {
		size = 0;
		for (int i = 0; i < WORDS; i++) words[i] = PrimitiveBitSet.EMPTY_SET;
	}

	/**
	 * Returns array which contains this set elements in increasing order.
	 * 
	 * @return array of this set elements in increasing order
	 */
	public byte[] toPrimitiveArray() {
		int arrSize = 0;
		for (int i = 0; i < WORDS; i++) arrSize += PrimitiveBitSet.size(words[i]);
		byte[] arr = new byte[arrSize];
		
		int ai = 0;
		for (int wi = 0; wi < WORDS; wi++) {
			long w = words[wi];
			if (w > 0)
				for (int j = 0; j <= BIT_INDEX_MASK; j++)
					if (PrimitiveBitSet.contains(w, j))
						arr[ai++] = (byte) (Byte.MIN_VALUE + (wi << ADDRESS_BITS_PER_WORD) + j);
		}
		return arr;
	}
}
