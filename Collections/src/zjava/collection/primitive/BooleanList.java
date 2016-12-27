package zjava.collection.primitive;

import java.util.Arrays;

import zjava.system.Const;

/**
 * Resizable dynamic array of primitive boolean values.
 *
 * <p>The <tt>size</tt>, <tt>get</tt>, <tt>set</tt> operations run in
 * constant time. The <tt>add</tt> operation runs in <i>amortized constant
 * time</i>, that is, adding n elements requires O(n) time. Removal and
 * insertion of elements at arbitrary index runs in <i>O(sqrt(n)) amortized
 * time</i>.
 * 
 * <p>Basically, this class has the following advantages over regular array:
 * <li>Resizable implementation which requires only O(sqrt(n)) of additional
 * memory.</li>
 * <li>Faster insertions at random positions.</li>
 * <li>Support for more than {@code Integer.MAX_VALUE} elements.</li>
 * <li>Uses only 1 bit of memory per value
 * 
 * @since Zjava 1.0
 *  
 * @author Ivan Zaitsau
 * 
 */
public class BooleanList implements Cloneable, java.io.Serializable {

	static private final long serialVersionUID = 201612271645L;
	
	/** Actual initial block size is 2<sup>INITIAL_BLOCK_ADDRESS_BITS</sup> */
	static private final int INITIAL_BLOCK_ADDRESS_BITS = 10;
	
	/** Number of blocks on BooleanList initialization.
	 * <br> <b>Note:</b> Must be even number due to some simplifications and assumptions made in the code*/
	static private final int INITIAL_BLOCKS_COUNT = 2;
	
	/** This coefficient used to check if reduction of block size and amount of blocks is required.
	 * <br> <b>Note:</b> Must be no less than 4. Needs to be no less than 8 for amortized performance estimations to hold */
	static private final int REDUCTION_COEFFICIENT = 12;

    /**
	 * Internal storage block.<br>
	 * Maintains up to <tt>capacity</tt> boolean values.<br>
	 * If there are more elements than <tt>capacity</tt> after <tt>add</tt> operation,
	 * last element is removed from the block and returned from <tt>add</tt> method.<br>
	 * 
	 * <p>Implementation may vary, but the following conditions must be met:
	 * <li> Insertions and deletions at the beginning and the end of the block must complete in constant time
	 * <li> Retrieval of element at arbitrary position must complete in constant time
	 * <li> Any other operation must complete in O(n)
	 * <li> Only Blocks of sizes 2, 4, 8, ... 2<sup>30</sup> need to be supported
	 * 
	 * @author Ivan Zaitsau
	 */
	static final private class BinaryBlock implements Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 201612271645L;
		
		private static final int ADDRESS_BITS_PER_WORD = Const.ADDRESS_BITS_PER_LONG;
		private static final int WORD_MASK = (1 << ADDRESS_BITS_PER_WORD) - 1;
		
		// - partial logical shift to left
		private static long pshl(final long value, final int fromIndex, final int toIndex) {
			final long rangeMask = (((1L << toIndex) << 1) - 1) - ((1L << fromIndex) - 1);
			return (value & ~rangeMask) | (((value & rangeMask) << 1) & rangeMask);
		}
		
		// - partial logical shift to right
		private static long pshr(final long value, final int fromIndex, final int toIndex) {
			final long rangeMask = (((1L << toIndex) << 1) - 1) - ((1L << fromIndex) - 1);
			return (value & ~rangeMask) | (((value & rangeMask) >>> 1) & rangeMask);
		}
		
