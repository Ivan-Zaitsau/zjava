package zjava.collection;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
	 * Wrapper of {@code null} value.<br>
	 * Equal to {@code null} and any other object which is equal to {@code null}.
	 */
	public static final Object NULL = new java.io.Serializable() {
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
		// - required to preserve singleton property during serialization
		private Object readResolve() {
			return NULL;
		}
	};
	
	/**
	 * Root class for wrappers of lists of primitives.
	 * 
	 * @since Zjava 1.0
	 * 
	 * @author Ivan Zaitsau
	 */
	abstract private static class AbstractPrimitiveListWrapper<E> extends AbstractList<E> implements List<E>, HugeListAccess<E> {
		
		transient HugeList<E> hugeView;
		
		abstract long _size();
		
		abstract E _get(long index);
		
		abstract E _set(long index, E element);

		abstract void _add(E element);
		
		abstract void _add(long index, E element);

		abstract E _remove(long index);

		public int size() {
			long size = _size();
			return size < Integer.MAX_VALUE ? (int) size : Integer.MAX_VALUE;
		}

		public E get(int index) {
			return _get(index);
		}

		public E set(int index, E element) {
			return _set(index, element);
		}

		public boolean add(E e) {
			_add(e);
			return true;
		}

		public void add(int index, E element) {
			_add(index, element);
		}

		public E remove(int index) {
			return _remove(index);
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
					return i < _size();
				}

		        public E next() {
		            checkForComodification();
		            try {
		            	E e = _get(i);
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
		            	_remove(last);
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

		public HugeList<E> hugeView() {
			if (hugeView == null) {
				hugeView = new HugeList<E>() {
					public long size() {
						return _size();
					}
					public E get(long index) {
						return _get(index);
					}
					public E set(long index, E element) {
						return _set(index, element);
					}
					public void add(long index, E element) {
						_add(index, element);
					}
					public E remove(long index) {
						return _remove(index);
					}
				};
			}
			return hugeView;
		}
	}

	private static class ByteListWrapper extends AbstractPrimitiveListWrapper<Byte> implements List<Byte>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201504020400L;

		ByteList list;
		
		ByteListWrapper(ByteList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Byte _get(long index) {
			return list.get(index);
		}

		Byte _set(long index, Byte element) {
			return list.set(index, element);
		}

		void _add(Byte e) {
			list.add(e);
		}
		
		void _add(long index, Byte element) {
			list.add(index, element);
		}

		Byte _remove(long index) {
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

		CharList list;
		
		CharListWrapper(CharList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Character _get(long index) {
			return list.get(index);
		}

		Character _set(long index, Character element) {
			return list.set(index, element);
		}

		void _add(Character e) {
			list.add(e);
		}

		void _add(long index, Character element) {
			list.add(index, element);
		}

		Character _remove(long index) {
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

		DoubleList list;
		
		DoubleListWrapper(DoubleList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Double _get(long index) {
			return list.get(index);
		}

		Double _set(long index, Double element) {
			return list.set(index, element);
		}

		void _add(Double e) {
			list.add(e);
		}

		void _add(long index, Double element) {
			list.add(index, element);
		}

		Double _remove(long index) {
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

		FloatList list;
		
		FloatListWrapper(FloatList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Float _get(long index) {
			return list.get(index);
		}

		Float _set(long index, Float element) {
			return list.set(index, element);
		}

		void _add(Float e) {
			list.add(e);
		}

		void _add(long index, Float element) {
			list.add(index, element);
		}

		Float _remove(long index) {
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

		IntList list;
		
		IntListWrapper(IntList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Integer _get(long index) {
			return list.get(index);
		}

		Integer _set(long index, Integer element) {
			return list.set(index, element);
		}

		void _add(Integer e) {
			list.add(e);
		}

		void _add(long index, Integer element) {
			list.add(index, element);
		}

		Integer _remove(long index) {
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

		LongList list;
		
		LongListWrapper(LongList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Long _get(long index) {
			return list.get(index);
		}

		Long _set(long index, Long element) {
			return list.set(index, element);
		}

		void _add(Long e) {
			list.add(e);
		}

		void _add(long index, Long element) {
			list.add(index, element);
		}

		Long _remove(long index) {
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

		ShortList list;
		
		ShortListWrapper(ShortList list) {
			this.list = list;
		}
		
		long _size() {
			return list.size();
		}

		Short _get(long index) {
			return list.get(index);
		}

		Short _set(long index, Short element) {
			return list.set(index, element);
		}

		void _add(Short e) {
			list.add(e);
		}

		void _add(long index, Short element) {
			list.add(index, element);
		}

		Short _remove(long index) {
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

	private Wrapperz() {
		throw new AssertionError("Instantiation of utility class " + getClass().getName() + " is prohibited");
	}
}
