package extension.collection;

import java.util.List;

/**
 * Marker interface which indicates that {@link List} implementation can
 * handle more than <tt>Integer.MAX_VALUE</tt> elements and provides means
 * of accessing elements beyond this limit.
 * 
 * @author Ivan Zaitsau
 */
public interface HugeCapacityList<E> extends List<E> {
	FarListAccess<E> far();
}
