package zjava.collection.primitive;

/**
 * Early draft of ByteSet
 * 
 * Implemented as a Bitmap 
 * 
 * TODO javadoc
 * 
 * @author Ivan Zaitsau
 * 
 */
public class ByteSet {
	
	private static final int WORDS = 4;
	private static final int WORD_BITSIZE = 6;
	private static final int WORD_MASK = (1 << WORD_BITSIZE) - 1;
	
	private int size;
	private long[] bits = new long[4];
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean contains(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		return (bits[i >>> WORD_BITSIZE] & (1L << (i & WORD_MASK))) != 0;
	}

	public boolean add(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		final int wi = i >>> WORD_BITSIZE;
		final long bit = 1L << (i & WORD_MASK);
		if ((bits[wi] & bit) == 0) {
			bits[wi] |= bit;
			return true;
		}
		else return false;
	}

	public boolean remove(byte v) {
		final int i = (int) v - Byte.MIN_VALUE;
		final int wi = i >>> WORD_BITSIZE;
		final long bit = 1L << (i & WORD_MASK);
		if ((bits[wi] & bit) != 0) {
			bits[wi] &= ~bit;
			return true;
		}
		else return false;
	}

	public boolean addAll(byte... values) {
		boolean result = false;
		for (byte v : values)
			result |= add(v);
		return result;
	}

	public boolean removeAll(byte... values) {
		boolean result = false;
		for (byte v : values)
			result |= add(v);
		return result;
	}

	public void clear() {
		size = 0;
		for (int i = 0; i < WORDS; i++) bits[i] = 0;
	}

	public byte[] toArray() {
		int arrSize = 0;
		for (int i = 0; i < WORDS; i++) arrSize += Long.bitCount(bits[i]);
		byte[] arr = new byte[arrSize];
		
		int ai = 0;
		for (int i = 0; i < WORDS; i++) {
			long w = bits[i];
			if (w > 0)
				for (int j = 0; j <= WORD_MASK; j++)
					if ((w & (1L << j)) != 0)
						arr[ai++] = (byte) (Byte.MIN_VALUE + (i << WORD_BITSIZE) + j);
		}
		return arr;
	}
}