		// - merges two blocks of equal capacities into block with doubled capacity
		static BinaryBlock merge(BinaryBlock block1, BinaryBlock block2) {
			if ((block1 == null || block1.size() == 0) && (block2 == null || block2.size() == 0))
				return null;

			assert (block1 == null | block2 == null) || (block1.values.length == block2.values.length);

			if (block1 == null || block1.size == 0) {
				block1 = block2;
				block2 = null;
			}
			BinaryBlock mergedBlock = new BinaryBlock(2 * block1.values.length);
			
			// - copy bits from block1
			final int blockMask = (block1.values.length - 1);
			int remainingBits = block1.size & WORD_MASK;
			int wordSplit = block1.offset & WORD_MASK;
			int fullWords = block1.size >>> ADDRESS_BITS_PER_WORD;
			int wordOffset = block1.offset >>> ADDRESS_BITS_PER_WORD;
			if (wordSplit == 0) {
				for (int wi = 0; wi < fullWords; wi++) {
					mergedBlock.values[wi] = block1.values[(wordOffset + wi) & blockMask];
				}
				if (remainingBits > 0) {
					mergedBlock.values[fullWords] = block1.values[(wordOffset + fullWords) & blockMask]
							& ((1L << remainingBits) - 1);
				}
			}
			else {
				for (int wi = 0; wi < fullWords; wi++) {
					mergedBlock.values[wi] = (block1.values[(wordOffset + wi) & blockMask] >>> wordSplit)
							| (block1.values[(wordOffset + wi + 1) & blockMask] << -wordSplit);
				}
				if (remainingBits > 0) {
					mergedBlock.values[fullWords] = ((block1.values[(wordOffset + fullWords) & blockMask] >>> wordSplit)
							| (block1.values[(wordOffset + fullWords + 1) & blockMask] << -wordSplit))
							& ((1L << remainingBits) - 1);
				}
			}
			mergedBlock.size += block1.size;

			if (block2 == null || block2.size == 0)
				return mergedBlock;

			// - copy bits from block2
			final int bitsToCompleteWord = (remainingBits > 0) ? (1 << ADDRESS_BITS_PER_WORD) - remainingBits : 0;
			// - finish uncompleted word first
			if (remainingBits > 0) {
				if (bitsToCompleteWord > block2.size) {
					
				}

			}

			final int mergedWords = fullWords;
			remainingBits = (block2.size - bitsToCompleteWord) & WORD_MASK;
			wordSplit = (block2.offset + bitsToCompleteWord) & WORD_MASK;
			fullWords = (block2.size - bitsToCompleteWord) >>> ADDRESS_BITS_PER_WORD;
			wordOffset = (block2.offset + bitsToCompleteWord) >>> ADDRESS_BITS_PER_WORD;
			
			if (wordSplit == 0) {
				for (int wi = 0; wi < fullWords; wi++) {
					mergedBlock.values[mergedWords + wi] = block2.values[(wordOffset + wi) & blockMask];
				}
				if (remainingBits > 0) {
					mergedBlock.values[mergedWords + fullWords] = block2.values[(wordOffset + fullWords) & blockMask]
							& ((1L << remainingBits) - 1);
				}
			}
			else {
				for (int wi = 0; wi < fullWords; wi++) {
					mergedBlock.values[mergedWords + wi] = (block2.values[(wordOffset + wi) & blockMask] >>> wordSplit)
							| (block2.values[(wordOffset + wi + 1) & blockMask] << -wordSplit);
				}
				if (remainingBits > 0) {
					mergedBlock.values[mergedWords + fullWords] = ((block2.values[(wordOffset + fullWords) & blockMask] >>> wordSplit)
							| (block2.values[(wordOffset + fullWords + 1) & blockMask] << -wordSplit))
							& ((1L << remainingBits) - 1);
				}
			}
			mergedBlock.size += block2.size;

			return mergedBlock;
		}

