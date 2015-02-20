package zjava.collection;

/**
 * Implementation of custom hash-function
 * 
 * @param <T> - the type of objects that may be hashed and
 *              checked for equality by this comparator
 */
public abstract class Hasher<T> {
	
	/**
	 * Default hash-function which uses standard Object hashCode and equals methods
	 */
	final public static Hasher<Object> DEFAULT_HASHER = new Hasher<Object>() {
		
		/**
		 * Default hashCode implementation.<br>
		 * Returns method's argument <tt>hashCode()</tt> or 0
		 * if argument is equal to <tt>null</tt>.
		 */
		protected int hashCode(Object o) {
			return (o == null) ? 0 : o.hashCode();
		}
	};

	/**
	 * Custom hash-function provided by the Hasher.<br>
	 * See {@link Object#hashCode()} for the general contract of hashCode. 
	 * @param o
	 * @return
	 */
	abstract protected int hashCode(T o);
	
	/**
	 * Optional alternative implementation of equals method.<br>
	 * By default considers objects equal if both are null or
	 * o1.equals(o2) returns true;
	 * 
	 * @param o1 - 1st object to check for equality
	 * @param o2 - 2nd object to check for equality
	 * 
	 * @return <code>true</code> if objects are considered to be
	 *                           equal.
	 */
	protected boolean equals(T o1, T o2) {
		return (o1 == null) ? o2 == null : o1.equals(o2);
	}
}
