package zjava.collection;

public final class Const {
	
    /**
     * The maximum size of array to allocate.<br>
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
	static public final int MAX_ARRAY_SIZE = -(-Integer.MIN_VALUE + 8);

}
