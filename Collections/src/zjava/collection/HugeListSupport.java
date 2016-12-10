package zjava.collection;

/**
 * Marker interface which indicates that list can handle more than 
 * <tt>Integer.MAX_VALUE</tt> elements and provides means of accessing
 * elements beyond this limit.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 * @see HugeList
 */
public interface HugeListSupport<E> extends HugeArraySupport<E> {
	
	/**
	 * Provides means of accessing list elements above <tt>Integer.MAX_VALUE</tt> limit.
	 * 
	 * @return <tt>HugeList</tt> object, used to work with lists of huge capacity
	 */
	HugeList<E> asHuge();
}