		// - splits block to two smaller blocks of capacity equal to half of given block
		static BinaryBlock[] split(BinaryBlock block) {
			if (block == null || block.size == 0)
				return new BinaryBlock[] {null, null};
			
			assert (block.values.length & 1) == 0;
			
			final int halfCapacity = (block.values.length / 2) << ADDRESS_BITS_PER_WORD;
			final int blockMask = (block.values.length - 1);
			final int wordSplit = block.offset & WORD_MASK;
			final int remainingBits = block.size & WORD_MASK;
			final int wordOffset = block.offset >>> ADDRESS_BITS_PER_WORD;
			final int fullWords = block.size >>> ADDRESS_BITS_PER_WORD;
			
			if (block.size <= halfCapacity) {
				BinaryBlock block1 = new BinaryBlock(halfCapacity);
				if (wordSplit == 0) {
					for (int wi = 0; wi < fullWords; wi++) {
						block1.values[wi] = block.values[(wordOffset + wi) & blockMask];
					}
					if (remainingBits > 0) {
						block1.values[fullWords] = block.values[(wordOffset + fullWords) & blockMask]
								& ((1L << remainingBits) - 1);
					}
				}
				else {
					for (int wi = 0; wi < fullWords; wi++) {
						block1.values[wi] = (block.values[(wordOffset + wi) & blockMask] >>> wordSplit)
								+ (block.values[(wordOffset + wi + 1) & blockMask] << -wordSplit);
					}
					if (remainingBits > 0) {
						block1.values[fullWords] = ((block.values[(wordOffset + fullWords) & blockMask] >>> wordSplit)
								+ (block.values[(wordOffset + fullWords + 1) & blockMask] << -wordSplit))
								& ((1L << remainingBits) - 1);
					}
				}
				block1.size = block.size;
				return new BinaryBlock[] {block1, null};
			}
			else {
				BinaryBlock block1 = new BinaryBlock(halfCapacity);
				if (wordSplit == 0) {
					for (int wi = 0; wi < block1.values.length; wi++) {
						block1.values[wi] = block.values[(wordOffset + wi) & blockMask];
					}
				}
				else {
					for (int wi = 0; wi < block1.values.length; wi++) {
						block1.values[wi] = (block.values[(wordOffset + wi) & blockMask] >>> wordSplit)
								+ (block.values[(wordOffset + wi + 1) & blockMask] << -wordSplit);
					}
				}
				block1.size = halfCapacity;

				BinaryBlock block2 = new BinaryBlock(halfCapacity);
				if (wordSplit == 0) {
					for (int wi = 0; wi < fullWords; wi++) {
						block2.values[wi - block1.values.length] = block.values[(wordOffset + wi) & blockMask];
					}
					if (remainingBits > 0) {
						block2.values[fullWords - block1.values.length] = block.values[(wordOffset + fullWords) & blockMask]
								& ((1L << remainingBits) - 1);
					}
				}
				else {
					for (int wi = 0; wi < fullWords; wi++) {
						block2.values[wi - block1.values.length] = (block.values[(wordOffset + wi) & blockMask] >>> wordSplit)
								+ (block.values[(wordOffset + wi + 1) & blockMask] << -wordSplit);
					}
					if (remainingBits > 0) {
						block2.values[fullWords - block1.values.length] = ((block.values[(wordOffset + fullWords) & blockMask] >>> wordSplit)
								+ (block.values[(wordOffset + fullWords + 1) & blockMask] << -wordSplit))
								& ((1L << remainingBits) - 1);
					}
				}
				block2.size = block.size - halfCapacity;
				return new BinaryBlock[] {block1, block2};
			}
		}

		private int offset;
		private int size;
		private long[] values;

		BinaryBlock(int capacity) {
			// - capacity must be power of 2, greater than 64 (i.e. must contain at least two "words")
			assert((capacity & (capacity-1)) == 0 && capacity > 1 << ADDRESS_BITS_PER_WORD);
			
			this.offset = 0;
			this.size = 0;
			this.values = new long[capacity >>> ADDRESS_BITS_PER_WORD];
		}
		
		int size() {
			return size;
		}
		
		// - returns maximum amount of values this BinaryBlock can hold
		private int capacity() {
			return values.length << ADDRESS_BITS_PER_WORD;
		}
		
		// - returns "physical" index for given "logical" position
		private int index(final int pos) {
			return (offset + pos) & (capacity() - 1);
		}
		
		private boolean getBit(final int index) {
			return (values[index >>> ADDRESS_BITS_PER_WORD] & (1L << (index & WORD_MASK))) != 0;
		}
		
		// - updates given bit at the specified index to given value
		// - returns the value previously at the specified index
		private boolean setBit(final int index, final boolean value) {
			final int wi = index >>> ADDRESS_BITS_PER_WORD;
			final long beforeUpdate = values[wi];
			if (value) {
				// - 1 bit
				values[wi] |= 1L << (index & WORD_MASK);
				return values[wi] == beforeUpdate;
			}
			else {
				// - 0 bit
				values[wi] &= ~(1L << (index & WORD_MASK));
				return values[wi] != beforeUpdate;
			}
		}
		
		// - appends given value to the beginning of the block
		boolean addFirst(final boolean value) {
			offset = index(-1);
			if (size < capacity()) {
				setBit(offset, value);
				size++;
				return false;
			}
			return setBit(offset, value);
		}
		
		// - appends given value to the end of the block
		void addLast(final boolean value) {
			if (size == capacity())
				return;
			setBit(index(size), value);
			size++;
		}
		
