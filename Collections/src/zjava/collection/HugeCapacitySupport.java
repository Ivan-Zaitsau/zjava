package zjava.collection;

/**
 * Marker interface which indicates that collection can handle more than 
 * <tt>Integer.MAX_VALUE</tt> elements and provides means of acquiring
 * actual collection size.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 * @see HugeCapacity
 */
public interface HugeCapacitySupport {

	/**
	 * Provides means of querying real size of huge collections (which have more than
	 * <tt>Integer.MAX_VALUE</tt> elements).
	 * 
	 * @return <tt>HugeCapacity</tt> object, used to query real size of the underlying collection.
	 */
	HugeCapacity asHuge();
}