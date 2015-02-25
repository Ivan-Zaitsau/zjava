package zjava.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * Early draft for compact implementation of SortedSet based on SortedDynamicList.<br>
 * Doesn't permit <tt>null</tt>.
 * 
 * TODO javadoc
 * 
 * @author Ivan Zaitsau
 *
 * @param <E>
 */
public class CompactSortedSet<E> extends AbstractSet<E> implements Set<E> {

	private SortedList<E> data;
	
	public CompactSortedSet() {
		data = new SortedDynamicList<E>();
	}
	
	public CompactSortedSet(Comparator<? super E> comparator) {
		data = new SortedDynamicList<>(comparator);
	}
	
	@Override
	public Iterator<E> iterator() {
		return data.iterator();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean contains(Object o) {
		return data.contains(o);
	}

	@Override
	public Object[] toArray() {
		return data.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return data.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return data.contains(e) ? false : data.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return data.remove(o);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return data.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return data.removeAll(c);
	}

	@Override
	public void clear() {
		data.clear();
	}

}
