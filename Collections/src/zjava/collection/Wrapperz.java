package zjava.collection;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import zjava.collection.primitive.BooleanArray;
import zjava.collection.primitive.BooleanList;
import zjava.collection.primitive.ByteList;
import zjava.collection.primitive.CharList;
import zjava.collection.primitive.DoubleList;
import zjava.collection.primitive.FloatList;
import zjava.collection.primitive.IntList;
import zjava.collection.primitive.LongList;
import zjava.collection.primitive.ShortList;

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
	 * Root class for wrappers of lists of primitives.
	 * 
	 * @since Zjava 1.0
	 * 
	 * @author Ivan Zaitsau
	 */
	abstract private static class AbstractPrimitiveListWrapper<E> extends AbstractList<E> implements List<E>, HugeListSupport<E> {
		
		transient HugeList<E> hugeView;
		
		abstract long getSize();
		
		abstract E doGet(long index);
		
		abstract E doSet(long index, E element);

		abstract void doAdd(E element);
		
		abstract void doAdd(long index, E element);

		abstract E doRemove(long index);

		public int size() {
			long size = getSize();
			return size < Integer.MAX_VALUE ? (int) size : Integer.MAX_VALUE;
		}

		public E get(final int index) {
			return doGet(index);
		}

		public E set(final int index, final E element) {
			return doSet(index, element);
		}

		public boolean add(final E e) {
			doAdd(e);
			return true;
		}

		public void add(final int index, final E element) {
			doAdd(index, element);
		}

		public E remove(final int index) {
			return doRemove(index);
		}

		public Iterator<E> iterator() {
			return new Iterator<E>() {

				/**
				 * Cursor position
				 */
				private long i = 0;
				
				/**
				 * Current (last returned) element index or -1 if element is not defined (or has been removed)
				 */
				private long last = -1;
				
				/**
				 * Expected version (modifications count) of the backing List 
				 */
				int expectedModCount = modCount;
				
				public boolean hasNext() {
					return i < getSize();
				}

		        public E next() {
		            checkForComodification();
		            try {
		            	E e = doGet(i);
		                last = i++;
		                return e;
		            }
		            catch (IndexOutOfBoundsException e) {
		                checkForComodification();
		                throw new NoSuchElementException();
		            }
		        }

		        public void remove() {
		            if (last < 0)
		                throw new IllegalStateException();
		            checkForComodification();
		            
		            try {
		            	doRemove(last);
		                i--;
		                last = -1;
		                expectedModCount = modCount;
		            } catch (IndexOutOfBoundsException e) {
		                throw new ConcurrentModificationException();
		            }
		        }
		        
				void checkForComodification() {
					if (expectedModCount != modCount)
						throw new ConcurrentModificationException();
				}
			};
		}

		public HugeList<E> asHuge() {
			if (hugeView == null) {
				hugeView = new HugeList<E>() {
					public long size() {
						return getSize();
					}
					public E get(long index) {
						return doGet(index);
					}
					public E set(long index, E element) {
						return doSet(index, element);
					}
					public void add(long index, E element) {
						doAdd(index, element);
					}
					public E remove(long index) {
						return doRemove(index);
					}
				};
			}
			return hugeView;
		}
	}

	private static class BooleanListWrapper extends AbstractPrimitiveListWrapper<Boolean> implements List<Boolean>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private BooleanList list;
		
		private BooleanListWrapper(BooleanList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Boolean doGet(long index) {
			return list.get(index);
		}

		Boolean doSet(long index, Boolean element) {
			return list.set(index, element);
		}

		void doAdd(Boolean e) {
			list.add(e);
		}

		void doAdd(long index, Boolean element) {
			list.add(index, element);
		}

		Boolean doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}

		public Object clone() {
	    	try {
	    		BooleanListWrapper clone = (BooleanListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (BooleanList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class ByteListWrapper extends AbstractPrimitiveListWrapper<Byte> implements List<Byte>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private ByteList list;
		
		private ByteListWrapper(ByteList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Byte doGet(long index) {
			return list.get(index);
		}

		Byte doSet(long index, Byte element) {
			return list.set(index, element);
		}

		void doAdd(Byte e) {
			list.add(e);
		}
		
		void doAdd(long index, Byte element) {
			list.add(index, element);
		}

		Byte doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}
		
		public Object clone() {
	    	try {
	    		ByteListWrapper clone = (ByteListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (ByteList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}
	
	private static class CharListWrapper extends AbstractPrimitiveListWrapper<Character> implements List<Character>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private CharList list;
		
		private CharListWrapper(CharList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Character doGet(long index) {
			return list.get(index);
		}

		Character doSet(long index, Character element) {
			return list.set(index, element);
		}

		void doAdd(Character e) {
			list.add(e);
		}

		void doAdd(long index, Character element) {
			list.add(index, element);
		}

		Character doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}
		
		public Object clone() {
	    	try {
	    		CharListWrapper clone = (CharListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (CharList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class DoubleListWrapper extends AbstractPrimitiveListWrapper<Double> implements List<Double>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private DoubleList list;
		
		private DoubleListWrapper(DoubleList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Double doGet(long index) {
			return list.get(index);
		}

		Double doSet(long index, Double element) {
			return list.set(index, element);
		}

		void doAdd(Double e) {
			list.add(e);
		}

		void doAdd(long index, Double element) {
			list.add(index, element);
		}

		Double doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}
		
		public Object clone() {
	    	try {
	    		DoubleListWrapper clone = (DoubleListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (DoubleList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class FloatListWrapper extends AbstractPrimitiveListWrapper<Float> implements List<Float>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private FloatList list;
		
		private FloatListWrapper(FloatList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Float doGet(long index) {
			return list.get(index);
		}

		Float doSet(long index, Float element) {
			return list.set(index, element);
		}

		void doAdd(Float e) {
			list.add(e);
		}

		void doAdd(long index, Float element) {
			list.add(index, element);
		}

		Float doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}
		
		public Object clone() {
	    	try {
	    		FloatListWrapper clone = (FloatListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (FloatList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class IntListWrapper extends AbstractPrimitiveListWrapper<Integer> implements List<Integer>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private IntList list;
		
		private IntListWrapper(IntList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Integer doGet(long index) {
			return list.get(index);
		}

		Integer doSet(long index, Integer element) {
			return list.set(index, element);
		}

		void doAdd(Integer e) {
			list.add(e);
		}

		void doAdd(long index, Integer element) {
			list.add(index, element);
		}

		Integer doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}
		
		public Object clone() {
	    	try {
	    		IntListWrapper clone = (IntListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (IntList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class LongListWrapper extends AbstractPrimitiveListWrapper<Long> implements List<Long>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private LongList list;
		
		private LongListWrapper(LongList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Long doGet(long index) {
			return list.get(index);
		}

		Long doSet(long index, Long element) {
			return list.set(index, element);
		}

		void doAdd(Long e) {
			list.add(e);
		}

		void doAdd(long index, Long element) {
			list.add(index, element);
		}

		Long doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}

		public Object clone() {
	    	try {
	    		LongListWrapper clone = (LongListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (LongList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class ShortListWrapper extends AbstractPrimitiveListWrapper<Short> implements List<Short>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		private ShortList list;
		
		private ShortListWrapper(ShortList list) {
			this.list = list;
		}
		
		long getSize() {
			return list.size();
		}

		Short doGet(long index) {
			return list.get(index);
		}

		Short doSet(long index, Short element) {
			return list.set(index, element);
		}

		void doAdd(Short e) {
			list.add(e);
		}

		void doAdd(long index, Short element) {
			list.add(index, element);
		}

		Short doRemove(long index) {
			return list.remove(index);
		}

		public void clear() {
			list.clear();
		}

		public Object clone() {
	    	try {
	    		ShortListWrapper clone = (ShortListWrapper) super.clone();
	    		clone.modCount = 0;
	    		clone.hugeView = null;
	    		clone.list = (ShortList) list.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	public static List<Boolean> asList(BooleanList list) {
		return new BooleanListWrapper(list);
	}

	public static List<Byte> asList(ByteList list) {
		return new ByteListWrapper(list);
	}
	
	public static List<Character> asList(CharList list) {
		return new CharListWrapper(list);
	}

	public static List<Double> asList(DoubleList list) {
		return new DoubleListWrapper(list);
	}

	public static List<Float> asList(FloatList list) {
		return new FloatListWrapper(list);
	}

	public static List<Integer> asList(IntList list) {
		return new IntListWrapper(list);
	}

	public static List<Long> asList(LongList list) {
		return new LongListWrapper(list);
	}

	public static List<Short> asList(ShortList list) {
		return new ShortListWrapper(list);
	}

	private static class BooleanArrayWrapper implements HugeArray<Boolean>, Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 201611211700L;
		
		private BooleanArray array;
		
		private BooleanArrayWrapper(BooleanArray arr) {
			array = arr;
		}

		public long size() {
			return array.length();
		}

		public Boolean set(final long index, final Boolean value) {
			return array.set(index, value);
		}

		public Boolean get(final long index) {
			return array.get(index);
		}
		
		public Object clone() {
	    	try {
	    		BooleanArrayWrapper clone = (BooleanArrayWrapper) super.clone();
	    		clone.array = (BooleanArray) array.clone();
	    		return clone;
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}
	
	public static HugeArray<Boolean> asHugeArray(BooleanArray arr) {
		return new BooleanArrayWrapper(arr);
	}
	
	private Wrapperz() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
