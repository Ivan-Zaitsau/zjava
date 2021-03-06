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
	
	private long[] words = new long[WORDS];
	
	/**
	 * Returns number of elements in this set (it's size).
	 * 
	 * @return number of elements in this set
	 */
	public int size() {
		int size = 0;
		for (int i = 0; i < WORDS; i++)
			size += PrimitiveBitSet.size(words[i]);

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
		final long beforeUpdate = words[wi];
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
		final long beforeUpdate = words[wi];
		words[wi] = PrimitiveBitSet.remove(beforeUpdate, i);
		return words[wi] != beforeUpdate;
	}
	
	/**
	 * Returns value which is equal to or appears after method argument <tt>v</tt>
	 * or <tt>null</tt> if such value doesn't exist in this set.
	 * 
	 * @param v - value to get next for
	 * @return value next to method parameter
	 */
	public Byte next(final byte v) {
		int i = (int) v - Byte.MIN_VALUE;
		for (int wi = i >>> ADDRESS_BITS_PER_WORD; wi < WORDS; wi++) {
			if (words[i] != PrimitiveBitSet.EMPTY_SET) {
				int j = PrimitiveBitSet.next(words[i], i);
				if (j >= 0)
					return (byte) (Byte.MIN_VALUE + (wi << ADDRESS_BITS_PER_WORD) + j);
			}
			i = 0;
		}
		return null;
	}

    /**
     * Removes all the elements from this set.
     * The set will be empty after this call returns.
     */
	public void clear() {
		for (int i = 0; i < WORDS; i++) words[i] = PrimitiveBitSet.EMPTY_SET;
	}

	/**
	 * Returns array which contains this set elements in increasing order.
	 * 
	 * @return array of this set elements in increasing order
	 */
	public byte[] toPrimitiveArray() {
		byte[] arr = new byte[size()];
		
		int ai = 0;
		for (int wi = 0; wi < WORDS; wi++) {
			long w = words[wi];
			for (int j = PrimitiveBitSet.next(w, 0); w != PrimitiveBitSet.EMPTY_SET; j = PrimitiveBitSet.next(w, j+1)) {
				arr[ai++] = (byte) (Byte.MIN_VALUE + (wi << ADDRESS_BITS_PER_WORD) + j);
				w = PrimitiveBitSet.remove(w, j);
			}
		}
		return arr;
	}
}
