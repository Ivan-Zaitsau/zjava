package zjava.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This class contains number of useful methods that operate on collections.
 *
 * <p>The methods of this class all throw a <tt>NullPointerException</tt>
 * if the collections or class objects provided to them are null.
 *
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
final public class Collectionz {

	/**
	 * Performs binary search of the specified object on given <tt>HugeArray</tt>
	 * instance and returns index of position right after the last element which is
	 * strictly less than given object.<br>
	 * If list contains object, returned value is equal to position, otherwise it's
	 * equal to binary inverse of the position.<br>
	 * The array must be sorted into ascending order according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @param array <tt>HugeArray</tt> to be searched
	 * @param o object to search for
	 * 
	 * @return index of position right after the last element strictly less than given
	 *         object (or it's inverse)
	 * 
	 * @throws NullPointerException if the specified object is null
	 */
	public static <E, K extends Comparable<? super E>> long binarySearch(HugeArray<E> array, K o) {
		if (o == null)
			throw new NullPointerException();
		
		long size = array.size();
		if (size == 0)
			return ~0;
		
		long low = 0, high = size - 1;
		while (low < high) {
			long i = (low + high) / 2;
			if (o.compareTo(array.get(i)) <= 0)
				high = i;
			else
				low = i + 1;
		}
		int lastCmp = o.compareTo(array.get(low));
		return (lastCmp < 0) ? ~low : (lastCmp == 0) ? low : ~(low+1);
	}

	/**
	 * Performs binary search of the specified object on given <tt>HugeArray</tt>
	 * instance and returns index of position right after the last element which is
	 * strictly less than given object.<br>
	 * If list contains object, returned value is equal to position, otherwise it's
	 * equal to binary inverse of the position.<br>
     * The array must be sorted into ascending order according to the specified comparator
     * prior to making this call.
	 * 
	 * @param array <tt>HugeArray</tt> to be searched
	 * @param o object to search for
	 * @param comparator the comparator by which the list is ordered or null (if natural
	 *                   ordering should be used)
	 * 
	 * @return index of position right after the last element strictly less than key or it's inverse
	 * 
	 * @throws NullPointerException if the specified object is null and comparator doesn't permit null elements
	 */
	public static <T, E extends T, K extends T> long binarySearch(HugeArray<E> array, K o, Comparator<T> comparator) {
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> comparable = (Comparable<? super E>) o;
			return binarySearch(array, comparable);
		}
		
		long size = array.size();
		if (size == 0)
			return ~0;
		
