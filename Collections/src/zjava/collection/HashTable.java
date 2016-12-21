package zjava.collection;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Early draft of HashTable.
 * 
 * <p> Doesn't permit <tt>null</tt> value.
 * 
 * @param <E> - the type of entries in this hash table
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
class HashTable<E> implements Iterable<E>, Cloneable, java.io.Serializable {
	
	private static final long serialVersionUID = 201612081900L;

	private static class Entry<E> implements Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 201612081900L;
		
		final int hash;
		final E key;
		Entry<E> next;
		
		Entry(int hash, E key, Entry<E> next) {
			this.hash = hash;
			this.key = key;
			this.next = next;
		}

		public Entry<E> clone() {
			try {
				@SuppressWarnings("unchecked")
				Entry<E> clone = (Entry<E>) super.clone();
				if (next != null)
					clone.next = next.clone();
				return clone();
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	private static class Block<E> implements Iterable<E>, Cloneable, java.io.Serializable {

		private static final long serialVersionUID = 201612081900L;

		static final int ADDRESS_BITS = 6;
		static final int SIZE = 1 << ADDRESS_BITS;

		private static final int COLLISIONS_ADDRESS_BITS = 4;
		private static final int COLLISIONS_SIZE = 1 << COLLISIONS_ADDRESS_BITS;
		private static final int ASSOCIATIVITY = 7;

		// - essential variables
		E[] values;
		int[] hashes;
		Entry<E>[] collisions;

		// - service variables
		byte rank;

		public Block() {
		}

		@SuppressWarnings("unchecked")
		private void lazyInitValues() {
			if (values == null) {
				hashes = new int[SIZE + ASSOCIATIVITY];
				values = (E[]) new Object[SIZE + ASSOCIATIVITY];
			}
		}

		@SuppressWarnings("unchecked")
		private void lazyInitCollisions() {
			if (collisions == null) {
				collisions = new Entry[COLLISIONS_SIZE];
			}
		}

		public boolean add(int hash, E key) {
			if (contains(hash, key))
				return false;
			// - look for free space in values
			lazyInitValues();
			int idx = hash & (SIZE - 1);
			for (int i = 0; i < ASSOCIATIVITY; i++) {
				if (values[idx+i] == null) {
					hashes[idx+i] = hash;
					values[idx+i] = key;
					return true;
				}
			}
			// - add to collisions if no space left in "values"
			lazyInitCollisions();
			idx = hash & (COLLISIONS_SIZE - 1);
			collisions[idx] = new Entry<E>(hash, key, collisions[idx]);
			return true;
		}

		public boolean contains(int hash, E key) {
			// - check values
			if (values == null)
				return false;
			int idx = hash & (SIZE - 1);
			for (int i = 0; i < ASSOCIATIVITY; i++)
				if (hashes[idx+i] == hash && key.equals(values[idx+i]))
					return true;

			// - check collisions
			if (collisions == null)
				return false;
			Entry<E> entry = collisions[hash & (COLLISIONS_SIZE - 1)];
			while (entry != null) {
				if (entry.hash == hash && entry.key.equals(key))
					return true;
				entry = entry.next;
			}
			return false;
		}

		public boolean remove(int hash, E key) {
			// - check values
			if (values == null)
				return false;
			int idx = hash & (SIZE - 1);
			for (int i = 0; i < ASSOCIATIVITY; i++)
				if (hashes[idx+i] == hash && key.equals(values[idx+i])) {
					hashes[idx+i] = 0;
					values[idx+i] = null;
					return true;
				}

			// - check collisions
			if (collisions == null)
				return false;
			idx = hash & (COLLISIONS_SIZE - 1);
			Entry<E> prev = null;
			Entry<E> entry = collisions[idx];
			while (entry != null) {
				if (entry.hash == hash && entry.key.equals(key)) {
					if (prev == null)
						collisions[idx] = entry.next;
					else
						prev.next = entry.next;
					return true;
				}
				prev = entry;
				entry = entry.next;
			}
			return false;
		}

		Block<E> extract(int requestedRank, int hash) {
			if (requestedRank == rank)
				return this;
			// - FIXME implementation is missing
			return null;
		}

		public Iterator<E> iterator() {
			
			return new Iterator<E>() {

				private byte lastIndex = -1;
				private byte nextIndex = 0;
				private byte lastCollisionIndex = -1;
				private byte nextCollisionIndex = 0;
				private Entry<E> lastCollision = null;
	
				public boolean hasNext() {
					try {
						// - iteration over values
						if (values == null)
							return false;
						while (nextIndex < values.length && values[nextIndex] == null)
							nextIndex++;
						if (nextIndex < values.length)
							return true;

						// - iteration over collisions
						if (collisions == null)
							return false;
						if (lastCollision != null && lastCollision.next != null)
							return true;

						while (nextCollisionIndex < collisions.length && collisions[nextCollisionIndex] == null)
							nextCollisionIndex++;
						return (nextCollisionIndex < collisions.length);						
					}
					catch (NullPointerException e) {
						throw new ConcurrentModificationException();
					}
					catch (IndexOutOfBoundsException e) {
						throw new ConcurrentModificationException();						
					}
				}

				public E next() {
					if (!hasNext())
						throw new NoSuchElementException();
					try {
						// - iteration over values
						lastIndex = nextIndex;
						if (lastIndex < values.length) {
							return values[lastIndex];
						}
						// - iteration over collisions
						if (lastCollision == null || lastCollision.next == null) {
							lastCollisionIndex = nextCollisionIndex++;
							lastCollision = collisions[lastCollisionIndex];
						}
						else {
							lastCollision = lastCollision.next;
							if (lastCollisionIndex < 0)
								lastCollisionIndex = (byte) ~lastCollisionIndex;
						}
						return lastCollision.key;
					}
					catch (NullPointerException e) {
						throw new ConcurrentModificationException();
					}
					catch (IndexOutOfBoundsException e) {
						throw new ConcurrentModificationException();						
					}
				}

				public void remove() {
					try {
						// - iteration over values
						if (lastIndex < 0)
							throw new IllegalStateException();
						if (lastIndex < values.length) {
							values[lastIndex] = null;
							hashes[lastIndex] = 0;
							lastIndex = -1;
						}
						// - iteration over collisions
						if (lastCollisionIndex < 0)
							throw new IllegalStateException();
						Entry<E> collision = collisions[lastCollisionIndex];
						if (collision == lastCollision) {
							collisions[lastCollisionIndex] = collision.next;
							lastCollisionIndex = (byte) ~lastCollisionIndex;
							return;
						}
						while (collision.next != lastCollision) {
							collision = collision.next;
						}
						collision.next = lastCollision.next;
						lastCollisionIndex = (byte) ~lastCollisionIndex;
					}
					catch (NullPointerException e) {
						throw new ConcurrentModificationException();
					}
					catch (IndexOutOfBoundsException e) {
						throw new ConcurrentModificationException();						
					}
				}
			};
		}
		
		@SuppressWarnings("unchecked")
		public Block<E> clone() {
			try {
				Block<E> clone = (Block<E>) super.clone();
				clone.values = values.clone();
				clone.hashes = hashes.clone();
				if (collisions != null) {
					clone.collisions = new Entry[collisions.length];
					for (int i = 0; i < collisions.length; i++) {
						if (collisions[i] != null) {
							clone.collisions[i] = collisions[i].clone();
						}
					}
				}
				return clone();
			}
	    	catch (CloneNotSupportedException e) {
	    		// - should never be thrown since we are Cloneable
	    		throw new InternalError();
			}
		}
	}

	// - essential variables
	private Block<E>[] data;
	private long size;
	
	@SuppressWarnings("unchecked")
	private void init() {
		data = (Block<E>[]) new Block[1];
		data[0] = new Block<E>();
		size = 0;
	}
	
	/**
	 * Creates {@code HashTable}.
	 */
	public HashTable() {
		init();
	}

	public long size() {
		return size;
	}

	public boolean add(int hash, E entry) {
		if (data[(hash >>> Block.ADDRESS_BITS) & (data.length - 1)].add(hash, entry)) {
			size++;
			return true;
		}
		return false;
	}
	
	public boolean contains(int hash, E entry) {
		return data[(hash >>> Block.ADDRESS_BITS) & (data.length - 1)].contains(hash, entry);
	}
	
	public boolean remove(int hash, E entry) {
		if (data[(hash >>> Block.ADDRESS_BITS) & (data.length - 1)].remove(hash, entry)) {
			size--;
			return true;
		}
		return false;
	}

	public void clear() {
		init();
	}

	private int rank() {
		return Integer.bitCount(data.length - 1);
	}

	/**
	 * Doubles size of this {@code HashTable}
	 */
	public void doubleTableSize() {
		final int oldLength = data.length;
		data = Arrays.copyOf(data, data.length);
		System.arraycopy(data, 0, data, oldLength, oldLength);
	}

	public Iterator<E> iterator() {

		return new Iterator<E>() {

			/** index of current data block */
			private int i = 0;

			/** current block iterator */
			Iterator<E> iter;
			
			/** previously used iterator to handle <tt>remove()</tt> calls */
			Iterator<E> prev;
			
			/** used to track already visited blocks */
			Set<Block<E>> visitedBlocks = new HashSet<Block<E>>();
			
			public boolean hasNext() {
				while ((iter == null || !iter.hasNext()) && i < data.length) {
					if (visitedBlocks.add(data[i])) {
						iter = data[i].iterator();
					}
					i++;
				}
				return (iter != null && iter.hasNext());
			}

			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				E next = iter.next();
				prev = iter;
				return next;
			}

			public void remove() {
				if (prev == null)
					throw new IllegalStateException();
				prev.remove();
				prev = null;
			}
		};
	}
	
    /**
     * Returns a shallow copy of this <tt>HashTable</tt> instance.
     * (The elements themselves are not cloned).
     *
     * @return a clone of this <tt>HashTable</tt> instance
     */
    @SuppressWarnings("unchecked")
	public HashTable<E> clone() {
    	try {
    		HashTable<E> clone = (HashTable<E>) super.clone();
    		for (int i = 0; i < data.length; i++) {
    			clone.data[i] = data[i].clone();
    		}
    		return clone;
		}
    	catch (CloneNotSupportedException e) {
    		// - should never be thrown since we are Cloneable
    		throw new InternalError();
		}
    }
}