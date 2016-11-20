package zjava.common;

/**
 * This class serves as a facade to commonly used utility-methods from other utility-classes.
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 *
 */
public final class Z {

	private Z() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
