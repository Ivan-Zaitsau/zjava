package extension.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
public class DynamicList<E> extends AbstractList<E> implements List<E>, HugeCapacityList<E>, RandomAccess, java.io.Serializable {

	private static final long serialVersionUID = 2013_01_23_2100L;
	
	/** Actual initial block size is 2<sup>INITIAL_BLOCK_BITSIZE</sup> */
	static private final int INITIAL_BLOCK_BITSIZE = 4;
	
	/** Number of blocks on DynamicList initialization.*/
	static private final int INITIAL_BLOCKS_COUNT = 1;
	
	/** This coefficient used to check if reduction of block size and amount of blocks is required.
	 * <br> <b>Note:</b> Must be no less than 4. Needs to be no less than 8 to hold */
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
		
		static <E> Block<E> merge(Block<E> block1, Block<E> block2) {
			Block<E> mergedBlock = new Block<E>(block1.values.length + block2.values.length);
			mergedBlock.size = block1.size + block2.size;
			block1.copyToArray(mergedBlock.values, 0);
			block2.copyToArray(mergedBlock.values, block1.size);
			return mergedBlock;
		}

		@SuppressWarnings("unchecked")
		static <E> Block<E>[] split(Block<E> block) {
			if (block == null || block.size == 0)
				return new Block[] {null, null};
			
			int halfSize = block.values.length / 2;
			
			if (block.size <= halfSize) {
				Block<E> block1 = new Block<E>(halfSize);
				block.copyToArray(block1.values, 0, 0, block.size);
				block1.size = block.size;
				return new Block[] {block1, null};
			}
			else {
				Block<E> block1 = new Block<E>(halfSize);
				block.copyToArray(block1.values, 0, 0, halfSize);
				block1.size = halfSize;
				Block<E> block2 = new Block<E>(halfSize);
				block.copyToArray(block2.values, 0, halfSize, block.size - halfSize);
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

		int copyToArray(Object[] array, int trgPos, int srcPos, int count) {
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

		int copyToArray(Object[] array, int pos) {
			return copyToArray(array, pos, 0, size);
		}

		int size() {
			return size;
		}
		
		E addFirst(E value) {
			offset = (offset - 1) & mask;
			@SuppressWarnings("unchecked")
			E last = (E) values[offset];
			values[offset] = value;
			if (size <= mask) {
				size++;
			}
			return last;
		}
		
		void add(E value) {
			if (size > mask)
				return;
			values[(offset + size) & mask] = value;
			size++;
		}
		
		E add(int index, E value) {
			// - range check
			assert(index >= 0 && index <= size);
			
			@SuppressWarnings("unchecked")			
			E last = size > mask ? (E) values[(offset - 1) & mask] : null;
			if (2*index < size) {
				offset = (offset - 1) & mask;
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
			E removed = (E) values[(offset + index) & mask];
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
			
			return (E) values[(offset + index) & mask];
		}
	}

	private long size;
	private int totalBlocks;
	private int blockBitsize;
	private Block<E>[] data;
	
	transient private FarListAccess<E> farAccess;
	
	private class FarAccess implements FarListAccess<E> {
		
		public long size() {
			return size;
		}

		public E get(long index) {
			rangeCheck(index);
			return fastGet(index);
		}

		public E set(long index, E element) {
			rangeCheck(index);
			return fastSet(index, element);
		}

		public void add(long index, E element) {
			rangeCheckForAdd(index);
			ensureCapacity(size + 1);
			fastAdd(index, element);
		}

		public E remove(long index) {
			rangeCheck(index);
			return fastRemove(index);
		}
	};

	private class Iter implements Iterator<E> {

		/**
		 * Cursor position
		 */
		long i = 0;
		
		/**
		 * Current (last returned) element index or -1 if element is not defined (or has been removed)
		 */
		long last = -1;
		
		/**
		 * Expected version (modifications count) of the backing List, must 
		 */
		int expectedModCount = modCount;
		
		public boolean hasNext() {
			return i < size;
		}

        public E next() {
            checkForComodification();
            try {
                rangeCheck(i);
                E next = fastGet(i);
                last = i++;
                return next;
            }
            catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (last < 0)
                throw new IllegalStateException();
            checkForComodification();
            
            try {
            	DynamicList.this.remove(last);
                if (last < i) i--;
                last = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
	
		void checkForComodification() {
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
	}
	
	@Override
	public FarListAccess<E> far() {
		if (farAccess == null)
			farAccess = new FarAccess();
		return farAccess;
	}

	private void ensureCapacity(long requiredCapacity) {
		long capacity = (long) totalBlocks << blockBitsize;
		while (requiredCapacity > capacity) {
			modCount++;
			if (totalBlocks < data.length) {
				data[totalBlocks++] = new Block<E>(1 << blockBitsize);
			}
			else {
				// - double number of blocks and their size
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
			modCount++;
			// - decrease number of blocks and their size by half
			@SuppressWarnings("unchecked")
			Block<E>[] newData = new Block[(data.length+1)/2];
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

    /**
     * Constructs an empty list with an initial capacity of 16 elements.
     */
	@SuppressWarnings("unchecked")
	public DynamicList() {
		size = 0;
		blockBitsize = INITIAL_BLOCK_BITSIZE;
		data = new Block[INITIAL_BLOCKS_COUNT];
		totalBlocks = 0;
	}
	
    /**
     * Returns the number of elements in this list.<br>
     * If this list  contains more than <tt>Integer.MAX_VALUE</tt> elements,
     * returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
	public int size() {
		return (size > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) size;
	}

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
	public boolean add(E element) {
		ensureCapacity(size + 1);
		int blockIndex = (int) (size >>> blockBitsize);
		data[blockIndex].add(element);
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
		fastAdd(index, element);
	}

	private void fastAdd(long index, E element) {
		modCount++;
		int blockIndex = (int) (index >>> blockBitsize);
		int valueIndex = (int) (index & (-1L >>> -blockBitsize));
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
		return fastGet(index);
	}

	private E fastGet(long index) {
		int blockIndex = (int) (index >>> blockBitsize);
		int valueIndex = (int) (index & (-1L >>> -blockBitsize));
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
		return fastSet(index, element);
	}

	private E fastSet(long index, E element) {
		int blockIndex = (int) (index >>> blockBitsize);
		int valueIndex = (int) (index & (-1L >>> -blockBitsize));
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
		return fastRemove(index);
	}

	private E fastRemove(long index) {
		modCount++;
		int blockIndex = (int) (index >>> blockBitsize);
		int valueIndex = (int) (index & (-1L >>> -blockBitsize));
		E removed = data[blockIndex].remove(valueIndex);
		while (++blockIndex < totalBlocks) {
			data[blockIndex-1].add(data[blockIndex].removeFirst());
		}
		size--;
		// - free unused block for GC and compact list if needed
		if (totalBlocks > 0 && data[totalBlocks-1].size() == 0) {
			data[--totalBlocks] = null;
			compact();
		};
		return removed;
	}

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 
     * <p>This implementation supports lists of sizes above <tt>Integer.MAX_VALUE</tt>
     * limit.
     * 
     * <p>This implementation is made to throw runtime exceptions in the
     * face of concurrent modification, as described in the specification
     * for the (protected) {@link AbstractList#modCount modCount} field.
     *
     * @return an iterator over the elements in this list in proper sequence
     */
	public Iterator<E> iterator() {
		return new Iter();
	}
	
    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list in
     *         proper sequence
     */
	public Object[] toArray() {
		long size = this.size;
		if (size > Integer.MAX_VALUE)
			throw new OutOfMemoryError("Required array size too large");
		
		int expectedModCount = modCount;
		Object[] r = new Object[(int) size];
		for (int i = 0, pos = 0; i < totalBlocks; i++) {
			pos += data[i].copyToArray(r, pos);
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();
		}
		return r;
	}

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
	public <T> T[] toArray(T[] a) {
		long size = this.size;
		if (size > Integer.MAX_VALUE)
			throw new OutOfMemoryError("Required array size too large");
		
		int expectedModCount = modCount;
		@SuppressWarnings("unchecked")
		T[] r = (a.length < size)
				? (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), (int) size)
				: a;
		for (int i = 0, pos = 0; i < totalBlocks; i++) {
			pos += data[i].copyToArray(r, pos);
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();			
		}
		if (r.length > size)
			r[(int) size] = null;
		return r;
	}
}