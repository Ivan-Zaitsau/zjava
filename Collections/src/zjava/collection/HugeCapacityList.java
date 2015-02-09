package zjava.collection;

/**
 * Marker interface which indicates that list can handle more than 
 * <tt>Integer.MAX_VALUE</tt> elements and provides means of accessing
 * elements beyond this limit.
 * 
 * @author Ivan Zaitsau
 * @see FarListAccess
 */
public interface HugeCapacityList<E> {
	
	/**
	 * Provides means of accessing elements above <tt>Integer.MAX_VALUE</tt> limit
	 * 
	 * @return <tt>FarListAccess</tt> object, used to work with lists of huge capacity
	 */
	FarListAccess<E> far();
}
