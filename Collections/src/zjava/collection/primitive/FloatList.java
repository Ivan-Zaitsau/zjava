package zjava.collection.primitive;

import java.util.Arrays;

/**
 * Resizable dynamic array of primitive float values.
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
 * <li>Support for arrays of more than {@code Integer.MAX_VALUE} elements.</li>
 * 
 * @since Zjava 1.0
 *  
 * @author Ivan Zaitsau
 * 
 */
public class FloatList implements Cloneable, java.io.Serializable {

	static private final long serialVersionUID = 201503092100L;
	
	/** Actual initial block size is 2<sup>INITIAL_BLOCK_ADDRESS_BITS</sup> */
	static private final int INITIAL_BLOCK_ADDRESS_BITS = 5;
	
	/** Number of blocks on FloatList initialization.
	 * <br> <b>Note:</b> Must be even number due to some simplifications and assumptions made in the code*/
	static private final int INITIAL_BLOCKS_COUNT = 2;
	
	/** This coefficient used to check if reduction of block size and amount of blocks is required.
	 * <br> <b>Note:</b> Must be no less than 4. Needs to be no less than 8 for amortized performance estimations to hold */
	static private final int REDUCTION_COEFFICIENT = 12;

    /**
	 * Internal storage block.<br>
	 * Maintains up to <tt>capacity</tt> float values.<br>
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
	static final private class Block implements Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 201503092100L;
		
		// - merges two blocks of equal capacities into block with doubled capacity
		static Block merge(Block block1, Block block2) {
			if ((block1 == null || block1.size() == 0) && (block2 == null || block2.size() == 0))
				return null;

			assert (block1 == null | block2 == null) || (block1.values.length == block2.values.length);

			Block mergedBlock = new Block(2 * ((block1 == null) ? block2.values.length : block1.values.length));
			if (block1 != null)
				mergedBlock.size += block1.copyToArray(mergedBlock.values, 0);
			if (block2 != null)
				mergedBlock.size += block2.copyToArray(mergedBlock.values, mergedBlock.size);
			return mergedBlock;
		}

		// - splits block to two smaller blocks of capacity equal to half of given block
		static Block[] split(Block block) {
			if (block == null || block.size == 0)
				return new Block[] {null, null};
			
			assert (block.values.length & 1) == 0;
			
			int halfSize = block.values.length / 2;
			
			if (block.size <= halfSize) {
				Block block1 = new Block(halfSize);
				block.copyToArray(block1.values, 0, 0, block.size);
				block1.size = block.size;
				return new Block[] {block1, null};
			}
			else {
				Block block1 = new Block(halfSize);
				block.copyToArray(block1.values, 0, 0, halfSize);
				block1.size = halfSize;
				Block block2 = new Block(halfSize);
				block.copyToArray(block2.values, 0, halfSize, block.size - halfSize);
				block2.size = halfSize;
				return new Block[] {block1, block2};				
			}
		}
		
		private int offset;
		private int size;
		private float[] values;

		Block(int capacity) {
			// - capacity must be even power of 2
			assert((capacity & (capacity-1)) == 0 && capacity > 1);
			
			this.offset = 0;
			this.size = 0;
			this.values = new float[capacity];
		}
		
		// - copies "count" values of this block starting from "srcPos" to array starting from "trgPos" index
		int copyToArray(float[] array, int trgPos, int srcPos, int count) {
			if (srcPos >= values.length | count <= 0)
				return 0;
			if (srcPos + count > values.length)
				count = values.length - srcPos;
			int first = (offset + srcPos < values.length) ? offset + srcPos : offset + srcPos - values.length;
			if (first + count <= values.length) {
				System.arraycopy(values, first, array, trgPos, count);
			}
			else {
				int halfCount = values.length - first;
				System.arraycopy(values, first, array, trgPos, halfCount);
				System.arraycopy(values, 0, array, trgPos + halfCount, count - halfCount);
			}
			return count;
		}

		// - copies all values of this block to given array starting from "pos" index
		int copyToArray(float[] array, int pos) {
			return copyToArray(array, pos, 0, size);
		}

		int size() {
			return size;
		}
		
		// - returns "physical" index for given "logical" position
		int index(final int pos) {
			return (offset + pos) & (values.length - 1);
		}

		// - appends given value to the beginning of the block
		float addFirst(final float value) {
			offset = index(-1);
			float last = values[offset];
			values[offset] = value;
			if (size < values.length) {
				size++;
			}
			return last;
		}
		
		// - appends given value to the end of the block
		void addLast(final float value) {
			if (size == values.length)
				return;
			values[index(size)] = value;
			size++;
		}
		
		// - inserts given value at given position
		float add(final int pos, final float value) {
			// - range check
			assert(pos >= 0 && pos <= size);
			
			float last = (size == values.length) ? values[index(-1)] : 0;
			if (2*pos < size) {
				offset = index(-1);
				for (int i = 0; i < pos; i++) {
					values[index(i)] = values[index(i+1)];
				}
			}
			else {
				for (int i = (size == values.length) ? size-1 : size; i > pos; i--) {
					values[index(i)] = values[index(i-1)];
				}
			}
			values[index(pos)] = value;
			if (size < values.length) size++;
			return last;
		}

		// - replaces element at given position with given value
		float set(final int pos, final float value) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			int i = index(pos);
			float replaced = values[i];
			values[i] = value;
			return replaced;
		}
		
		// - removes first element of the block
		float removeFirst() {
			// - range check
			assert(size > 0);
			
			float removed = values[offset];
			offset = index(1);
			size--;
			return removed;
		}
		
		// - removes element at given position
		float remove(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			float removed = values[index(pos)];
			if (2*pos < size) {
				for (int i = pos; i > 0; i--) {
					values[index(i)] = values[index(i-1)];					
				}
				offset = index(1);
			}
			else {
				for (int i = pos + 1; i < size; i++) {
					values[index(i-1)] = values[index(i)];
				}
			}
			size--;
			return removed;
		}
		
		// - returns element at given position
		float get(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			return values[index(pos)];
		}
		
		public Object clone() {
			try {
				Block clone = (Block) super.clone();
				clone.values = Arrays.copyOf(values, values.length);
				return clone;
			} catch (CloneNotSupportedException e) {
	    		// - this should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private long size;
	private int blockAddressBits;
	private Block[] data;

    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + size;
    }
	
	private void rangeCheck(long index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void rangeCheckForAdd(long index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/** Null-safe access to data block with initialization.*/
	private Block data(int index) {
		if (data[index] == null)
			data[index] = new Block(1 << blockAddressBits);
		return data[index];
	}
	
