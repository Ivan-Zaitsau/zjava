package zjava.collection.primitive;

/**
 * This class contains number of methods to operate on
 * primitives as if they are sets of numbers between <tt>0</tt>
 * (inclusive) and <tt>bit_length_of_primitive</tt> (exclusive).<br>
 * For performance and simplicity range checks are omitted.
 * Instead, only last bits of a value are taken into consideration.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
final public class PrimitiveBitSet {

	/** Value which denotes empty set */
	static public final int EMPTY_SET = 0;
	
	// - "int" related methods
	
	/**
     * Returns the number of elements in the given set (it's cardinality).
	 * 
	 * @param set - given set
	 * @return number of elements in this set
	 */
	static public int size(final int set) {
		return Integer.bitCount(set);
	}
	
	/**
	 * Returns true if given set contains the specified value.
	 * 
	 * @param set - given set
	 * @param value - value whose presence needs to be checked
	 * @return <tt>true</tt> if the set contains given value<br>
	 *         <tt>false</tt> otherwise
	 */
	static public boolean contains(final int set, final int value) {
		return (set & (1 << value)) != 0;
	}
	
	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param set - given set
	 * @param value - value to add to the set
	 * @return updated set
	 */
	static public int add(final int set, final int value) {
		return set | (1 << value);
	}
	
	/**
	 * Removes the specified element from this set if it's present.
	 * 
	 * @param set - given set
	 * @param value - value to remove from the set
	 * @return updated set
	 */
	static public int remove(final int set, final int value) {
		return set & ~(1 << value);
	}
	
	/**
	 * Returns number of elements smaller than the given value.
	 * 
	 * @param set - given set
	 * @param value - value to get index for
	 * @return number of elements smaller than the given value
	 */
	static public int indexOf(final int set, final int value) {
		return Integer.bitCount(set & ((1 << value) - 1));
	}
	
	/**
	 * Returns next element on or after method argument <tt>value</tt>
	 * or <tt>-1</tt> if such element doesn't exist in the set provided.
	 * 
	 * @param set - given set
	 * @param value - value to get next for
	 * @return value next to method parameter
	 */
	static public int next(final int set, final int value) {
		int trailingZeroes = Integer.numberOfTrailingZeros(set & ~((1 << value) - 1));
		return (trailingZeroes < Integer.SIZE) ? trailingZeroes : -1;
	}

	// - "long" related methods
	
	/**
     * Returns the number of elements in the given set (it's cardinality).
	 * 
	 * @param set - given set
	 * @return number of elements in this set
	 */
	static public int size(final long set) {
		return Long.bitCount(set);
	}
	
	/**
	 * Returns true if given set contains the specified value.
	 * 
	 * @param set - given set
	 * @param value - value whose presence needs to be checked
	 * @return <tt>true</tt> if the set contains given value<br>
	 *         <tt>false</tt> otherwise
	 */
	static public boolean contains(final long set, final long value) {
		return (set & (1L << value)) != 0;
	}
	
	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param set - given set
	 * @param value - value to add to the set
	 * @return updated set
	 */
	static public long add(final long set, final long value) {
		return set | (1L << value);
	}
	
	/**
	 * Removes the specified element from this set if it's present.
	 * 
	 * @param set - given set
	 * @param value - value to remove from the set
	 * @return updated set
	 */
	static public long remove(final long set, final long value) {
		return set & ~(1L << value);
	}
	
	/**
	 * Returns number of elements smaller than the given value.
	 * 
	 * @param set - given set
	 * @param value - value to get index for
	 * @return number of elements smaller than the given value
	 */
	static public int indexOf(final long set, final long value) {
		return Long.bitCount(set & ((1L << value) - 1));
	}
	
	/**
	 * Returns next element on or after method argument <tt>value</tt>
	 * or <tt>-1</tt> if such element doesn't exist in the set provided.
	 * 
	 * @param set - given set
	 * @param value - value to get next for
	 * @return value next to method parameter
	 */
	static public long next(final long set, final long value) {
		int trailingZeroes = Long.numberOfTrailingZeros(set & ~((1L << value) - 1));
		return (trailingZeroes < Long.SIZE) ? trailingZeroes : -1;
	}
	
	/**
	 * Returns string representation of the set passed as method argument
	 * 
	 * @param set - given set
	 * @return string representation of the given set
	 */
	static public String toString(long set) {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		boolean isSeparatorPending = false;
		for (int i = 0; i < Long.SIZE; i++) {
			if (((1L << i) & set) != 0) {
				if (isSeparatorPending)
					sb.append(',').append(' ');
				sb.append(i);
				isSeparatorPending = true;
			}
		}
		sb.append(']');
		return sb.toString();
	}

	private PrimitiveBitSet() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	};
}
