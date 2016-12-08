package zjava.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import static zjava.system.Const.MAX_ARRAY_SIZE;

/**
 * Resizable dynamic array implementation of the <tt>List</tt> interface.
 * Implements all optional list operations, and permits all elements,
 * including <tt>null</tt>.
 *
 * <p>The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time. Removal and insertion of
 * elements at arbitrary index runs in <i>O(sqrt(n)) amortized time</i>.<br>
 * In most cases this implementation is significantly faster than
 * {@link java.util.ArrayList ArrayList} implementation and requires just
 * O(sqrt(n)) of additional memory.
 *
 * @param <E> - the type of elements in this list
 * 
 * @since Zjava 1.0
 *  
 * @author Ivan Zaitsau
 * 
 * @see     Collection
 * @see     List 
 */
public class DynamicList<E> extends AbstractList<E> implements List<E>, HugeListAccess<E>, RandomAccess, Cloneable, java.io.Serializable {

	static private final long serialVersionUID = 201503121600L;
	
	/** Actual initial block size is 2<sup>INITIAL_BLOCK_ADDRESS_BITS</sup> */
	static private final int INITIAL_BLOCK_ADDRESS_BITS = 5;
	
	/** Number of blocks on DynamicList initialization.
	 * <br> <b>Note:</b> Must be even number due to some simplifications and assumptions made in the code*/
	static private final int INITIAL_BLOCKS_COUNT = 2;
	
	/** This coefficient used to check if reduction of block size and amount of blocks is required.
	 * <br> <b>Note:</b> Must be no less than 4. Needs to be no less than 8 for amortized performance estimations to hold */
	static private final int REDUCTION_COEFFICIENT = 12;

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
	static final private class Block<E> implements Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 201503121600L;

		// - merges two blocks of equal capacities into block with doubled capacity
		static <E> Block<E> merge(Block<E> block1, Block<E> block2) {
			if ((block1 == null || block1.size() == 0) && (block2 == null || block2.size() == 0))
				return null;

			assert (block1 == null | block2 == null) || (block1.values.length == block2.values.length);

			Block<E> mergedBlock = new Block<E>(2 * ((block1 == null) ? block2.values.length : block1.values.length));
			if (block1 != null)
				mergedBlock.size += block1.copyToArray(mergedBlock.values, 0);
			if (block2 != null)
				mergedBlock.size += block2.copyToArray(mergedBlock.values, mergedBlock.size);
			return mergedBlock;
		}

