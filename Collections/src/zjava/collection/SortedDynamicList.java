package zjava.collection;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * Simple implementation of <tt>SortedList</tt> interface which
 * uses <tt>DynamicList</tt> as internal storage. <br>
 * Does <b>not</b> permit <i>null</i> element.<br>
 * Supports more than <tt>Integer.MAX_VALUE</tt> elements.
 * 
 * @param <E> the type of elements in this list
 * 
 * @author Ivan Zaitsau
 * @see Collection
 * @see java.util.List List
 * @see SortedList
 * @see DynamicList
 */
public class SortedDynamicList<E> extends AbstractCollection<E> implements SortedList<E>, HugeCapacityList<E>, RandomAccess, Cloneable, java.io.Serializable {

	static private final long serialVersionUID = 2015_02_12_1200L;

	private DynamicList<E> data;
	private Comparator<? super E> comparator;
	
	private transient FarListAccess<E> farAccess;
	
	private final class FarAccess implements FarListAccess<E> {
		
		private final FarListAccess<E> farDataAccess = data.far();
		
		public long size() {
			return farDataAccess.size();
		}

		public E get(long index) {
			return farDataAccess.get(index);
		}

		public E set(long index, E element) {
			throw new UnsupportedOperationException();
		}

		public void add(long index, E element) {
			throw new UnsupportedOperationException();
		}

		public E remove(long index) {
			return farDataAccess.remove(index);
		}
	};
	
	public SortedDynamicList() {
		data = new DynamicList<E>();
	}

	public SortedDynamicList(Comparator<? super E> comparator) {
		this();
		this.comparator = comparator;
	}

	public FarListAccess<E> far() {
		if (farAccess == null)
			farAccess = new FarAccess();
		return farAccess;
	}

