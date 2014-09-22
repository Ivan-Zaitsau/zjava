package extension.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * Resizable array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time. Removal and insertion of
 * elements at arbitrary index runs in <i>O(sqrt(n)) amortized time</i>.<br>
 * In most cases this implementation is significantly faster than
 * {@link java.util.ArrayList ArrayList} implementation and require just
 * O(sqrt(n)) of additional memory.
 *
 * @author Ivan Zaitsau
 * @see     Collection
 * @see     List
 */
public class DynamicList<E> extends AbstractList<E> implements List<E>, RandomAccess, java.io.Serializable {

	private static final long serialVersionUID = 2013_01_23_2100L;
	
	/** Actual initial block size is 2<sup>INITIAL_BLOCK_BITSIZE</sup> */
	static private final int INITIAL_BLOCK_BITSIZE = 3;
	
	/** Number of blocks on DynamicList initialization.*/
	static private final int INITIAL_BLOCKS_COUNT = 2;
	
	/** This coefficient used to check if reduction of block size and amount of blocks is required. <br> <b>Note:</b> Must be no less than 4.*/
	static private final int REDUCTION_COEFFICIENT = 8;

	/**
	 * Internal storage block.<br>
	 * Maintains up to <tt>capacity</tt> objects.<br>
	 * If there are more elements than <tt>capacity</tt> after <tt>add</tt> operation, last element is removed from the block and returned from <tt>add</tt> method.<br>
	 * 
	 * <p>Implementation may vary, but the following conditions must be met:
	 * <li> Insertions and deletions at the beginning and the end of the block must complete in constant time
	 * <li> Retrieval of element at arbitrary position must complete in constant time
	 * <li> Any other operation must complete in O(n)
	 * <li> Only Blocks of sizes 2, 4, 8, ... 2<sup>30</sup> need to be supported
	 * 
	 * @author Ivan Zaitsau
	 */
	static final private class Block<E> implements java.io.Serializable {
		
		private static final long serialVersionUID = 2013_01_23_2100L;
		
		@SuppressWarnings("unchecked")
		static <E> Block<E> merge(Block<E> block1, Block<E> block2) {
			Block<E> mergedBlock = new Block<E>(block1.values.length + block2.values.length);
			mergedBlock.size = block1.size + block2.size;
			block1.copyToArray((E[]) mergedBlock.values, 0);
			block2.copyToArray((E[]) mergedBlock.values, block1.size);
			return mergedBlock;
		}

		@SuppressWarnings("unchecked")
		static <E> Block<E>[] split(Block<E> block) {
			if (block == null || block.size == 0)
				return new Block[] {null, null};
			
			int halfSize = block.values.length / 2;
			
			if (block.size <= halfSize) {
				Block<E> block1 = new Block<E>(halfSize);
				block.copyToArray((E[]) block1.values, 0, 0, block.size);
				block1.size = block.size;
				return new Block[] {block1, null};
			}
			else {
				Block<E> block1 = new Block<E>(halfSize);
				block.copyToArray((E[]) block1.values, 0, 0, halfSize);
				block1.size = halfSize;
				Block<E> block2 = new Block<E>(halfSize);
				block.copyToArray((E[]) block2.values, 0, halfSize, block.size - halfSize);
				block2.size = halfSize;
				return new Block[] {block1, block2};				
			}
		}
		
		private final int mask;
		private int offset;
		private int size;
		private final Object[] values;

		Block(int capacity) {
			// - capacity must be even power of 2
			assert((capacity & (capacity-1)) == 0 && capacity > 1);
			
			this.mask = capacity - 1;
			this.offset = 0;
			this.size = 0;
			this.values = new Object[capacity];
		}

		private void copyToArray(E[] array, int trgPos, int srcPos, int count) {
			int first = (offset + srcPos < values.length) ? offset + srcPos : offset + srcPos - values.length;
			if (first + count <= values.length) {
				System.arraycopy(values, first, array, trgPos, count);
			}
			else {
				int halfCount = values.length - first;
				System.arraycopy(values, first, array, trgPos, halfCount);
				System.arraycopy(values, 0, array, trgPos + halfCount, count - halfCount);
			}
		}

		private void copyToArray(E[] array, int pos) {
			copyToArray(array, pos, 0, size);
		}

		int size() {
			return size;
		}
		
		@SuppressWarnings("unchecked")
		E addFirst(E value) {
			E last = null;
			offset = (offset + mask) & mask;
			if (size > mask)
				last = (E) values[offset];
			else
				size++;
			values[offset] = value;
			return last;
		}
		
		@SuppressWarnings("unchecked")
		E add(E value) {
			E last = null;
			if (size > mask) {
				int i = (offset + mask) & mask;
				last = (E) values[i];
				values[i] = value;
			}
			else {
				values[(offset + size) & mask] = value;
				size++;
			}
			return last;
		}
		
		E add(int index, E value) {
			// - range check
			assert(index >= 0 && index <= size);
			
			@SuppressWarnings("unchecked")			
			E last = size > mask ? (E) values[(offset + mask) & mask] : null;
			if (2*index < size) {
				offset = (offset + mask) & mask;
				for (int i = 0; i < index; i++) {
					values[(offset + i) & mask] = values[(offset + i + 1) & mask];
				}
			}
			else {
				for (int i = (size < mask) ? size : mask; i > index; i--) {
					values[(offset + i) & mask] = values[(offset + i - 1) & mask];
				}
			}
			values[(offset + index) & mask] = value;
			if (size <= mask) size++;
			return last;
		}

