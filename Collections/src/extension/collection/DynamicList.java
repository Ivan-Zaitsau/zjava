package extension.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time. Removal and insertion of
 * elements at arbitrary index runs in <i>O(sqrt(n)) amortized time</i>
 * In most cases this implementation significantly faster than
 * {@link java.util.ArrayList ArrayList} implementation and require just
 * O(sqrt(n)) of additional memory.
 *
 * @author Ivan Zaitsau
 * @see     Collection
 * @see     List
 */
public class DynamicList<E> extends AbstractList<E> implements List<E>, RandomAccess, java.io.Serializable {

	private static final long serialVersionUID = 2013_01_23_2100L;
	
	static private final int INITIAL_BLOCK_BITSIZE = 3;
	static private final int INITIAL_BLOCKS_COUNT = 2;

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
		
		private final int mask;
		private int offset;
		private int size;
		private final Object[] values;

		Block(int capacity) {
			// - capacity must be a power of two greater that one
			assert((capacity & (capacity-1)) == 0 && capacity > 1);
			
			this.mask = capacity - 1;
			this.offset = 0;
			this.size = 0;
			this.values = new Object[capacity];
		}
		
		private void copyToArray(E[] array, int pos) {
			if (offset + size <= values.length) {
				System.arraycopy(values, offset, array, pos, size);
			}
			else {
				int halfSize = values.length - offset;
				System.arraycopy(values, offset, array, pos, halfSize);
				System.arraycopy(values, 0, array, pos + halfSize, size - halfSize);
			}
		}

		int size() {
			return size;
		}
		
		E add(E value) {
			return add((size < mask) ? size : mask, value);
		}
		
		@SuppressWarnings("unchecked")
		E add(int index, E value) {
			// - range check
			assert(index >= 0 && index <= size);
			
			Object last = size > mask ? values[(offset + mask) & mask] : null;
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
			return (E) last;
		}

		@SuppressWarnings("unchecked")
		E set(int index, E value) {
			// - range check
			assert(index >= 0 && index < size);
			
			int i = (offset + index) & mask;
			E replaced = (E) values[i];
			values[i] = value;
			return replaced;
		}
		
		E remove(int index) {
			// - range check
			assert(index >= 0 && index < size);
			
			E removed = get(index);
			if (2*index < size) {
				for (int i = index; i > 0; i--) {
					values[(offset + i) & mask] = values[(offset + i - 1) & mask];					
				}
				offset = (offset + 1) & mask;
			}
			else {
				for (int i = index + 1; i < size; i++) {
					values[(offset + i - 1) & mask] = values[(offset + i) & mask];
				}
			}
			size--;
			return removed;
		}
		
		@SuppressWarnings("unchecked")
		public E get(int index) {
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
		int blockSize = 1 << blockBitsize;
		data = new Block[INITIAL_BLOCKS_COUNT];
		data[0] = new Block<E>(blockSize);
		totalBlocks = 1;
	}
	
	@SuppressWarnings("unchecked")
	private void ensureCapacity() {
		if (size() < totalBlocks << blockBitsize)
			return;
		if (totalBlocks < data.length) {
			data[totalBlocks++] = new Block<E>(1 << blockBitsize);
		}
		else {
			blockBitsize++;
			Block<E>[] oldData = data;
			data = new Block[2*data.length];
			totalBlocks = 0;
			for (int i = 0; i < oldData.length; i += 2) {
				data[totalBlocks++] = Block.merge(oldData[i], oldData[i+1]);
			}
			data[totalBlocks++] = new Block<E>(1 << blockBitsize);
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
	public boolean add(E e) {
		add(size(), e);
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
		ensureCapacity();
		int blockIndex = index >>> blockBitsize;
		int valueIndex = index - (blockIndex << blockBitsize);
		element = data[blockIndex].add(valueIndex, element);
		for (int i = blockIndex+1; i < totalBlocks; i++) {
			element = data[i].add(0, element);
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
		int valueIndex = index - (blockIndex << blockBitsize);
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
		int valueIndex = index - (blockIndex << blockBitsize);
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
		int valueIndex = index - (blockIndex << blockBitsize);
		E removed = data[blockIndex].remove(valueIndex);
		for (int i = blockIndex+1; i < totalBlocks; i++) {
			data[i-1].add(data[i].remove(0));			
		}
		if (data[totalBlocks-1].size() == 0) totalBlocks--;
		size--;
		return removed;
	}
}