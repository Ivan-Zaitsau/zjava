package zjava.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/** 
 * Early draft of HashedSet.
 * 
 * @param <E> - the type of elements in this set
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public class HashedSet<E> extends AbstractSet<E> implements Set<E> {
	
	transient volatile int modCount = 0;

	private final Hasher<? super E> hasher;
	
	private long size;
	private final HashTable<E> table;
	private boolean containsNull;
	
	private int hash(E value) {
		int h = hasher.hashCode(value);
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
	}
	
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

	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		return (size > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) size;
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
		if (containsNull)
			return false;
		containsNull = true;
		size++;
		return true;
	}

	public boolean add(E e) {
		if (e == null)
			return addNull();
		return table.add(hash(e), e);
	}

	private boolean removeNull() {
		if (!containsNull)
			return false;
		containsNull = false;
		size--;
		return true;
	}

	public boolean remove(Object o) {
		if (o == null)
			removeNull();
		@SuppressWarnings("unchecked")
		E e = (E) o;
		return table.remove(hash(e), e);
	}

	public void clear() {
		containsNull = false;
		table.clear();
		size = 0;
	}
}