		// - splits block to two smaller blocks of capacity equal to half of given block
		@SuppressWarnings("unchecked")
		static <E> Block<E>[] split(Block<E> block) {
			if (block == null || block.size == 0)
				return new Block[] {null, null};
			
			assert (block.values.length & 1) == 0;
			
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
		
		private int offset;
		private int size;
		private Object[] values;
		
		Block(int capacity) {
			// - capacity must be power of 2 and greater than one
			assert((capacity & (capacity-1)) == 0 && capacity > 1);
			
			this.offset = 0;
			this.size = 0;
			this.values = new Object[capacity];
		}

		Block(int capacity, E[] values, int pos, int length) {
			this(capacity);
			System.arraycopy(values, pos, this.values, 0, length);
			size = length;
		}
		
		// - copies "count" values of this block starting from "srcPos" to array starting from "trgPos" index
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

		// - copies all values of this block to given array starting from "pos" index
		int copyToArray(Object[] array, int pos) {
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
		E addFirst(final E value) {
			offset = index(-1);
			@SuppressWarnings("unchecked")
			E last = (E) values[offset];
			values[offset] = value;
			if (size < values.length) {
				size++;
			}
			return last;
		}
		
		// - appends given value to the end of the block
		void addLast(final E value) {
			if (size == values.length)
				return;
			values[index(size)] = value;
			size++;
		}
		
		// - inserts given value at given position
		E add(final int pos, final E value) {
			// - range check
			assert(pos >= 0 && pos <= size);
			
			@SuppressWarnings("unchecked")
			E last = (E) ((size == values.length) ? values[index(-1)] : null);
			if (pos + pos < size) {
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
		E set(final int pos, final E value) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			int i = index(pos);
			@SuppressWarnings("unchecked")
			E replaced = (E) values[i];
			values[i] = value;
			return replaced;
		}
		
		// - removes first element of the block
		E removeFirst() {
			// - range check
			assert(size > 0);
			
			@SuppressWarnings("unchecked")
			E removed = (E) values[offset];
			values[offset] = null;
			offset = index(1);
			size--;
			return removed;
		}
		
		// - removes element at given position
		E remove(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			@SuppressWarnings("unchecked")
			E removed = (E) values[index(pos)];
			if (pos + pos < size) {
				for (int i = pos; i > 0; i--) {
					values[index(i)] = values[index(i-1)];					
				}
				values[offset] = null;
				offset = index(1);
			}
			else {
				for (int i = pos + 1; i < size; i++) {
					values[index(i-1)] = values[index(i)];
				}
				values[index(size-1)] = null;
			}
			size--;
			return removed;
		}
		
		// - returns element at given position
		@SuppressWarnings("unchecked")
		E get(final int pos) {
			// - range check
			assert(pos >= 0 && pos < size);
			
			return (E) values[index(pos)];
		}
		
		public Block<E> clone() {
			try {
				@SuppressWarnings("unchecked")
				Block<E> clone = (Block<E>) super.clone();
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
	private Block<E>[] data;
	
	private transient HugeList<E> hugeView;
	
	/** Null-safe access to data block with initialization.*/
	private Block<E> data(final int index) {
		if (data[index] == null)
			data[index] = new Block<E>(1 << blockAddressBits);
		return data[index];
	}
	
    /**
     * Increases the capacity of this <tt>DynamicList</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the required capacity argument.
     *
     * @param requiredCapacity the desired capacity
     */
	public void ensureCapacity(long requiredCapacity) {
		long capacity = (long) data.length << blockAddressBits;
		while (requiredCapacity > capacity) {
			// - double number of blocks and their size
			@SuppressWarnings("unchecked")
			Block<E>[] newData = new Block[2*data.length];
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
			@SuppressWarnings("unchecked")
			Block<E>[] newData = new Block[(data.length+1)/2];
			int newBlockBitsize = blockAddressBits-1;
			main:
			for (int i = 0; ; i++) {
				Block<E>[] splitBlock = Block.split(data[i]);
				for (int j = 0; j <= 1; j++) {
					if (splitBlock[j] == null || splitBlock[j].size() == 0)
						break main;
						newData[i+i+j] = splitBlock[j];
				}
			}
			data = newData;
			blockAddressBits = newBlockBitsize;
			modCount++;
		}
	}
	
    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + size;
    }
	
	private void rangeCheck(final long index) {
		if (index < 0 | index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void rangeCheckForAdd(final long index) {
		if (index < 0 | index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void init() {
		init(0);
	}
	
	@SuppressWarnings("unchecked")
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
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *         {@code toIndex} is out of range
     */
	protected void removeRange(int fromIndex, int toIndex) {
		rangeCheck(fromIndex);
		rangeCheckForAdd(toIndex);
		int fromBlock = (1 + ((fromIndex - 1) >> blockAddressBits));
		int toBlock = (toIndex >>> blockAddressBits);
		int blocksToRemove = toBlock - fromBlock;
		if (blocksToRemove > 0) {
			for (int i = toBlock; i < data.length && data[i] != null && data[i].size() > 0; i++) {
				data[i-blocksToRemove] = data[i];
				data[i] = null;
			}
			size -= blocksToRemove << blockAddressBits;
			toIndex -= blocksToRemove << blockAddressBits;
		}
		for (int i = fromIndex; i < toIndex; i++)
			fastRemove(fromIndex);
	}
	
	/**
     * Constructs an empty list with an initial capacity of 32 elements.
     */
	public DynamicList() {
		init();
	}

	/**
     * Constructs an empty list with at least specified initial capacity.
     */
	public DynamicList(long initialCapacity) {
		init(initialCapacity);
	}

	/**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
	@SuppressWarnings("unchecked")
	public DynamicList(Collection<? extends E> src) {
		init(src.size());
		// - unsafe, can throw ConcurrentModificationException if source collection is changed
		if (src.size() > MAX_ARRAY_SIZE) {
			synchronized(src) {
				for (E e : src)
					add(e);
			}
		}
		// - more safe but resource consuming as well.
		// - can't be applied to enormous collections with more than MAX_ARRAY_SIZE elements.
		else {
			for (E e : (E[]) src.toArray())
				add(e);
		}
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
     * @param element element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
	public boolean add(E element) {
		ensureCapacity(size + 1);
		int blockIndex = (int) (size >>> blockAddressBits);
		data(blockIndex).addLast(element);
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

	// - "add" method without range and capacity checks
	private void fastAdd(final long index, E element) {
		modCount++;
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		int blockSize = 1 << blockAddressBits;
		if (data(blockIndex).size() < blockSize) {
			data[blockIndex].add(valueIndex, element);
		}
		else {
			element = data[blockIndex].add(valueIndex, element);
			while (data(++blockIndex).size() == blockSize) {
				element = data[blockIndex].addFirst(element);
			}
			data[blockIndex].addFirst(element);
		}
		size++;
	}

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's <tt>toArray</tt> method.
     *
     * @param collection collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> collection) {
		@SuppressWarnings("unchecked")
		E[] values = (E[]) collection.toArray();
    	if (values.length == 0)
    		return false;
    	for (E value : values)
    		add(value);
    	return true;
    }
    
    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param collection collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
	public boolean addAll(int index, Collection<? extends E> collection) {
    	rangeCheckForAdd(index);
    	@SuppressWarnings("unchecked")
		E[] values = (E[]) collection.toArray();
    	if (values.length == 0)
    		return false;
    	ensureCapacity(size + values.length);
    	int blockSize = 1 << blockAddressBits;
    	int mask = (1 << blockAddressBits) - 1;
    	int i = 0;
		while (i < values.length && ((index + i) & mask) > 0) {
    		fastAdd((long)index + i, values[i]);
    		i++;
		}
    	while (i < values.length - blockSize) {
    		int fromBlock = (int) (((long)index + i) >>> blockAddressBits);
    		int toBlock = (int) ((size + mask) >>> blockAddressBits);
    		for (int j = toBlock; j > fromBlock; j--)
    			data[j] = data[j-1];
    		data[fromBlock] = new Block<E>(blockSize, values, i, blockSize);
    		i += blockSize;
    		size += blockSize;
    	}
    	while (i < values.length) {
    		fastAdd((long)index + i, values[i]);
    		i++;
    	}
    	return true;
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

	// - "get" method without range check
	private E fastGet(final long index) {
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
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

	// - "add" method without range check
	private E fastSet(final long index, E element) {
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
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

	// - "remove" method without range check
	private E fastRemove(final long index) {
		modCount++;
		int blockIndex = (int) (index >>> blockAddressBits);
		int valueIndex = (int) (index & (-1L >>> -blockAddressBits));
		E removed = data[blockIndex].remove(valueIndex);
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
     * Removes all of this collection's elements that are also contained in the
     * specified collection. After this call returns, this collection will contain
     * no elements in common with the specified collection. 
     *
     * @param c collection containing elements to be retained in this collection
     * 
     * @return <tt>true</tt> if this collection changed as a result of the call
     * 
     * @throws NullPointerException if this collection contains one or more
     *         <tt>null</tt> elements and the specified collection does not permit
     *         <tt>null</tt> elements, or if the specified collection is <tt>null</tt>
     */
	public boolean removeAll(Collection<?> c) {
		return removeOrRetainAll(c, false);
	}
	
    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection. In other words, removes from this collection all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * 
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws NullPointerException if this collection contains one or more
     *         <tt>null</tt> elements and the specified collection does not permit <tt>null</tt>
     *         elements, or if the specified collection is <tt>null</tt>
     */
	public boolean retainAll(Collection<?> c) {
		return removeOrRetainAll(c, true);
	}
	
	// - removes or retains all elements that belong to specified collection
	// - dependent on specified boolean flag (remove = false, retain = true)
	private boolean removeOrRetainAll(Collection<?> c, boolean retain) {
		if (isEmpty())
			return false;
		if (c == this) {
			if (retain)
				return false;
			clear();
			return true;
		}
		boolean modified = false;
		for (long i = size-1; i >= 0; i--) {
			if (c.contains(fastGet(i)) ^ retain) {
				fastRemove(i);
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns.
	 */
	public void clear() {
		Arrays.fill(data, null);
		size = 0;
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
		return new Iterator<E>() {

			/**
			 * Cursor position
			 */
			private long i = 0;
			
			/**
			 * Current (last returned) element index or -1 if element is not defined (or has been removed)
			 */
			private long last = -1;
			
			/**
			 * Expected version (modifications count) of the backing List 
			 */
			int expectedModCount = modCount;
			
			public boolean hasNext() {
				return i < size;
			}

	        public E next() {
	            checkForComodification();
	            try {
	                rangeCheck(i);
	                E e = fastGet(i);
	                last = i++;
	                return e;
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
	                rangeCheck(last);
	            	fastRemove(last);
	                i--;
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
		};
	}
	
	public HugeList<E> asHuge() {
		if (hugeView == null) {
			hugeView = new HugeList<E>() {
				
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
		}
		return hugeView;
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
		if (size > MAX_ARRAY_SIZE)
			throw new OutOfMemoryError("Required array size too large");
		
		Object[] result = new Object[(int) size];
		int pos = 0;
		for (int i = 0; i < data.length && data[i] != null && data[i].size() > 0; i++) {
			pos += data[i].copyToArray(result, pos);
		}
		return (pos < size) ? Arrays.copyOf(result, pos) : result;
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
		if (size > MAX_ARRAY_SIZE)
			throw new OutOfMemoryError("Required array size too large");
		
		@SuppressWarnings("unchecked")
		T[] result = (a.length < size)
				? (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), (int) size)
				: a;
		for (int i = 0, pos = 0; i < data.length && data[i] != null && data[i].size() > 0; i++) {
			pos += data[i].copyToArray(result, pos);	
		}
		if (result.length > size)
			result[(int) size] = null;
		return result;
	}
	
    /**
     * Returns a shallow copy of this <tt>DynamicList</tt> instance.
     * (The elements themselves are not cloned).
     *
     * @return a clone of this <tt>DynamicList</tt> instance
     */
    @SuppressWarnings("unchecked")
	public Object clone() {
    	try {
			DynamicList<E> clone = (DynamicList<E>) super.clone();
			clone.modCount = 0;
			clone.hugeView = null;
			clone.data = new Block[data.length];
    		for (int i = 0; i < data.length && data[i] != null; i++) {
    			clone.data[i] = data[i].clone();
    		}
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }

    /**
     * Returns a string representation of this list.<br>
     * The string representation consists of a list of the elements separated
     * by commas in the index increasing order.<br>
     * List is enclosed in square brackets (<tt>"[]"</tt>).<br>
     * If list is too large, only first elements will be shown, followed by
     * three-dot (<tt>"..."</tt>).
     * 
     * @return a string representation of this list
     */
	public String toString() {
        if (isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < size; ) {
            E e = fastGet(i);
            sb.append(e == this ? "(this List)" : e);
            if (++i < size) {
                if (sb.length() > 1000) {
                	sb.append(',').append(" ...");
                	break;
                }            	
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        return sb.toString();
	}
}
