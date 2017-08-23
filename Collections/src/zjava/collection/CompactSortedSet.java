package zjava.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/**
 * A {@link SortedSet} implementation which uses {@link DynamicList} as an internal
 * storage.<br>
 * The elements are ordered using their {@link Comparable natural ordering} or by
 * a {@link Comparator} provided at creation time, depending on constructor used.<br>
 * Doesn't permit <tt>null</tt> element.<br>
 * Supports more than <tt>Integer.MAX_VALUE</tt> elements.
 * 
 * <p>This implementation of SortedSet interface is focused on lower memory footprint
 * rather than performance. However, it tends to outperform <tt>TreeSet</tt> for sets
 * of sizes up to approximately 20.000 - 250.000 elements due to simplicity of operations.
 * (actual numbers depend on system architecture and percentage of update operations)
 * 
 * <p>This implementation provides guaranteed log(n) time cost for {@code contains}
 * operation and amortized sqrt(n) time cost for the operations {@code add} and
 * {@code remove}. Retrieval of first or last element completes in constant time.
 *
 * <p>Note that the ordering maintained by this set (whether or not an explicit
 * comparator is provided) must be <i>consistent with equals</i> to follow  {@code Set}
 * interface contract. (See {@link Comparable} or {@link Comparator} for a precise
 * definition of <i>consistent with equals</i>.)
 * Reason for this is that {@code Set} interface is defined in terms of the {@code equals}
 * operation, but a {@code CompactSortedSet} instance performs all element comparisons using
 * {@code compareTo} or {@code compare} method, so two elements that are deemed equal by this
 * method are, from the standpoint of this set, equal.
 * However, the behavior of a set <i>is</i> well-defined even if its ordering is inconsistent
 * with equals.
 *
 * @param <E> - the type of elements in this set
 *  
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public class CompactSortedSet<E> extends AbstractSet<E> implements SortedSet<E>, HugeCapacitySupport, Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 201503111900L;
	
	private DynamicList<E> data;
	private Comparator<? super E> comparator;
	
	private transient HugeCapacity hugeView;

	private class SubSet extends AbstractSet<E> implements SortedSet<E> {
		
		E fromElement;
		E toElement;
		
		SubSet(E fromElement, E toElement) {
			this.fromElement = fromElement;
			this.toElement = (fromElement == null || toElement == null || compare(fromElement, toElement) <= 0)
					? toElement
					: fromElement;
		}

		@SuppressWarnings("unchecked")
		int compare(E e1, E e2) {
			if (comparator == null)
				return ((Comparable<? super E>) e1).compareTo(e2);
			else
				return comparator.compare(e1, e2);
		}
		
		// - checks if given element falls in [fromElement; toElement) range
		boolean inRange(E element) {
			return (fromElement == null || compare(fromElement, element) <= 0)
					&& (toElement == null || compare(element, toElement) < 0);
		}

		// - returns index of given element in data array
		long index(E element) {
			long index = binarySearch(element);
			return index < 0 ? ~index : index;
		}

		public int size() {
			long fromIndex = (fromElement == null) ? 0 : index(fromElement);
			long toIndex = (toElement == null) ? data.asHuge().size() : index(toElement);
			long size = toIndex - fromIndex;
			return (size < Integer.MAX_VALUE) ? (int) size : Integer.MAX_VALUE;
		}

		public boolean contains(Object o) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			return inRange(e) ? CompactSortedSet.this.contains(o) : false;
		}

	    /**
	     * Returns an iterator over the elements in this set in ascending order.
	     *
	     * @return an iterator over the elements in this set in ascending order
	     */
		public Iterator<E> iterator() {

			return new Iterator<E>() {
				
				HugeList<E> data = CompactSortedSet.this.data.asHuge();
				
				long index = (fromElement == null) ? 0 : index(fromElement);
				E element = index < data.size() ? data.get(index) : null;
				
				long lastIndex = -1;
				E lastElement;

				
				void checkForComodification(long index, E element) {
					if (data.get(index) != element)
						throw new ConcurrentModificationException();
				}
				
				public boolean hasNext() {
					if (index >= data.size())
						return false;
					
					checkForComodification(index, element);
					
					if (toElement == null)
						return true;
					
					return compare(element, toElement) < 0;
				}

				public E next() {
					if (!hasNext())
						throw new NoSuchElementException();
					
					lastIndex = index;
					lastElement = element;
					index++;
					element = index < data.size() ? data.get(index) : null;
					return lastElement;
				}

				public void remove() {
					if (lastIndex < 0)
						throw new IllegalStateException();
					
					checkForComodification(lastIndex, lastElement);
					
					data.remove(lastIndex);
					lastIndex = -1;
					lastElement = null;
					index--;
				}
			};
		}

		public boolean add(E element) {
			if (!inRange(element))
				throw new IllegalArgumentException();
			return CompactSortedSet.this.add(element);
		}

		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			return (inRange((E) o)) ? CompactSortedSet.this.remove(o) : false;
		}

		public boolean removeAll(Collection<?> c) {
			boolean modified = false;
			for (Object e : c)
				if (e != null)
					modified |= remove(e);
			return modified;
		}

		public Comparator<? super E> comparator() {
			return comparator;
		}

		public SortedSet<E> subSet(E fromElement, E toElement) {
			E from = (fromElement == null) || (this.fromElement != null && compare(this.fromElement, fromElement) > 0)
					? this.fromElement
					: fromElement;
			E to = (toElement == null) || (this.toElement != null && compare(this.toElement, toElement) < 0)
					? this.toElement
					: toElement;
			return new SubSet(from, to);
		}

		public SortedSet<E> headSet(E toElement) {
			return subSet(null, toElement);
		}

		public SortedSet<E> tailSet(E fromElement) {
			return subSet(fromElement, null);
		}

		public E first() {
			E first = data.asHuge().get((fromElement == null) ? 0 : index(fromElement));
			if (compare(first, toElement) < 0)
				return first;
			throw new NoSuchElementException();
		}

		public E last() {
			E last = data.asHuge().get((toElement == null) ? data.asHuge().size()-1 : index(toElement)-1);
			if (compare(fromElement, last) <= 0)
				return last;
			throw new NoSuchElementException();
		}
	}
	
	public CompactSortedSet() {
		data = new DynamicList<E>();
	}
	
	public CompactSortedSet(Comparator<? super E> comparator) {
		this();
		this.comparator = comparator;
	}
	
	public CompactSortedSet(Collection<? extends E> collection) {
		this();
		addAll(collection);
	}
	
    /**
     * Returns an iterator over the elements in this set in ascending order.
     *
     * @return an iterator over the elements in this set in ascending order
     */
	public Iterator<E> iterator() {
		return data.iterator();
	}

	// - returns index of position right after last element which is less than method argument
	// - if list contains given object, returned value is equal to position, otherwise it's equal to binary inverse of the position
	@SuppressWarnings("unchecked")
	private long binarySearch(Object o) {
		return Collectionz.binarySearch(data.asHuge(), (E) o, comparator);
	}

	public int size() {
		return data.size();
	}

    /**
     * Returns {@code true} if this set contains the specified element.
     *
     * @param o object to be checked for containment in this set
     * @return {@code true} if this set contains the specified element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in the set
     * @throws NullPointerException if the specified element is null
     */
	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		return binarySearch(o) >= 0;
	}

    /**
     * Adds the specified element to this set if it is not already present.<br>
     * 
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param e element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     */
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		long i = binarySearch(e);
		if (i >= 0)
			return false;
		data.asHuge().add(~i, e);
		return true;
	}

    /**
     * Removes the specified element from this set if it is present.
     * Returns {@code true} if this set contained the element (or
     * equivalently, if this set changed as a result of the call).<br>
     * This set will not contain the element once the call returns.
     *
     * @param o object to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     */
	public boolean remove(Object o) {
		if (o == null)
			throw new NullPointerException();
		long i = binarySearch(o);
		if (i < 0)
			return false;
		data.asHuge().remove(i);
		return true;
	}

    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this set
     * @return <tt>true</tt> if this set changed as a result of the call
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
				modified |= remove(e);
		return modified;
	}

    /**
     * Retains only the elements in this set that are contained in the
     * specified collection. In other words, removes from this set all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this set
     * @return <tt>true</tt> if this set changed as a result of the call
     * 
     * @throws NullPointerException if the specified collection is null
     * 
     * @see #remove(Object)
     * @see #contains(Object)
     */
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

    /**
     * Removes all the elements from this set.
     * The set will be empty after this call returns.
     */
	public void clear() {
		data.clear();
	}

	public Comparator<? super E> comparator() {
		return comparator;
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		return new SubSet(fromElement, toElement);
	}

	public SortedSet<E> headSet(E toElement) {
		return subSet(null, toElement);
	}

	public SortedSet<E> tailSet(E fromElement) {
		return subSet(fromElement, null);
	}

	public E first() {
		return data.get(0);
	}

	public E last() {
		HugeList<E> hugeView = data.asHuge();
		return hugeView.get(hugeView.size()-1);
	}

	public HugeCapacity asHuge() {
		if (hugeView == null) {
			hugeView = new HugeCapacity() {
				public long size() {
					return data.asHuge().size();
				}
			};
		}
		return hugeView;
	}

	public Object[] toArray() {
		return data.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}
	
    /**
     * Returns a shallow copy of this <tt>CompactSortedSet</tt> instance.
     * (The elements themselves are not cloned).
     *
     * @return a clone of this <tt>CompactSortedSet</tt> instance
     */
    @SuppressWarnings("unchecked")
	public Object clone() {
    	try {
    		CompactSortedSet<E> clone = (CompactSortedSet<E>) super.clone();
    		clone.hugeView = null;
    		clone.data = (DynamicList<E>) data.clone();
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }

    /**
     * Returns a string representation of this set.<br>
     * The string representation consists of a list of the set's elements
     * separated by commas in the order they are returned by its iterator.<br>
     * Set is enclosed in square brackets (<tt>"[]"</tt>).
     * If this set contains too many elements, only first elements will be shown,
     * followed by three-dot (<tt>"..."</tt>).
     * 
	 * @return a string representation of this set.
     */
	public String toString() {
		return Collectionz.toString(this, "(this Set)");
	}
}