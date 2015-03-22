package zjava.common;

/**
 * This class contains number of useful general-purpose methods related to
 * {@code Object}.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
final public class Objectz {

	/**
     * Returns {@code true} if the arguments are equal to each other
     * and {@code false} otherwise.
     * 
     * @param o1 an object
     * @param o2 an object to be compared with {@code o1} for equality
     * @return {@code true} if the arguments are equal to each other
     *         and {@code false} otherwise
     * @see Object#equals(Object)
	 */
	public static boolean equals(Object o1, Object o2) {
		return (o1 == null) ? o2 == null : o1.equals(o2);
	}
	
    /**
     * Returns hash-code of a given argument or 0 if argument is {@code null}.
     *
     * @param o an object to return hash-code for
     * @return hash-code of a given argument or 0 if argument is {@code null}
     * @see Object#hashCode
     */
	public static int hashCode(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}
	
	private Objectz() {
		throw new AssertionError("Instantiaion of utility class " + getClass().getName() + " is prohibited");
	}
}
