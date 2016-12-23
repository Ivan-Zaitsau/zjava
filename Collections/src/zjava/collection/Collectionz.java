package zjava.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

/**
 * This class contains number of useful methods that operate on collections.
 *
 * <p>The methods of this class usually throw a <tt>NullPointerException</tt>
 * if the collections or class objects provided to them are null.<br>
 *
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
final public class Collectionz {

	private static final int TO_STRING_SIZE_THRESHOLD = 4000;
	
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
	 * Returns immutable representation of a range of <tt>Byte</tt> values.<br>
	 * Returned range is open-ended and doesn't contain point passed as second
	 * parameter.<br>
	 * Closed range can be obtained by calling {@link RangeSet#closed() closed()}
	 * method.<br>
	 * <br>
	 * Range returned is also an implementation of {@link java.util.Set Set}
	 * interface. Calling any method that tries to change returned RangeSet will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param fromInclusive - first element of the range
	 * @param toExclusive - value right after the last element of the range
	 * 
	 * @return RangeSet which corresponds to specified interval [fromInclusive; toExclusive)
	 */
	public static RangeSet<Byte> range(byte fromInclusive, byte toExclusive) {
		return new ByteRangeSet(fromInclusive, toExclusive);
	}

	/**
	 * Returns immutable representation of a range of <tt>Short</tt> values.<br>
	 * Returned range is open-ended and doesn't contain point passed as second
	 * parameter.<br>
	 * Closed range can be obtained by calling {@link RangeSet#closed() closed()}
	 * method.<br>
	 * <br>
	 * Range returned is also an implementation of {@link java.util.Set Set}
	 * interface. Calling any method that tries to change returned RangeSet will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param fromInclusive - first element of the range
	 * @param toExclusive - value right after the last element of the range
	 * 
	 * @return RangeSet which corresponds to specified interval [fromInclusive; toExclusive)
	 */
	public static RangeSet<Short> range(short fromInclusive, short toExclusive) {
		return new ShortRangeSet(fromInclusive, toExclusive);
	}

	/**
	 * Returns immutable representation of a range of <tt>Integer</tt> values.<br>
	 * Returned range is open-ended and doesn't contain point passed as second
	 * parameter.<br>
	 * Closed range can be obtained by calling {@link RangeSet#closed() closed()}
	 * method.<br>
	 * <br>
	 * Range returned is also an implementation of {@link java.util.Set Set}
	 * interface. Calling any method that tries to change returned RangeSet will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param fromInclusive - first element of the range
	 * @param toExclusive - value right after the last element of the range
	 * 
	 * @return RangeSet which corresponds to specified interval [fromInclusive; toExclusive)
	 */
	public static RangeSet<Integer> range(int fromInclusive, int toExclusive) {
		return new IntegerRangeSet(fromInclusive, toExclusive);
	}

	/**
	 * Returns immutable representation of a range of <tt>Long</tt> values.<br>
	 * Returned range is open-ended and doesn't contain point passed as second
	 * parameter.<br>
	 * Closed range can be obtained by calling {@link RangeSet#closed() closed()}
	 * method.<br>
	 * <br>
	 * Range returned is also an implementation of {@link java.util.Set Set}
	 * interface. Calling any method that tries to change returned RangeSet will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param fromInclusive - first element of the range
	 * @param toExclusive - value right after the last element of the range
	 * 
	 * @return RangeSet which corresponds to specified interval [fromInclusive; toExclusive)
	 */
	public static RangeSet<Long> range(long fromInclusive, long toExclusive) {
		return new LongRangeSet(fromInclusive, toExclusive);
	}

	/**
	 * Returns immutable representation of a range of <tt>Character</tt> values.<br>
	 * Returned range is open-ended and doesn't contain point passed as second
	 * parameter.<br>
	 * Closed range can be obtained by calling {@link RangeSet#closed() closed()}
	 * method.<br>
	 * <br>
	 * Range returned is also an implementation of {@link java.util.Set Set}
	 * interface. Calling any method that tries to change returned RangeSet will
	 * throw an {@link UnsupportedOperationException}.
	 * 
	 * @param fromInclusive - first element of the range
	 * @param toExclusive - value right after the last element of the range
	 * 
	 * @return RangeSet which corresponds to specified interval [fromInclusive; toExclusive)
	 */
	public static RangeSet<Character> range(char fromInclusive, char toExclusive) {
		return new CharacterRangeSet(fromInclusive, toExclusive);
	}

	// - actual implementation of toString() method
	// - keeps track of visited Iterable's and arrays of Object's
	// - limits returned String size to approximately 'charsLeft' characters
	private static String toString(Object[] array, String onSelf, Set<Object> visited, int charsLeft) {
		if (array == null)
			return "null";
		if (!visited.add(array))
			return "[...]";

		StringBuilder result = new StringBuilder();
		result.append('[');
        for (int i = 0; i < array.length; i++) {
        	Object e = array[i];
        	if (e == array)
        		result.append(onSelf);
        	else if (e instanceof Iterable<?>)
        		result.append(toString((Iterable<?>) e, "(this)", visited, charsLeft - result.length()));
        	else if (e instanceof Object[])
        		result.append(toString((Object[]) e, "(this)", visited, charsLeft - result.length()));
        	else 
        		result.append(e);

            if (result.length() > charsLeft) {
            	result.append(", ...");
            	break;
            }
            if (i+1 < array.length)
            	result.append(',').append(' ');
        }
        result.append(']');
        return result.toString();
	}

	// - actual implementation of toString() method
	// - keeps track of visited Iterable's and arrays of Object's
	// - limits returned String size to approximately 'charsLeft' characters
	private static String toString(Iterable<?> c, String onSelf, Set<Object> visited, int charsLeft) {
		if (c == null)
			return "null";
		if (!visited.add(c))
			return "[...]";

		StringBuilder result = new StringBuilder();
		result.append('[');
        for (Iterator<?> iter = c.iterator(); iter.hasNext(); ) {
        	Object e = iter.next();
        	if (e == c)
        		result.append(onSelf);
        	else if (e instanceof Iterable<?>)
        		result.append(toString((Iterable<?>) e, "(this)", visited, charsLeft - result.length()));
        	else if (e instanceof Object[])
        		result.append(toString((Object[]) e, "(this)", visited, charsLeft - result.length()));
        	else 
        		result.append(e);

            if (result.length() > charsLeft) {
            	result.append(", ...");
            	break;
            }
            if (iter.hasNext())
            	result.append(',').append(' ');
        }
        result.append(']');
        return result.toString();
	}
	
    /**
     * Returns a string representation of the given <tt>Iterable</tt>.<br>
     * The string representation consists of a list of the <tt>Iterable</tt>'s
     * elements separated by commas in the order they are returned by its iterator.
     * <tt>Iterable</tt> is enclosed in square brackets (<tt>"[]"</tt>).
     * 
     * <p>If <tt>Iterable</tt> contains too many elements, only first elements will be shown,
     * followed by three-dot (<tt>"..."</tt>).
     * 
     * <p> If given {@code Iterable} is null, string "null" is returned.
     * 
	 * @param c - given <tt>Iterable</tt>
	 * @param onSelf - denotes string value assigned to <tt>Iterable</tt> itself
	 *                 if it will be one of the <tt>Iterable</tt> elements.
	 *                 
	 * @return a string representation of the given <tt>Iterable</tt>
     */
	public static String toString(Iterable<?> c, String onSelf) {
		return toString(c, onSelf, new HashedSet<Object>(Hasher.IDENTITY), TO_STRING_SIZE_THRESHOLD);
	}
	
    /**
     * Returns a string representation of the given <tt>Iterable</tt>.<br>
     * The string representation consists of a list of the <tt>Iterable</tt>'s
     * elements separated by commas in the order they are returned by its iterator.
     * <tt>Iterable</tt> enclosed in square brackets (<tt>"[]"</tt>).
     * 
     * <p>If <tt>Iterable</tt> contains too many elements, only first elements will be shown,
     * followed by three-dot (<tt>"..."</tt>).
     * 
     * <p> If given {@code Iterable} is null, string "null" is returned.
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
