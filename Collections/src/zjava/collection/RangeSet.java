package zjava.collection;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Immutable representation of numbers range.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public abstract class RangeSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 201612092300L;
	
	final long from;
	final long to;
	final int d;
	final boolean isClosedRange;

	RangeSet(long from, long to, boolean isClosedRange) {
		this.from = from;
		this.to = to;
		this.d = (from > to) ? -1 : 1;
		this.isClosedRange = isClosedRange;
	}

	/**
	 * Returns <tt>RangeSet</tt> which represents <i>closed</i> variant of this range.<br>
	 * If range is already <i>closed</i>, the method returns this range itself.<br>
	 * <br>
	 * In other words, if our <tt>RangeSet</tt> represents either [a, b) or [a, b] interval,
	 * interval [a, b] is returned.
	 * 
	 * @return <i>closed</i> range corresponding to this range
	 */
	abstract public RangeSet<E> closed();
	
	abstract E asObject(long v);
	
	abstract long asPrimitive(Object o);

	/**
     * Returns the number of elements in this range.<br>
     * If this range contains more than <tt>Integer.MAX_VALUE</tt> elements,
     * returns <tt>Integer.MAX_VALUE</tt> instead.
     * 
	 * @return size of this range
	 */
	public int size() {
		if (from == to)
			return isClosedRange ? 1 : 0;
		long diff = (d < 0) ? from-to : to-from;
		// - diff can be less than 0 because of long overflow
		return (diff >= 0) && (diff < Integer.MAX_VALUE) ? (int)(diff + ((isClosedRange) ? 1 : 0)) : Integer.MAX_VALUE;		
	}

	// - returns true if our range contains specified value
	private boolean contains(long value) {
		if (value == to)
			return isClosedRange;
		
		if (d < 0)
			return from >= value & value > to;
		else
			return from <= value & value < to;
	}

    /**
     * Returns <tt>true</tt> if this range contains the specified value.
     *
     * @param o element whose presence in this range is to be tested
     * @return <tt>true</tt> if this range contains the specified element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this range of values
     * @throws NullPointerException if the specified element is null
     */
	public boolean contains(Object o) {
		return contains(asPrimitive(o));
	}

    /**
     * {@inheritDoc}
     *
     * <p> If specified collection is also a <tt>RangeSet</tt> then it checks that
     * specified range lies within our range borders.<br> Otherwise it calls to
     * {@linkplain AbstractCollection#containsAll (Collection) method of superclass}
     * which iterates over the specified collection, checking each element returned
     * by the iterator in turn to see if it's contained in this collection. If all
     * elements are so contained <tt>true</tt> is returned, otherwise <tt>false</tt>.
     *
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @see #contains(Object)
     */
	public boolean containsAll(Collection<?> c) {
		if (c instanceof RangeSet<?>) {
			RangeSet<?> other = (RangeSet<?>) c;
			return this.contains(other.asObject(other.from))
					&& this.contains(other.isClosedRange ? other.to : other.to-other.d);
		}
		return super.containsAll(c);
	}
	
	private class RangeSetIterator implements Iterator<E> {

		private long v;
		
		RangeSetIterator() {
			v = from;
		}
		
		public boolean hasNext() {
			return contains(v);
		}

		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			E o = asObject(v);
			v += d;
			return o;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * <p>
		 * This implementation always throws an {@code UnsupportedOperationException}.
		 * 
		 * @throws UnsupportedOperationException {@inheritDoc}
		 * @throws IllegalArgumentException {@inheritDoc}
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
    /**
     * Returns an iterator over the elements in this range.
     *
     * @return an <tt>Iterator</tt> over the elements in this range
     */
    public Iterator<E> iterator() {
    	return new RangeSetIterator();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an {@code UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    public void clear() {
    	throw new UnsupportedOperationException();    	
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an {@code UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public boolean addAll(Collection<? extends E> c) {
    	throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an {@code UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c) {
    	throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an {@code UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c) {
    	throw new UnsupportedOperationException();
    }

    /*
     * Compares the specified RangeSet with this RangeSet for equality.
     * 
     * Returns true if the specified RangeSet represents the same interval
     * and primitive values within interval are mapped to objects of the same class.
     * 
     * Such implementation ensures that RangeSet follows contract on equals
     * for Set interface.
     */
    private boolean equals(RangeSet<?> other) {
		final int thisSize = size(), otherSize = other.size();
		if (thisSize != otherSize)
			return false;
		if ((thisSize | otherSize) == 0)
			return true;
		return asObject(0).equals(other.asObject(0))
				&& contains(other.from) && contains((other.isClosedRange) ? other.to : other.to-other.d);    	
    }
    
    public boolean equals(Object o) {
    	if (this == o)
    		return true;

    	if (!(o instanceof Set<?>))
    	    return false;
    	
    	if (o instanceof RangeSet<?>) {
    		return equals((RangeSet<?>) o);
    	}

    	Set<?> other = (Set<?>) o;
    	if (size() != other.size())
    	    return false;
        try {
            return containsAll(other);
        }
        catch (ClassCastException unused)   {
            return false;
        }
        catch (NullPointerException unused) {
            return false;
        }
    }
    
	public RangeSet<E> clone() {
    	try {
    	    @SuppressWarnings("unchecked")
    		RangeSet<E> clone = (RangeSet<E>) super.clone();
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }
    
    /**
     * Returns a string representation of this range.<br>
     * String representation consist of word "Range: " followed by
     * actual range which is represented by this collection.
     * 
     *  @return string representation of this range
     */
    public String toString() {
    	return "Range: [" + from + "; " + to + ((isClosedRange) ? ']' : ')');
    }
}

/**
 * Immutable representation of numbers range.<br>
 * Values of this <tt>RangeSet</tt> are mapped to instances of class {@link Byte}.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class ByteRangeSet extends RangeSet<Byte> {
	
	private static final long serialVersionUID = 201612092300L;

	ByteRangeSet(long from, long to, boolean isClosedRange) {super(from, to, isClosedRange);}

	ByteRangeSet(long from, long to) {super(from, to, false);}

	Byte asObject(long v) {return (byte)v;}

	long asPrimitive(Object o) {return (Byte) o;}

	public RangeSet<Byte> closed() {return (isClosedRange) ? this : new ByteRangeSet(from, to, true);}
	
	// - returns sum of all values in [from; to] interval
	private static final int sum(final int from, final int to) {
		return (int) (((long)to - (long)from + 1L) * ((long)to + (long)from) / 2);
	}
	
	public int hashCode() {
    	if (isEmpty())
    		return 0;
		int first = (int) from;
		int last = (int) ((isClosedRange) ? to : to-d);
    	return (d < 0) ? sum(last, first) : sum(first, last);
	}
}

/**
 * Immutable representation of numbers range.<br>
 * Values of this <tt>RangeSet</tt> are mapped to instances of class {@link Short}.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class ShortRangeSet extends RangeSet<Short> {
	
	private static final long serialVersionUID = 201612092300L;

	ShortRangeSet(long from, long to, boolean isClosedRange) {super(from, to, isClosedRange);}

	ShortRangeSet(long from, long to) {super(from, to, false);}

	Short asObject(long v) {return (short)v;}

	long asPrimitive(Object o) {return (Short) o;}

	public RangeSet<Short> closed() {return (isClosedRange) ? this : new ShortRangeSet(from, to, true);}
	
	// - returns sum of all values in [from; to] interval
	private static final int sum(final int from, final int to) {
		return (int) (((long)to - (long)from + 1L) * ((long)to + (long)from) / 2);
	}
	
	public int hashCode() {
    	if (isEmpty())
    		return 0;
		int first = (int) from;
		int last = (int) ((isClosedRange) ? to : to-d);
    	return (d < 0) ? sum(last, first) : sum(first, last);
	}
}

/**
 * Immutable representation of numbers range.<br>
 * Values of this <tt>RangeSet</tt> are mapped to instances of class {@link Integer}.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class IntegerRangeSet extends RangeSet<Integer> {
	
	private static final long serialVersionUID = 201612092300L;

	IntegerRangeSet(long from, long to, boolean isClosedRange) {super(from, to, isClosedRange);}

	IntegerRangeSet(long from, long to) {super(from, to, false);}

	Integer asObject(long v) {return (int) v;}

	long asPrimitive(Object o) {return (Integer) o;}

	public RangeSet<Integer> closed() {return (isClosedRange) ? this : new IntegerRangeSet(from, to, true);}
	
	// - returns sum of all values in [from; to] interval
	private static final int sum(final int from, final int to) {
		return (int) (((long)to - (long)from + 1L) * ((long)to + (long)from) / 2);
	}
	
	public int hashCode() {
    	if (isEmpty())
    		return 0;
		int first = (int) from;
		int last = (int) ((isClosedRange) ? to : to-d);
    	return (d < 0) ? sum(last, first) : sum(first, last);
	}
}

/**
 * Immutable representation of numbers range.<br>
 * Values of this <tt>RangeSet</tt> are mapped to instances of class {@link Long}.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class LongRangeSet extends RangeSet<Long> {
	
	private static final long serialVersionUID = 201612092300L;

	LongRangeSet(long from, long to, boolean isClosedRange) {super(from, to, isClosedRange);}

	LongRangeSet(long from, long to) {super(from, to, false);}

	Long asObject(long v) {return v;}

	long asPrimitive(Object o) {return (Long) o;}

	public RangeSet<Long> closed() {return (isClosedRange) ? this : new LongRangeSet(from, to, true);}
	
    // - method returns sum of (ai ^ x) for each ai in [0; b]
    // - b is considered to be unsigned integer
    // - helper method for hashCode evaluation
    private static int xorSum(int b, int x) {
    	long nb = (b < 0) ? 0x80000000L + (b ^ 0x80000000) : b;
    	int sum = 0;
    	for (int i = 0; i < 32; i++) {
    		long bits = ((nb+1) >>> (i+1)) << i;
    		long bitsRemain = ((nb+1) & ((1L << (i+1)) - 1));
    		if ((x & (1L << i)) == 0) // - if current x bit is zero
    			bits += Math.max(0, bitsRemain - (1L << i));
    		else // - if current x bit is one
    			bits += Math.min(1L << i, bitsRemain);
    		
    		sum += bits << i; // - multiply by current bit power
    	}
    	return sum;
    }
    
    // - method returns sum of (ai ^ x) for each ai in [a; b]
    // - both a and b are considered to be unsigned integers
    // - helper method for hashCode evaluation
    private static int xorSum(int a, int b, int x) {
    	assert ((a ^ 0x80000000) <= (b ^ 0x80000000)); // - unsigned comparison

    	int suma = (a == 0) ? 0 : xorSum(a-1, x);
    	int sumb = (b == 0xFFFFFFFF) ? Integer.MIN_VALUE : xorSum(b, x);
    	
    	return sumb - suma;
    }

    public int hashCode() {
    	if (isEmpty())
    		return 0;
		long min = from;
		long max = (isClosedRange) ? to : to-d;
    	if (d < 0) {
    		long t = min; min = max; max = t;
    	}
    	int minh = (int) (min >>> 32), minl = (int) min;
    	int maxh = (int) (max >>> 32), maxl = (int) max;
    	
    	int hash;
    	if (minh == maxh)
    		hash = xorSum(minl, maxl, maxh);
    	else
    		hash = xorSum(minl, 0xFFFFFFFF, minh) + (maxh-minh-1) * Integer.MIN_VALUE + xorSum(0, maxl, maxh);
    	return hash;
    }
}

/**
 * Immutable representation of characters range.<br>
 * Values of this <tt>RangeSet</tt> are mapped to instances of class {@link Character}.
 * 
 * @since zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class CharacterRangeSet extends RangeSet<Character> {
	
	private static final long serialVersionUID = 201612092300L;

	CharacterRangeSet(long from, long to, boolean isClosedRange) {super(from, to, isClosedRange);}

	CharacterRangeSet(long from, long to) {super(from, to, false);}

	Character asObject(long v) {return (char)v;}

	long asPrimitive(Object o) {return (Character) o;}

	public RangeSet<Character> closed() {return (isClosedRange) ? this : new CharacterRangeSet(from, to, true);}
	
	// - returns sum of all values in [from; to] interval
	private static final int sum(final int from, final int to) {
		return (int) (((long)to - (long)from + 1L) * ((long)to + (long)from) / 2);
	}
	
	public int hashCode() {
    	if (isEmpty())
    		return 0;
		int first = (int) from;
		int last = (int) ((isClosedRange) ? to : to-d);
    	return (d < 0) ? sum(last, first) : sum(first, last);
	}
}