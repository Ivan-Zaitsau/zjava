package extension.collection;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * TODO javadoc is completely missing
 * 
 * @param <E>
 * 
 * @author Ivan Zaitsau
 * @see Collection
 * @see java.util.List List
 * @see SortedList
 * @see DynamicList
 */
public class SortedDynamicList<E> extends AbstractCollection<E> implements SortedList<E>, HugeCapacityList<E>, RandomAccess, java.io.Serializable {

	static private final long serialVersionUID = 2014_10_20_0000L;

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

	public int size() {
		return data.size();
	}

	// - returns index of position right after last element which is less than method argument
	// - if list contains given object, returned value is equal to position, otherwise it's equal to -(position+1)
	private long binarySearch(Object o) {
		if (o == null)
			throw new NullPointerException();
		
		if (size() == 0)
			return -1;
		
		FarListAccess<E> storage = far();
		long l = 0, r = storage.size() - 1;
		int lastCmp;
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> e = (Comparable<? super E>) o;
			while (l < r) {
				long i = (l + r) >>> 1;
				if (e.compareTo(storage.get(i)) <= 0)
					r = i;
				else
					l = i + 1;
			}
			lastCmp = e.compareTo(storage.get(l));
		}
		else {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			while (l < r) {
				long i = (l + r) >>> 1;
				if (comparator.compare(e, storage.get(i)) <= 0)
					r = i;
				else
					l = i + 1;
			}
			lastCmp = comparator.compare(e, storage.get(l));
		}
		return (lastCmp < 0) ? -(l+1) : (lastCmp == 0) ? l : -(l+2);
	}
	
	// - returns index of position right after last element which is less or equal to method argument
	// - if list contains given object, returned value is equal to position, otherwise it's equal to -(position+1)
	private long binarySearchNext(Object o) {
		if (o == null)
			throw new NullPointerException();
		
		if (size() == 0)
			return -1;
		
		FarListAccess<E> storage = far();
		long l = 0, r = storage.size() - 1;
		int lastCmp;
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> e = (Comparable<? super E>) o;
			while (l < r) {
				long i = (l + r + 1) >>> 1;
				if (e.compareTo(storage.get(i)) < 0)
					r = i - 1;
				else
					l = i;
			}
			lastCmp = e.compareTo(storage.get(l));
		}
		else {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			while (l < r) {
				long i = (l + r + 1) >>> 1;
				if (comparator.compare(e, storage.get(i)) < 0)
					r = i - 1;
				else
					l = i;
			}
			lastCmp = comparator.compare(e, storage.get(l));
		}
		return (lastCmp < 0) ? -(l+1) : (lastCmp == 0) ? l+1 : -(l+2);
	}
	
	public int indexOf(Object o) {
		long i = binarySearch(o);
		if (i < 0)
			return -1;
		if (i <= Integer.MAX_VALUE)
			return (int) i;
		else
			return -1;
	}

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

	public boolean contains(Object o) {
		return binarySearch(o) >= 0;
	}

	public E get(int index) {
		return data.get(index);
	}

	public boolean add(E e) {
		long i = binarySearchNext(e);
		if (i < 0)
			i = -(i+1);
		data.far().add(i, e);
		return true;
	}

	public boolean remove(Object o) {
		long i = binarySearch(o);
		if (i < 0)
			return false;
		data.far().remove(i);
		return true;
	}

	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object e : c)
			if (remove(e))
				modified = true;
		return modified;
	}

	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	public void clear() {
		data.clear();
	}

	public E remove(int index) {
		return data.remove(index);
	}

	public Iterator<E> iterator() {
		return data.iterator();
	}

	public Object[] toArray() {
		return data.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}
}
