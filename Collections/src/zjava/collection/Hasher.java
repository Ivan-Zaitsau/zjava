package zjava.collection;

import zjava.common.Objectz;

/**
 * Implementation of custom hash-function
 * 
 * @param <T> - the type of objects that may be hashed and
 *              checked for equality by this comparator
 *
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 * 
 */
public abstract class Hasher<T> {
	
	/**
	 * Default hash-function which uses standard Object hashCode and equals methods.
	 */
	final public static Hasher<Object> DEFAULT = new Hasher<Object>() {
		
		/**
		 * Default hashCode implementation.<br>
		 * @return method's argument <tt>hashCode()</tt> or 0
		 * if argument is equal to <tt>null</tt>.
		 */
		public int hashCode(Object o) {
			return Objectz.hashCode(o);
		}
	};

	/**
	 * Identity hash-function which uses identity hashCode() and identity equality (<tt>"=="</tt>)
	 * for objects comparisons.<br>
	 * 
	 * <p><b>WARNING: This implementation breaks general contract on Set and Map interfaces.</b>
	 */
	final public static Hasher<Object> IDENTITY = new Hasher<Object>() {
		
		/**
		 * Returns true if and only if o1 and o2 represent the same object
		 */
		public boolean equals(Object o1, Object o2) {
			return o1 == o2;
		}
		
		/**
		 * Identity hashCode.<br>
		 * Returns default <tt>Object.hashCode()</tt> for an Object
		 * as if it haven't been overridden.
		 * 
		 * @return method's argument identity <tt>hashCode()</tt>.
		 * 
		 * @see System#identityHashCode(Object) identityHashCode 
		 */
		public int hashCode(Object o) {
			return System.identityHashCode(o);
		}
	};

	/**
	 * Custom hash-function provided by the Hasher.<br>
	 * See {@link Object#hashCode()} for the general contract of hashCode. 
	 * @param o
	 * @return
	 */
	abstract public int hashCode(T o);
	
	/**
	 * Optional alternative implementation of equals method.<br>
	 * By default considers objects equal if both are null or
	 * o1.equals(o2) returns true;<br>
	 * 
	 * <p><b>WARNING: Overriding this method will break general contract on Set and Map interfaces.</b>
	 * 
	 * @param o1 - 1st object to check for equality
	 * @param o2 - 2nd object to check for equality
	 * 
	 * @return <code>true</code> if objects are considered to be
	 *                           equal.
	 */
	public boolean equals(T o1, T o2) {
		return Objectz.equals(o1, o2);
	}
}