		// - shifts values in direction of the beginning of the values array
		private void shiftToLeft(final int fromPosition, final int toPosition) {
			final int fromIndex = index(fromPosition);
			final int firstWordIndex = fromIndex >>> ADDRESS_BITS_PER_WORD;
			final int toIndex = index(toPosition);
			final int lastWordIndex = toIndex >>> ADDRESS_BITS_PER_WORD;
			if (firstWordIndex == lastWordIndex & fromIndex < toIndex) {
				// - requested shift lies within the first "word"
				values[firstWordIndex] = pshr(values[firstWordIndex], fromIndex & WORD_MASK, toIndex & WORD_MASK);
				return;
			}
			values[firstWordIndex] = pshr(values[firstWordIndex], fromIndex & WORD_MASK, WORD_MASK)
					+ (values[(firstWordIndex + 1) & (values.length - 1)] << WORD_MASK);
			for (int wi = (firstWordIndex + 1) & (values.length - 1); wi != lastWordIndex; ) {
				final int ni = (wi + 1) & (values.length - 1);
				values[wi] = (values[wi] >>> 1) + (values[ni] << WORD_MASK);
				wi = ni;
			}
			values[lastWordIndex] = pshr(values[lastWordIndex], 0, toIndex & WORD_MASK);
		}

		// - shifts values in direction of the end of the values array
		private void shiftToRight(final int fromPosition, final int toPosition) {
			final int fromIndex = index(fromPosition);
			final int firstWordIndex = fromIndex >>> ADDRESS_BITS_PER_WORD;
			final int toIndex = index(toPosition);
			final int lastWordIndex = toIndex >>> ADDRESS_BITS_PER_WORD;
			if (firstWordIndex == lastWordIndex & fromIndex < toIndex) {
				// - requested shift lies within the first "word"
				values[firstWordIndex] = pshl(values[firstWordIndex], fromIndex & WORD_MASK, toIndex & WORD_MASK);
				return;
			}
			values[lastWordIndex] = pshl(values[lastWordIndex], 0, toIndex & WORD_MASK)
					+ (values[(lastWordIndex - 1) & (values.length - 1)] >>> WORD_MASK);
			for (int wi = (lastWordIndex - 1) & (values.length - 1); wi != lastWordIndex; ) {
				final int ni = (wi - 1) & (values.length - 1);
				values[wi] = (values[wi] << 1) + (values[ni] >>> WORD_MASK);
				wi = ni;
			}
			values[firstWordIndex] = pshl(values[firstWordIndex], fromIndex & WORD_MASK, WORD_MASK);	
		}

		// - inserts given value at given position
		boolean add(final int pos, final boolean value) {
			// - range check
			assert(pos >= 0 && pos <= size);
			
			final boolean last = (size == capacity()) ? getBit(index(-1)) : false;
			if (2*pos < size) {
				offset = index(-1);
				shiftToLeft(0, pos);
			}
			else {
				shiftToRight(pos, (size == capacity() ? size-1 : size));
			}
			setBit(index(pos), value);
			if (size < capacity()) size++;
			return last;
		}

