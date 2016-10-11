package zjava.collection.primitive;

import java.util.Arrays;

import zjava.system.Const;

/**
 * LongSet represents set of primitive long values.<br>
 * Elements of this set are sorted.<br>
 * (Supports signed/unsigned and ascending/descending order of it's elements).
 * 
 * <p>This implementation provides guaranteed log(n) time cost for {@code contains},
 * {@code add} and {@code remove} operations.
 * 
 * <p>Memory usage varies from approximately 2 bits to 32 bytes per value, depending
 * on numbers distribution.
 * 
 * <p>Implemented as a Radix Tree. There are two types of nodes: <tt>Branch</tt> and <tt>Leaf</tt> nodes.<br>
 * <tt>Branch</tt> node represents 6-bit radix of long value.<br>
 * <tt>Leaf</tt> node represents last 10-bit radix.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 */
public class LongSet {
	
	static private final int ADDRESS_BITS_PER_WORD = Const.ADDRESS_BITS_PER_LONG;
	static private final int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
	
	static private final int BRANCH_RADIX = ADDRESS_BITS_PER_WORD;
	static private final int BRANCH_RADIX_MASK = (1 << BRANCH_RADIX) - 1;
	static private final int LEAF_RADIX = 10;
	static private final int LEAF_RADIX_MASK = (1 << LEAF_RADIX) - 1;
	
	static private interface Node {
		boolean contains(int startingBit, long value);
		boolean add(int startingBit, long value);
		boolean remove(int startingBit, long value);
		Long next(int startingBit, long value);
	}
	
	// - each branch node is assigned to BRANCH_RADIX-bit radix
	static private class Branch implements Node {
		
		static final int INITIAL_NUM_OF_CHILDREN = 4;
		static final int NUM_OF_CHILDREN_DELTA = 4;
		
		long used;
		Node[] children;

		Branch(long value) {
			used = value;
		}

		Node newEntry(int startingBit, long value) {
			return (startingBit <= LEAF_RADIX) ? new Leaf(value) : new Branch(value);
		}
		
		public boolean contains(int startingBit, long value) {
			// - special case: value is stored in "used" itself
			if (children == null)
				return (value == used) & (used != 0);
			// - general case
			long radix = (value >>> startingBit) & BRANCH_RADIX_MASK;
			if (!PrimitiveBitSet.contains(used, radix))
				return false;
			return children[PrimitiveBitSet.indexOf(used, radix)].contains(startingBit - BRANCH_RADIX, value);
		}

		public boolean add(int startingBit, long value) {
			// - special case: value is stored in "used" itself
			if (children == null) {
				if (used == 0 & value != 0) {
					used = value;
					return true;
				}
				children = new Node[INITIAL_NUM_OF_CHILDREN];
				add(startingBit, used);
				return add(startingBit, value);
			}
			// - general case
			long radix = (value >>> startingBit) & BRANCH_RADIX_MASK;
			if (PrimitiveBitSet.contains(used, radix))
				return children[PrimitiveBitSet.indexOf(used, radix)].add(startingBit - BRANCH_RADIX, value);
			// - add entry to "children" array
			int childrenAmount = PrimitiveBitSet.size(used);
			int childIndex = PrimitiveBitSet.indexOf(used, radix);
			if (childrenAmount >= children.length)
				children = Arrays.copyOf(children, children.length + NUM_OF_CHILDREN_DELTA);
			for (int i = childIndex; i < childrenAmount; i++)
				children[i+1] = children[i];
			children[childIndex] = newEntry(startingBit, value);
			PrimitiveBitSet.add(used, radix);
			return true;
		}

		public boolean remove(int startingBit, long value) {
			// - special case: value is stored in "used" itself
			if (children == null) {
				if (used == 0 | value != used)
					return false;

				used = 0;
				return true;
			}
			// - general case
			long radix = (value >>> startingBit) & BRANCH_RADIX_MASK;
			if (!PrimitiveBitSet.contains(used, radix))
				return false;
			return children[PrimitiveBitSet.indexOf(used, radix)].remove(startingBit - BRANCH_RADIX, value);
		}

		public Long next(int startingBit, long value) {
			// - special case: value is stored in "used" itself
			if (children == null) {
				if (used != 0 & value <= used)
					return used;
				else
					return null;
			}
			// - general case
			long radix = (value >>> startingBit) & BRANCH_RADIX_MASK;
			int i = PrimitiveBitSet.indexOf(used, (radix));
			if (PrimitiveBitSet.contains(used, radix)) {
				Long next = children[i++].next(startingBit - BRANCH_RADIX, value);
				if (next != null)
					return next;
			}
			for (radix = radix+1; radix <= BRANCH_RADIX_MASK; radix++)
				if (PrimitiveBitSet.contains(used, radix)) {
					long updatedValue = (((value >>> (startingBit + BRANCH_RADIX)) << BRANCH_RADIX) + radix) << startingBit;
					Long next = children[i++].next(startingBit - BRANCH_RADIX, updatedValue);
					if (next != null)
						return next;
				}
			return null;
		}
	}
	