	private void ensureCapacity(long requiredCapacity) {
		long capacity = (long) data.length << blockAddressBits;
		while (requiredCapacity > capacity) {
			// - double number of blocks and their size
			Block[] newData = new Block[2*data.length];
			int newBlockBitsize = blockAddressBits+1;
			for (int i = 1, j = 0; i < data.length; i += 2, j++) {
				newData[j] = Block.merge(data[i-1], data[i]);
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
			Block[] newData = new Block[(data.length+1)/2];
			int newBlockBitsize = blockAddressBits-1;
			main:
			for (int i = 0; ; i++) {
				Block[] splitBlock = Block.split(data[i]);
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
		data = new Block[blocksCount];
	}

	/**
     * Constructs an empty list with an initial capacity of 32 elements.
     */
	public FloatList() {
		init();
	}

	/**
     * Constructs an empty list with at least specified capacity.
     */
	public FloatList(long initialCapacity) {
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
	public float get(final long index) {
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
	public float set(final long index, float value) {
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
	public void add(float value) {
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
	public void add(final long index, float value) {
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
	public float remove(final long index) {
		rangeCheck(index);
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		float removed = data[blockIndex].remove(valueIndex);
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
     * Returns a copy of this <tt>FloatList</tt> instance.
     *
     * @return a clone of this <tt>FloatList</tt> instance
     */
	public Object clone() {
    	try {
			FloatList clone = (FloatList) super.clone();
			clone.data = new Block[data.length];
    		for (int i = 0; i < data.length && data[i] != null; i++) {
    			clone.data[i] = (Block) data[i].clone();
    		}
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }
}
