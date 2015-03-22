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

	// - "int" related methods
	
	/**
     * Returns the number of elements in the given set (it's cardinality).
	 * 
	 * @param set - given set
	 * @return number of elements in this set
	 */
	static public int size(int set) {
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
	static public boolean contains(int set, int value) {
		return (set & (1 << value)) != 0;
	}
	
	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param set - given set
	 * @param value - value to add to the set
	 * @return updated set
	 */
	static public int add(int set, int value) {
		return set | (1 << value);
	}
	
	/**
	 * Removes the specified element from this set if it's present.
	 * 
	 * @param set - given set
	 * @param value - value to remove from the set
	 * @return updated set
	 */
	static public int remove(int set, int value) {
		return set & ~(1 << value);
	}
	
	/**
	 * Returns number of elements smaller than the given value.
	 * 
	 * @param set - given set
	 * @param value - value to get index for
	 * @return number of elements smaller than the given value
	 */
	static public int indexOf(int set, int value) {
		return Integer.bitCount(set & ((1 << value) - 1));
	}
	
	// - "long" related methods
	
	/**
     * Returns the number of elements in the given set (it's cardinality).
	 * 
	 * @param set - given set
	 * @return number of elements in this set
	 */
	static public int size(long set) {
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
	static public boolean contains(long set, long value) {
		return (set & (1L << value)) != 0;
	}
	
	/**
	 * Adds the specified element to this set if it is not already present.
	 * 
	 * @param set - given set
	 * @param value - value to add to the set
	 * @return updated set
	 */
	static public long add(long set, long value) {
		return set | (1L << value);
	}
	
	/**
	 * Removes the specified element from this set if it's present.
	 * 
	 * @param set - given set
	 * @param value - value to remove from the set
	 * @return updated set
	 */
	static public long remove(long set, long value) {
		return set & ~(1L << value);
	}
	
	/**
	 * Returns number of elements smaller than the given value.
	 * 
	 * @param set - given set
	 * @param value - value to get index for
	 * @return number of elements smaller than the given value
	 */
	static public int indexOf(long set, long value) {
		return Long.bitCount(set & ((1L << value) - 1));
	}
	
	private PrimitiveBitSet() {};
}