	// - leaf node is assigned to last LEAF_RADIX bits of a number
	static private class Leaf implements Node {
		
		static final int INITIAL_NUM_OF_SETS = 4;
		static final int MAXIMUM_NUM_OF_SETS = 1 << (LEAF_RADIX - ADDRESS_BITS_PER_WORD);
		static final int COMPRESSED_RADIXES = 6;
		static final int COMPRESSED_RADIXES_BITS = COMPRESSED_RADIXES * LEAF_RADIX;
		
		long used;
		long[] sets;

		Leaf(long value) {
			used = (1L << COMPRESSED_RADIXES_BITS) | (value & LEAF_RADIX_MASK);
		}

		public boolean contains(int startingBit, long value) {
			long radix = value & LEAF_RADIX_MASK;
			// - optimization: a few radixes stored in "used" itself
			if (sets == null) {
				long bits = used;
				int i = (int) (used >>> COMPRESSED_RADIXES_BITS);
				while (i > 0) {
					if (radix == (bits & LEAF_RADIX_MASK))
						return true;
					bits >>>= LEAF_RADIX;
					i--;
				}
				return false;
			}
			// - general code
			long setId = radix >>> ADDRESS_BITS_PER_WORD;
			if (!PrimitiveBitSet.contains(used, setId))
				return false;
			return PrimitiveBitSet.contains(sets[PrimitiveBitSet.indexOf(used, setId)], radix);
		}
		
		void transform() {
			long bits = used;
			int entries = (int) (used >>> COMPRESSED_RADIXES_BITS);
			used = 0;
			sets = new long[INITIAL_NUM_OF_SETS];
			while (entries > 0) {
				add(0, bits);
				bits >>>= LEAF_RADIX;
				entries--;
			}
		}
		
		public boolean add(int startingBit, long value) {
			long radix = value & LEAF_RADIX_MASK;
			// - optimization: a few radixes stored in "used" itself			
			if (sets == null) {
				long bits = used;
				int entries = (int) (used >>> COMPRESSED_RADIXES_BITS);
				for (int i = 0; i < entries; i++) {
					if (radix == (bits & LEAF_RADIX_MASK))
						return false;
					bits >>>= LEAF_RADIX;					
				}
				if (entries < COMPRESSED_RADIXES) {
					entries++;
					used = (entries << COMPRESSED_RADIXES_BITS) | (used << LEAF_RADIX) | radix;
					return true;
				}
				transform();
			}
			// - general code
			long setId = radix >>> ADDRESS_BITS_PER_WORD;
			int setIndex = PrimitiveBitSet.indexOf(used, setId);
			if (!PrimitiveBitSet.contains(used, setId)) {
				// - add set
				int setsAmount = PrimitiveBitSet.size(used);
				if (setsAmount == sets.length)
					sets = Arrays.copyOf(sets, 2 * sets.length);
				for (int i = setIndex; i < setsAmount; i++)
					sets[i+1] = sets[i];
				sets[setIndex] = 0;
				used = PrimitiveBitSet.add(used, setId);
			}
			long beforeUpdate = sets[setIndex];
			sets[setIndex] = PrimitiveBitSet.add(beforeUpdate, radix);
			return sets[setIndex] == beforeUpdate;
		}
		
		public boolean remove(int startingBit, long value) {
			long radix = value & LEAF_RADIX_MASK;
			// - optimization: a few radixes stored in "used" itself
			if (sets == null) {
				long bits = used;
				long updatedBits = 0;
				boolean modified = false;
				int entries = (int) (used >>> COMPRESSED_RADIXES_BITS);
				for (int i = 0; i < entries; i++) {
					if (radix == (bits & LEAF_RADIX_MASK))
						modified = true;
					else
						updatedBits = (updatedBits << LEAF_RADIX) + (bits & LEAF_RADIX_MASK);
					bits >>>= LEAF_RADIX;					
				}
				if (modified) {
					entries--;
					used = (entries << COMPRESSED_RADIXES_BITS) + updatedBits;
				}
				return modified;
			}
			// - general code
			long setId = radix >>> ADDRESS_BITS_PER_WORD;
			if (!PrimitiveBitSet.contains(used, setId))
				return false;
			int setIndex = PrimitiveBitSet.indexOf(used, setId);
			long beforeUpdate = sets[setIndex];
			sets[setIndex] = PrimitiveBitSet.remove(beforeUpdate, radix);
			return sets[setIndex] != beforeUpdate;
		}