		long low = 0, high = size - 1;
		while (low < high) {
			long i = (low + high) / 2;
			if (comparator.compare(o, array.get(i)) <= 0)
				high = i;
			else
				low = i + 1;
		}
		int lastCmp = comparator.compare(o, array.get(low));
		return (lastCmp < 0) ? ~low : (lastCmp == 0) ? low : ~(low+1);
	}

	/**
	 * Performs binary search of the specified object on given <tt>HugeArray</tt>
	 * instance and returns index of position right after the last element which
	 * is less or equal to given object.<br>
	 * If list contains object, returned value is equal to position, otherwise it's
	 * equal to binary inverse of the position.<br>
	 * The array must be sorted into ascending order according to the
	 * {@linkplain Comparable natural ordering} of its elements.
	 * 
	 * @param array <tt>HugeArray</tt> to be searched
	 * @param o object to search for
	 * 
	 * @return index of position right after the last element strictly less than given
	 *         object (or it's inverse)
	 * 
	 * @throws NullPointerException if the specified object is null
	 */
	public static <E, K extends Comparable<? super E>> long binarySearchNext(HugeArray<E> array, K o) {
		if (o == null)
			throw new NullPointerException();
		
		long size = array.size();
		if (size == 0)
			return ~0;
		
		long low = 0, high = size - 1;
		if (o.compareTo(array.get(high)) >= 0)
			return ~size;
		else
			high--;
		while (low < high) {
			long i = (low + high + 1) / 2;
			if (o.compareTo(array.get(i)) < 0)
				high = i - 1;
			else
				low = i;
		}
		int lastCmp = o.compareTo(array.get(low));
		return (lastCmp < 0) ? ~low : (lastCmp == 0) ? low+1 : ~(low+1);
	}
	
	/**
	 * Performs binary search of the specified object on given <tt>HugeArray</tt>
	 * instance and returns index of position right after the last element which
	 * is less or equal to given object.<br>
	 * If list contains object, returned value is equal to position, otherwise it's
	 * equal to binary inverse of the position.<br>
     * The array must be sorted into ascending order according to the specified comparator
     * prior to making this call.
	 * 
	 * @param array <tt>HugeArray</tt> to be searched
	 * @param o object to search for
	 * @param comparator the comparator by which the list is ordered or null (if natural
	 *                   ordering should be used)
	 * 
	 * @return index of position right after the last element strictly less than key or it's inverse
	 * 
	 * @throws NullPointerException if the specified object is null and comparator doesn't permit null elements
	 */
	public static <T, E extends T, K extends T> long binarySearchNext(HugeArray<E> array, K o, Comparator<T> comparator) {
		if (comparator == null) {
			@SuppressWarnings("unchecked")
			Comparable<? super E> comparable = (Comparable<? super E>) o;
			return binarySearchNext(array, comparable);
		}

		long size = array.size();
		if (size == 0)
			return ~0;
		
		long low = 0, high = size - 1;
		if (comparator.compare(o, array.get(high)) >= 0)
			return ~size;
		else
			high--;
		while (low < high) {
			long i = (low + high + 1) / 2;
			if (comparator.compare(o, array.get(i)) < 0)
				high = i - 1;
			else
				low = i;
		}
		int lastCmp = comparator.compare(o, array.get(low));
		return (lastCmp < 0) ? ~low : (lastCmp == 0) ? low+1 : ~(low+1);
	}
	

	/**
	 * Returns true if specified collection contains null.
	 * 
	 * @param c - collection to check for null
	 * 
	 * @return true if specified collection contains null
	 */
	public static boolean containsNull(Collection<?> c) {
		boolean contains = false;
		try {
			contains = c.contains(null);
		}
		catch (NullPointerException ignore) {};
		return contains;
	}
	
    /**
     * Returns a string representation of the given <tt>Iterable</tt>.<br>
     * The string representation consists of a list of the <tt>Iterable</tt>'s
     * elements separated by commas in the order they are returned by its iterator.<br>
     * <tt>Iterable</tt> is enclosed in square brackets (<tt>"[]"</tt>).<br>
     * If <tt>Iterable</tt> contains too many elements, only first elements will be shown,
     * followed by three-dot (<tt>"..."</tt>).
     * 
	 * @param c - given <tt>Iterable</tt>
	 * @param onSelf - denotes string value assigned to <tt>Iterable</tt> itself
	 *                 if it will be one of the <tt>Iterable</tt> elements.
	 *                 
	 * @return a string representation of the given <tt>Iterable</tt>
     */
	public static String toString(Iterable<?> c, String onSelf) {
		if (c == null)
			return "null";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (Iterator<?> iter = c.iterator(); iter.hasNext(); iter.next()) {
        	Object e = iter.next();
            sb.append(e == c ? onSelf : e);
            if (sb.length() > 1000) {
            	sb.append(',').append(" ...");
            	break;
            }
            if (iter.hasNext())
            	sb.append(',').append(' ');
        }
        sb.append(']');
        return sb.toString();
	}
	
    /**
     * Returns a string representation of the given <tt>Iterable</tt>.<br>
     * The string representation consists of a list of the <tt>Iterable</tt>'s
     * elements separated by commas in the order they are returned by its iterator.<br>
     * <tt>Iterable</tt> enclosed in square brackets (<tt>"[]"</tt>).<br>
     * If <tt>Iterable</tt> contains too many elements, only first elements will be shown,
     * followed by three-dot (<tt>"..."</tt>).
     * 
	 * @param c - given <tt>Iterable</tt>
	 * 
	 * @return a string representation of the given <tt>Iterable</tt>
     */
	public static String toString(Iterable<?> c) {
		return toString(c, "(this)");
	}
	
	private Collectionz() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	};
}
