package zjava.collection;

/**
 * The <tt>HugeCapacity</tt> interface provides a <tt>size()</tt> method
 * to retrieve real size (beyond <tt>Integer.MAX_VALUE</tt> limit) of
 * the backing collection
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public interface HugeCapacity {
	
	/**
	 * Returns the number of elements in the backing collection.
     * If this collection contains more than <tt>Long.MAX_VALUE</tt> elements,
     * returns <tt>Long.MAX_VALUE</tt>.
     *
	 * @return number of elements in the backing collection
	 */
	long size();
}