		public Long next(int startingBit, long value) {
			long radix = value & LEAF_RADIX_MASK;
			// - optimization: a few radixes stored in "used" itself
			if (sets == null) {
				long next = Long.MAX_VALUE;
				long bits = used;
				int i = (int) (used >>> COMPRESSED_RADIXES_BITS);
				while (i > 0) {
					if (radix <= (bits & LEAF_RADIX_MASK) & (bits & LEAF_RADIX_MASK) < next)
						next = (bits & LEAF_RADIX_MASK);
					bits >>>= LEAF_RADIX;
					i--;
				}
				if (next == Long.MAX_VALUE)
					return null;
				else
					return (value & ~(long)LEAF_RADIX_MASK) + next;
			}
			// - general code
			int setId = (int)(radix >>> ADDRESS_BITS_PER_WORD);
			int setIndex = PrimitiveBitSet.indexOf(used, setId);
			if (PrimitiveBitSet.contains(used, setId)) {
				long next = PrimitiveBitSet.next(sets[setIndex++], value);
				if ((next & value) != next)
					return ((value >>> LEAF_RADIX) << LEAF_RADIX) + (setId << BITS_PER_WORD) + next;
					
			}
			for (setId = setId+1; setId < MAXIMUM_NUM_OF_SETS & setIndex < sets.length; setIndex++)
				if (PrimitiveBitSet.contains(used, setId) && sets[setIndex++] != PrimitiveBitSet.EMPTY_SET) {
					long set = sets[setIndex-1];
					long next = ((value >>> LEAF_RADIX) << LEAF_RADIX) + (setId << BITS_PER_WORD);
					if (!PrimitiveBitSet.contains(set, 0))
						next += PrimitiveBitSet.next(set, 0);
					return next;
				}
			return null;
		}
	}
	
	static private final long MODE_ASCENDING  = 0x0000000000000000L;
	static private final long MODE_DESCENDING = 0xFFFFFFFFFFFFFFFFL;
	static private final long MODE_SIGNED     = 0x8000000000000000L;
	static private final long MODE_UNSIGNED   = 0x0000000000000000L;
	
	private final long mode;
	private long size;
	private Node root;
	
	public LongSet() {
		this(true, true);
	}
	
	public LongSet(boolean ascending, boolean signed) {
		mode = (ascending ? MODE_ASCENDING : MODE_DESCENDING)
				^ (signed ? MODE_SIGNED : MODE_UNSIGNED);
	}
	
	private long applyMode(long value) {
		return value ^ mode;
	}
	
	private long reverseMode(long value) {
		return value ^ mode;
	}

	/**
	 * Returns number of elements in this set (it's size).
	 * 
	 * @return number of elements in this set
	 */
	public long size() {
		return size;
	}

	/**
	 * Returns true if this set contains specified value.
	 * 
	 * @param value - value whose presence needs to be checked
	 * @return {@code true} if the set contains given value
	 */
	public boolean contains(long value) {
		value = applyMode(value);
		if (root == null)
			return false;
		return root.contains(BITS_PER_WORD - BRANCH_RADIX, value);
	}

    /**
     * Adds the specified value to this set if it is not already present.<br>
     * 
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param value element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     */
	public boolean add(long value) {
		value = applyMode(value);
		if (root == null) {
			root = new Branch(value);
			return true;
		}
		if (!root.add(BITS_PER_WORD - BRANCH_RADIX, value))
			return false;
		size++;
		return true;
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
	public boolean remove(long value) {
		value = applyMode(value);
		if (root == null || !root.remove(BITS_PER_WORD - BRANCH_RADIX, value))
			return false;
		size--;
		if (size == 0)
			root = null;
		return true;
	}

	/**
	 * Returns value which is equal to or appears after method argument <tt>v</tt>
	 * or <tt>null</tt> if such value doesn't exist in this set.
	 * 
	 * @param v - value to get next for
	 * @return value next to method parameter
	 */
	public Long next(long value) {
		value = applyMode(value);
		if (root == null)
			return null;
		Long next = root.next(BITS_PER_WORD - BRANCH_RADIX, value);
		return (next == null) ? null : reverseMode(next);
	}

    /**
     * Removes all the elements from this set.
     * The set will be empty after this call returns.
     */
	public void clear() {
		root = null;
		size = 0;
	}
	
	/**
	 * Returns comparison result with respect to this set sort-order:<br>
	 * - <tt>negative</tt> value if <tt>v1</tt> is located <tt>before</tt> <tt>v2</tt><br>
	 * - <tt>0</tt> if <tt>v1</tt> is <tt>equal</tt> to <tt>v2</tt> <br>
	 * - <tt>positive</tt> value if <tt>v1</tt> located <tt>after</tt> <tt>v2</tt> 
	 * 
	 * @param v1 - first value to be compared
	 * @param v2 - second value to be compared
	 * @return comparison result
	 */
	public int compare(long v1, long v2) {
		v1 = applyMode(v1);
		v2 = applyMode(v2);
		return (v1 < v2) ? -1 : (v1 == v2) ? 0 : 1;
	}
}