    /**
     * Returns the number of elements in this sorted list.<br>
     * If this sorted list  contains more than <tt>Integer.MAX_VALUE</tt> elements,
     * returns <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
	public int size() {
		return data.size();
	}

	// - returns index of position right after last element which is less than method argument
	// - if list contains given object, returned value is equal to position, otherwise it's equal to -(position+1)
	private long binarySearch(Object o) {
		if (o == null)
			throw new NullPointerException();
		
		FarListAccess<E> storage = far();

		long size = storage.size();
		if (size == 0)
			return -1;
		
		long low = 0, high = size - 1;
		int lastCmp;
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> e = (Comparable<? super E>) o;
			while (low < high) {
				long i = (low + high) >>> 1;
				if (e.compareTo(storage.get(i)) <= 0)
					high = i;
				else
					low = i + 1;
			}
			lastCmp = e.compareTo(storage.get(low));
		}
		else {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			while (low < high) {
				long i = (low + high) >>> 1;
				if (comparator.compare(e, storage.get(i)) <= 0)
					high = i;
				else
					low = i + 1;
			}
			lastCmp = comparator.compare(e, storage.get(low));
		}
		return (lastCmp < 0) ? -(low+1) : (lastCmp == 0) ? low : -(low+2);
	}
	
	// - returns index of position right after last element which is less or equal to method argument
	// - if list contains given object, returned value is equal to position, otherwise it's equal to -(position+1)
	private long binarySearchNext(Object o) {
		if (o == null)
			throw new NullPointerException();
		
		FarListAccess<E> storage = far();

		long size = storage.size();
		if (size == 0)
			return -1;
		
		long low = 0, high = size - 1;
		int lastCmp;
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> e = (Comparable<? super E>) o;
			if (e.compareTo(storage.get(high)) >= 0)
				return -(size+1);
			else
				high--;
			while (low < high) {
				long i = (low + high + 1) >>> 1;
				if (e.compareTo(storage.get(i)) < 0)
					high = i - 1;
				else
					low = i;
			}
			lastCmp = e.compareTo(storage.get(low));
		}
		else {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			if (comparator.compare(e, storage.get(high)) >= 0)
				return -(size+1);
			else
				high--;
			while (low < high) {
				long i = (low + high + 1) >>> 1;
				if (comparator.compare(e, storage.get(i)) < 0)
					high = i - 1;
				else
					low = i;
			}
			lastCmp = comparator.compare(e, storage.get(low));
		}
		return (lastCmp < 0) ? -(low+1) : (lastCmp == 0) ? low+1 : -(low+2);
	}
	
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>o.equals(get(i))</tt>, or -1 if there is no such index.
     * 
     * <p><b>Note:</b> only up to <tt>Integer.MAX_VALUE</tt> first elements
     * are visible to this method. So, for huge lists {@link #contains(Object)}
     * may return <tt>true</tt> while this method may return <tt>-1</tt> at
     * the same time.

     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     *         
     * @throws NullPointerException if the specified element is null
     */
	public int indexOf(Object o) {
		long i = binarySearch(o);
		if (i < 0)
			return -1;
		if (i <= Integer.MAX_VALUE)
			return (int) i;
		else
			return -1;
	}

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>o.equals(get(i))</tt>, or -1 if there is no such index.<br>
     * 
     * <p><b>Note:</b> only up to <tt>Integer.MAX_VALUE</tt> first elements
     * are visible to this method. So, for huge lists {@link #contains(Object)}
     * may return <tt>true</tt> while this method may return <tt>-1</tt> at the
     * same time.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     *         
     * @throws NullPointerException if the specified element is null
     */
	public int lastIndexOf(Object o) {
		long i = binarySearchNext(o);
		if (i < 0)
			return -1;
		i--;
		if (i <= Integer.MAX_VALUE)
			return (int) i;
		else
			return (o.equals(data.get(Integer.MAX_VALUE))) ? Integer.MAX_VALUE : -1;
	}

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that <tt>o.equals(e)</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element

     * @throws NullPointerException if the specified element is null
     */
	public boolean contains(Object o) {
		return binarySearch(o) >= 0;
	}

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	public E get(int index) {
		return data.get(index);
	}

    /**
     * Inserts the specified element in this list in such way that it remains sorted.
     *
     * <p>This list doesn't permit <tt>null</tt> element.
     *
     * @param e element to be added to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
	public boolean add(E e) {
		long i = binarySearchNext(e);
		if (i < 0)
			i = -(i+1);
		data.far().add(i, e);
		return true;
	}

	private void checkForNull(Collection<?> c) {
		boolean hasNull = false;
		try {
			hasNull = c.contains(null);
		}
		catch (NullPointerException ignore) {};
		if (hasNull)
			throw new NullPointerException();
	}
	
    /**
     * Inserts all the elements from the specified collection in
     * this list, in such way that the list remains sorted. The behavior of this
     * operation is undefined if the specified collection is modified while
     * the operation is in progress.  (Note that this will occur if the
     * specified collection is this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this list
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this list
     * @see #add(Object)
     */
	public boolean addAll(Collection<? extends E> c) {
		checkForNull(c);
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
	}
	
	/**
	 * Removes the first occurrence of the specified element from this list,
     * if it is present. If this list does not contain the element, it is 
     * unchanged. More formally, removes the element with the lowest index 
     * <tt>i</tt> such that <tt>o.equals(get(i))</tt> (if such an element exists).
     * Returns <tt>true</tt> if this list was changed as a result of the call.
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     * 
     * @throws NullPointerException if the specified element is null 
     */
	public boolean remove(Object o) {
		long i = binarySearch(o);
		if (i < 0)
			return false;
		data.far().remove(i);
		return true;
	}

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * 
     * @throws NullPointerException if the specified collection is null
     * 
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object e : c)
			if (e != null)
				while (remove(e))
					modified = true;
		return modified;
	}

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection. In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * 
     * @throws NullPointerException if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns.
	 */
	public void clear() {
		data.clear();
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
		return data.remove(index);
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
		return data.iterator();
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
		return data.toArray();
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
		return data.toArray(a);
	}
	
    /**
     * Returns a shallow copy of this <tt>SortedDynamicList</tt> instance.
     * (The elements themselves are not cloned).
     *
     * @return a clone of this <tt>SortedDynamicList</tt> instance
     */
    @SuppressWarnings("unchecked")
	public Object clone() {
    	try {
			SortedDynamicList<E> clone = (SortedDynamicList<E>) super.clone();
			clone.farAccess = null;
			clone.data = (DynamicList<E>) data.clone();
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }

    /**
     * Compares the specified object with this list for equality. Returns
     * <tt>true</tt> if and only if the specified object is also a <tt>SortedList</tt>,
     * both lists have the same size, and all corresponding pairs of elements in
     * the two lists are <i>equal</i>. In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.
     * 
     * @param o the object to be compared for equality with this list
     * @return <tt>true</tt> if the specified object is equal to this list
     */
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof SortedList))
			return false;
		SortedList<?> other = (SortedList<?>) o;
		if (size() != other.size())
			return false;
		Iterator<E> thisIter = this.iterator();
		Iterator<?> otherIter = other.iterator();
		while(thisIter.hasNext() & otherIter.hasNext()) {
			E thisElement = thisIter.next();
			Object otherElement = otherIter.next();
			if (!thisElement.equals(otherElement))
				return false;
		}
		return !thisIter.hasNext() & !otherIter.hasNext();
	}
	
    /**
     * Returns the hash code value for this list.
     *
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link SortedList#hashCode}
     * method with null-value check omitted.
     *
     * @return the hash code value for this list
     */
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31*hashCode + e.hashCode();
        return hashCode;
    }
    
    /**
     * Returns a string representation of this list. The string representation
     * relies on <tt>toString</tt> implementation of underlying list.
     * 
     * @see DynamicList#toString()
     */
	public String toString() {
		return data.toString();
	}
}