		// - replaces element at given position with given value
		boolean set(final int pos, final boolean value) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			return setBit(index(pos), value);
		}
		
		// - removes first element of the block
		boolean removeFirst() {
			// - range check
			assert(size > 0);
			
			boolean removed = getBit(offset);
			offset = index(1);
			size--;
			return removed;
		}
		
		// - removes element at given position
		boolean remove(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			boolean removed = getBit(index(pos));
			if (2*pos < size) {
				shiftToRight(0, pos);
				offset = index(1);
			}
			else {
				shiftToLeft(pos, size-1);
			}
			size--;
			return removed;
		}
		
		// - returns element at given position
		boolean get(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			return getBit(index(pos));
		}
		
		public Object clone() {
			try {
				BinaryBlock clone = (BinaryBlock) super.clone();
				clone.values = values.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
	    		// - this should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private long size;
	private int blockAddressBits;
	private BinaryBlock[] data;

    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + size;
    }
	
	private void rangeCheck(final long index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void rangeCheckForAdd(final long index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/** Null-safe access to data block with initialization.*/
	private BinaryBlock data(final int index) {
		if (data[index] == null)
			data[index] = new BinaryBlock(1 << blockAddressBits);
		return data[index];
	}
	
	private void ensureCapacity(long requiredCapacity) {
		long capacity = (long) data.length << blockAddressBits;
		while (requiredCapacity > capacity) {
			// - double number of blocks and their size
			BinaryBlock[] newData = new BinaryBlock[2*data.length];
			int newBlockBitsize = blockAddressBits+1;
			for (int i = 1, j = 0; i < data.length; i += 2, j++) {
				newData[j] = BinaryBlock.merge(data[i-1], data[i]);
				if (newData[j] == null)
					break;
			}
			/* Redundant because data.length assumed to be even number
			 * *deleted code*
			*/
			assert (data.length & 1) == 0;
			
			data = newData;
			blockAddressBits = newBlockBitsize;
			capacity = (long) data.length << blockAddressBits;
		}
	}
	
	private void compact() {
		if (data.length <= INITIAL_BLOCKS_COUNT)
			return;
		if (size * REDUCTION_COEFFICIENT <= (long) data.length << blockAddressBits) {
			// - decrease number of blocks and their size by half
			BinaryBlock[] newData = new BinaryBlock[(data.length+1)/2];
			int newBlockBitsize = blockAddressBits-1;
			main:
			for (int i = 0; ; i++) {
				BinaryBlock[] splitBlock = BinaryBlock.split(data[i]);
				for (int j = 0; j <= 1; j++) {
					if (splitBlock[j] == null || splitBlock[j].size() == 0)
						break main;
					newData[i+i+j] = splitBlock[j];
				}
			}
			data = newData;
			blockAddressBits = newBlockBitsize;
		}
	}
	
	private void init() {
		init(0);
	}
	
	private void init(long initialCapacity) {
		size = 0;
		blockAddressBits = INITIAL_BLOCK_ADDRESS_BITS;
		int blocksCount = INITIAL_BLOCKS_COUNT;
		while ((long)blocksCount << blockAddressBits < initialCapacity) {
			blocksCount += blocksCount;
			blockAddressBits++;
		}
		data = new BinaryBlock[blocksCount];
	}

	/**
     * Constructs an empty list with an initial capacity of 32 elements.
     */
	public BooleanList() {
		init();
	}

	/**
     * Constructs an empty list with at least specified capacity.
     */
	public BooleanList(long initialCapacity) {
		init(initialCapacity);
	}

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
	public long size() {
		return size;
	}

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public boolean get(long index) {
		rangeCheck(index);
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		return data[blockIndex].get(valueIndex);
	}
	
    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param value element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public boolean set(long index, boolean value) {
		rangeCheck(index);
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		return data[blockIndex].set(valueIndex, value);
	}
	
    /**
     * Appends the specified element to the end of this list.
     *
     * @param value element to be appended to this list
     */
	public void add(boolean value) {
		ensureCapacity(size + 1);
		int blockIndex = (int) (size >>> blockAddressBits);
		data(blockIndex).addLast(value);
		size++;
	}

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param value element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public void add(long index, boolean value) {
		rangeCheckForAdd(index);
		ensureCapacity(size + 1);
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		int blockSize = 1 << blockAddressBits;
		if (data(blockIndex).size() < blockSize) {
			data[blockIndex].add(valueIndex, value);
		}
		else {
			value = data[blockIndex].add(valueIndex, value);
			while (data(++blockIndex).size() == blockSize) {
				value = data[blockIndex].addFirst(value);
			}
			data[blockIndex].addFirst(value);
		}
		size++;
	}

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public boolean remove(long index) {
		rangeCheck(index);
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		boolean removed = data[blockIndex].remove(valueIndex);
		while (++blockIndex < data.length && data[blockIndex] != null && data[blockIndex].size() > 0) {
			data[blockIndex-1].addLast(data[blockIndex].removeFirst());
		}
		size--;
		// - free unused blocks for GC and compact list if needed
		boolean blockFreed = false;
		while (++blockIndex < data.length && data[blockIndex] != null && data[blockIndex].size() == 0) {
			data[blockIndex] = null;
			blockFreed = true;
		}
		if (blockFreed)
			compact();
		return removed;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns.
	 */
	public void clear() {
		Arrays.fill(data, null);
		size = 0;
	}

    /**
     * Returns a copy of this <tt>BooleanList</tt> instance.
     *
     * @return a clone of this <tt>BooleanList</tt> instance
     */
	public Object clone() {
    	try {
			BooleanList clone = (BooleanList) super.clone();
			clone.data = new BinaryBlock[data.length];
    		for (int i = 0; i < data.length && data[i] != null; i++) {
    			clone.data[i] = (BinaryBlock) data[i].clone();
    		}
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }
}
