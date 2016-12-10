package zjava.collection;

/**
 * The <tt>HugeList</tt> interface provides number of methods to access
 * list elements beyond <tt>Integer.MAX_VALUE</tt> limit.<br>
 * 
 * @param <E> - the type of elements in this list
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public interface HugeList<E> extends HugeArray<E> {
	
	/**
	 * Returns the number of elements in the backing list.<br>
     * If this list contains more than <tt>Long.MAX_VALUE</tt> elements,
     * returns <tt>Long.MAX_VALUE</tt>.
     * 
	 * @return number of elements in the backing list
	 */
	long size();
	
    /**
     * Returns the element at the specified position in the backing list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in the backing list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	E get(long index);
	
    /**
     * Replaces the element at the specified position in backing list with the
     * specified element (optional operation).
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>set</tt> operation
     *         is not supported by this list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and
     *         this list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	E set(long index, E element);
	
    /**
     * Inserts the specified element at the specified position in the backing
     * list (optional operation). Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by backing list
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this list
     * @throws NullPointerException if the specified element is null and
     *         backing list does not permit null elements
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt; size()</tt>)
     */	
	void add(long index, E element);
	
    /**
     * Removes the element at the specified position in the backing list
     * (optional operation). Shifts any subsequent elements to the left
     * (subtracts one from their indices). Returns the element that was
     * removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by backing list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &gt;= size()</tt>)
     */
	E remove(long index);
}