		E set(int index, E value) {
			// - range check
			assert(index >= 0 && index < size);
			
			int i = (offset + index) & mask;
			@SuppressWarnings("unchecked")
			E replaced = (E) values[i];
			values[i] = value;
			return replaced;
		}
		
		E removeFirst() {
			// - range check
			assert(size > 0);
			
			@SuppressWarnings("unchecked")
			E removed = (E) values[offset];
			values[offset] = null;
			offset = (offset + 1) & mask;
			size--;
			return removed;
		}
		
		E remove(int index) {
			// - range check
			assert(index >= 0 && index < size);
			
			@SuppressWarnings("unchecked")
			E removed = (E) values[(index + offset) & mask];
			if (2*index < size) {
				for (int i = index; i > 0; i--) {
					values[(offset + i) & mask] = values[(offset + i - 1) & mask];					
				}
				values[offset] = null;
				offset = (offset + 1) & mask;
			}
			else {
				for (int i = index + 1; i < size; i++) {
					values[(offset + i - 1) & mask] = values[(offset + i) & mask];
				}
				values[(offset + size - 1) & mask] = null;
			}
			size--;
			return removed;
		}
		
		@SuppressWarnings("unchecked")
		E get(int index) {
			// - range check
			assert(index >= 0 && index < size);
			
			return (E) values[(index + offset) & mask];
		}
	}

	private int size;
	private int blockBitsize;
	private int totalBlocks;
	private Block<E>[] data;

	@SuppressWarnings("unchecked")
	private void init() {
		size = 0;
		blockBitsize = INITIAL_BLOCK_BITSIZE;
		data = new Block[INITIAL_BLOCKS_COUNT];
		totalBlocks = 0;
	}
	
	private void ensureCapacity(int requiredCapacity) {
		int capacity = totalBlocks << blockBitsize;
		while (requiredCapacity > capacity) {
			if (totalBlocks < data.length) {
				data[totalBlocks++] = new Block<E>(1 << blockBitsize);
			}
			else {
				@SuppressWarnings("unchecked")
				Block<E>[] newData = new Block[2*data.length];
				int newBlocks = 0;
				int newBlockBitsize = blockBitsize+1;
				for (int i = 1; i < data.length; i += 2) {
					newData[newBlocks++] = Block.merge(data[i-1], data[i]);
				}
				newData[newBlocks++] = new Block<E>(1 << newBlockBitsize);
				if ((data.length & 1) > 0) {
					Block<E> oldBlock = data[data.length-1];
					Block<E> newBlock = newData[newBlocks-1];
					for (int i = 0; i < oldBlock.size(); i++) {
						newBlock.add(oldBlock.get(i));
					}
				}
				data = newData;
				totalBlocks = newBlocks;
				blockBitsize = newBlockBitsize;
			}
			capacity = totalBlocks << blockBitsize;
		}
	}
	
	private void compact() {
		if (data.length <= INITIAL_BLOCKS_COUNT)
			return;
		if (REDUCTION_COEFFICIENT * totalBlocks <= data.length) {
			@SuppressWarnings("unchecked")
			Block<E>[] newData = new Block[data.length/2];
			int newBlockBitsize = blockBitsize-1;
			int newBlocks = 0;
			for (int i = 0; i < totalBlocks; i++) {
				Block<E>[] splitBlock = Block.split(data[i]);
				for (int j = 0; j <= 1; j++)
					if (splitBlock[j] != null && splitBlock[j].size() > 0)
						newData[newBlocks++] = splitBlock[j];
			}
			data = newData;
			totalBlocks = newBlocks;
			blockBitsize = newBlockBitsize;
		}
	}
	
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }
	
	private void rangeCheck(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));			
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

    /**
     * Constructs an empty list with an initial capacity of 16 elements.
     */
	public DynamicList() {
		init();
	}
	
    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
	public int size() {
		return size;
	}

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
	public boolean add(E element) {
		ensureCapacity(size + 1);
		data[size >>> blockBitsize].add(element);
		size++;
		return true;
	}

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public void add(int index, E element) {
		rangeCheckForAdd(index);
		ensureCapacity(size + 1);
		int blockIndex = index >>> blockBitsize;
		int valueIndex = index & (-1 >>> -blockBitsize);
		element = data[blockIndex].add(valueIndex, element);
		while (++blockIndex < totalBlocks) {
			element = data[blockIndex].addFirst(element);
		}
		size++;
	}

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public E get(int index) {
		rangeCheck(index);
		int blockIndex = index >>> blockBitsize;
		int valueIndex = index & (-1 >>> -blockBitsize);
		return data[blockIndex].get(valueIndex);
	}

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public E set(int index, E element) {
		rangeCheck(index);
		int blockIndex = index >>> blockBitsize;
		int valueIndex = index & (-1 >>> -blockBitsize);
		return data[blockIndex].set(valueIndex, element);
	}

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
	public E remove(int index) {
		rangeCheck(index);
		int blockIndex = index >>> blockBitsize;
		int valueIndex = index & (-1 >>> -blockBitsize);
		E removed = data[blockIndex].remove(valueIndex);
		while (++blockIndex < totalBlocks) {
			data[blockIndex-1].add(data[blockIndex].removeFirst());
		}
		size--;
		if (totalBlocks > 0 && data[totalBlocks-1].size() == 0) {
			data[--totalBlocks] = null;
			compact();
		};
		return removed;
	}
}