package extension.collection;

public class BooleanArray {
	
	private static int ADDRESS_BITS = 6;
	private static int BITS = 1 << ADDRESS_BITS;
	private static int MASK = BITS - 1;
	
	private int length;
	private long[] data;

	public BooleanArray(int size) {
        if (size < 0)
            throw new NegativeArraySizeException("size < 0: " + size);
		this.length = size;
		data = new long[1 + ((size-1) >>> ADDRESS_BITS)];
	}

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + length;
    }
	
	private void rangeCheck(int index) {
		if (index >= length)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	public void setTrue(int index) {
		rangeCheck(index);
		data[index >>> ADDRESS_BITS] |= 1L << (index & MASK);
	}
	
	public void setFalse(int index) {
		rangeCheck(index);
		data[index >>> ADDRESS_BITS] &= ~(1L << (index & MASK));
	}
	
	public boolean get(int index) {
		return (data[index >>> ADDRESS_BITS] & (1L << (index & MASK))) > 0;
	}
	
	public int length() {
		return length;
	}
}
