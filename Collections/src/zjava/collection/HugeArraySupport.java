package zjava.collection;

/**
 * Marker interface which indicates that array implementation can handle more than 
 * <tt>Integer.MAX_VALUE</tt> elements and provides means of accessing
 * elements beyond this limit.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 * @see HugeArray
 */
public interface HugeArraySupport<E> extends HugeCapacitySupport {

	/**
	 * Provides means of accessing elements above <tt>Integer.MAX_VALUE</tt> limit
	 * 
	 * @return <tt>HugeArray</tt> object, used to work with lists of huge capacity
	 */
	HugeArray<E> asHuge();
}
