package zjava.collection;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

/** 
 * Early draft of HashedSet.
 * 
 * @param <E> - the type of elements in this <tt>Set</tt>
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public class HashedSet<E> extends AbstractSet<E> implements Set<E>, HugeCapacitySupport, Cloneable, java.io.Serializable {
	
	private static final long serialVersionUID = 201612210000L;

	transient volatile int modCount = 0;

	private final Hasher<? super E> hasher;
	
	private long size;
	private HashTable<E> table;
	private boolean containsNull;

	private transient HugeCapacity hugeView;
	
	public HashedSet() {
		this(Hasher.DEFAULT);
	}

	/**
	 * 
	 * 
	 * <p>WARNING: {@code Hasher} with overridden {@code equals} method
	 * violates general contract on {@link Set} interface.
	 * 
	 * @param hasher - specified hash-function to use for elements hashing and (optionally) comparison
	 */
	public HashedSet(Hasher<? super E> hasher) {
		this.hasher = hasher;
		table = new HashTable<E>();
	}

	public int size() {
		return (size > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) size;
	}

	private int hash(E value) {
		int h = hasher.hashCode(value);
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
	}
	
	public boolean contains(Object o) {
		if (o == null)
			return containsNull;
		@SuppressWarnings("unchecked")
		E e = (E) o;
		int hash = hash(e);
		return table.contains(hash, e);
	}

	private boolean addNull() {
		if (!containsNull) {
			containsNull = true;
			size++;
			return true;			
		}
		return false;
	}

	public boolean add(E e) {
		if (e == null)
			return addNull();
		if (table.add(hash(e), e)) {
			modCount++;
			size++;
			return true;
		}
		return false;
	}

	private boolean removeNull() {
		if (containsNull) {
			containsNull = false;
			size--;
			return true;			
		}
		return false;
	}

	public boolean remove(Object o) {
		if (o == null)
			return removeNull();
		@SuppressWarnings("unchecked")
		E e = (E) o;
		if (table.remove(hash(e), e)) {
			modCount++;
			size--;
			return true;
		}
		return false;
	}

	public void clear() {
		modCount++;
		containsNull = false;
		table.clear();
		size = 0;
	}
	
	public Iterator<E> iterator() {

		return new Iterator<E>() {

			/** true if this iterator still has null value */
			private boolean hasNull = containsNull;

			/** true if previous value returned by this iterator was <tt>null</tt> */
			private boolean wasNull = false;
			
			/** Expected version (modifications count) of the backing Set */
			private int expectedModCount = modCount;
			private Iterator<E> tableIter = table.iterator();

			private void checkForComodification() {
				if (expectedModCount != modCount)
					throw new ConcurrentModificationException();
			}

			public boolean hasNext() {
				checkForComodification();
				return hasNull || tableIter.hasNext();
			}

			public E next() {
				checkForComodification();
				if (hasNull) {
					hasNull = false;
					wasNull = true;
					return null;
				}
				try {
					E next = tableIter.next();
					wasNull = false;
					return next;
				}
				catch (NullPointerException e) {
					throw new ConcurrentModificationException();
				}
				catch (IndexOutOfBoundsException e) {
					throw new ConcurrentModificationException();
				}
			}

			public void remove() {
				checkForComodification();
				if (wasNull)
					removeNull();
				try {
					tableIter.remove();					
				}
				catch (NullPointerException e) {
					throw new ConcurrentModificationException();
				}
				catch (IndexOutOfBoundsException e) {
					throw new ConcurrentModificationException();
				}
				expectedModCount = modCount;
			}
		};
	}

    /**
     * Returns a shallow copy of this <tt>HashedSet</tt> instance.
     * (The elements themselves are not cloned).
     *
     * @return a clone of this <tt>HashedSet</tt> instance
     */
    @SuppressWarnings("unchecked")
	public Object clone() {
    	try {
    		HashedSet<E> clone = (HashedSet<E>) super.clone();
			clone.modCount = 0;
			clone.hugeView = null;
			clone.table = (HashTable<E>) table.clone();
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }

	public HugeCapacity asHuge() {
		if (hugeView == null)
			hugeView = new HugeCapacity() {
			public long size() {
				return size;
			}
		};
		return hugeView;
	}
}