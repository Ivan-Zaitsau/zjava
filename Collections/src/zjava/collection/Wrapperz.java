package zjava.collection;

import java.io.Serializable;

/**
 * This class contains number of methods to wrap collections of primitives
 * with implementations of different interfaces.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
final public class Wrapperz {

	/**
	 * Wrapper of {@code null} value.<br>
	 * Equal to {@code null} and any other object which is equal to {@code null}.
	 */
	public static final Object NULL = new Serializable() {
		private static final long serialVersionUID = 201503220000L;
		public boolean equals(Object o) {
			return o == null || o.equals(null);
		}
		public int hashCode() {
			return 0;
		}
		public String toString() {
			return "null";
		}
		// - required to preserve singleton property on serialization.
		private Object readResolve() {
			return NULL;
		}
	};
	
	private Wrapperz() {
		throw new AssertionError("Instantiaion of utility class " + getClass().getName() + " is prohibited");
	}
}
