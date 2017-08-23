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

	private static final byte COLLISION_UPDATES_THRESHOLD = 12;

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
		private static final int ASSOCIATIVITY = 6;

		// - essential variables
		private E[] keys;
		private int[] hashes;
		private Entry<E>[] collisions;

		// - service variables
		private final byte rank;
		private byte collisionUpdates;

		private static int valuesIndex(final int hash) {
			return hash & (SIZE - 1);
		}

		private static int collisionsIndex(final int hash) {
			return hash & (COLLISIONS_SIZE - 1);
		}

		Block(int rank) {
			if (0 > rank | rank >= 32)
				throw new IllegalArgumentException();
			this.rank = (byte) rank;
		}

		private void incCollisionUpdates() {
			if (collisionUpdates < Byte.MAX_VALUE)
				collisionUpdates++;
		}
		
		byte getCollisionUpdates() {
			return collisionUpdates;
		}
		
		void resetCollisionUpdates() {
			collisionUpdates = 0;
		}
		
		@SuppressWarnings("unchecked")
		private void lazyInitValues() {
			if (keys == null) {
				hashes = new int[SIZE + ASSOCIATIVITY];
				keys = (E[]) new Object[SIZE + ASSOCIATIVITY];
			}
		}

		@SuppressWarnings("unchecked")
		private void lazyInitCollisions() {
			if (collisions == null) {
				collisions = new Entry[COLLISIONS_SIZE];
			}
		}

		boolean contains(final int hash, final E key) {
			// - check values
			if (keys == null)
				return false;
			int idx = valuesIndex(hash);
			for (int i = 0; i < ASSOCIATIVITY; i++)
				if (hashes[idx+i] == hash && key.equals(keys[idx+i]))
					return true;

			// - check collisions
			if (collisions == null)
				return false;
			Entry<E> entry = collisions[collisionsIndex(hash)];
			while (entry != null) {
				if (entry.hash == hash && entry.key.equals(key))
					return true;
				entry = entry.next;
			}
			return false;
		}

		private boolean putToValues(final int hash, final E key) {
			final int idx = valuesIndex(hash);
			for (int i = 0; i < ASSOCIATIVITY; i++)
				if (keys[idx+i] == null) {
					hashes[idx+i] = hash;
					keys[idx+i] = key;
					return true;
				}
			return false;
		}

		boolean add(final int hash, final E key) {
			if (contains(hash, key))
				return false;
			// - look for free space in values
			lazyInitValues();
			if (putToValues(hash, key))
				return true;
			
			// - add to collisions if no space left in "values"
			incCollisionUpdates();
			lazyInitCollisions();
			final int idx = collisionsIndex(hash);
			collisions[idx] = new Entry<E>(hash, key, collisions[idx]);
			return true;
		}

		boolean remove(final int hash, final E key) {
			// - check values
			if (keys == null)
				return false;
			int idx = valuesIndex(hash);
			for (int i = 0; i < ASSOCIATIVITY; i++)
				if (hashes[idx+i] == hash && key.equals(keys[idx+i])) {
					hashes[idx+i] = 0;
					keys[idx+i] = null;
					return true;
				}

			// - check collisions
			if (collisions == null)
				return false;
			idx = collisionsIndex(hash);
			Entry<E> prev = null;
			Entry<E> entry = collisions[idx];
			while (entry != null) {
				if (entry.hash == hash && entry.key.equals(key)) {
					incCollisionUpdates();
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

		// - returns bits of the given hash related to given rank
		private static int rankedHash(final int hash, final int rank) {
			return (hash >>> ADDRESS_BITS) & ((1 << rank) - 1);
		}

		// - extracts data related to requested hash-rank pair to new block and return it
		// - if requested rank is equal to this block rank return this block instead
		Block<E> extract(final int requestedRank, int hash) {
			if (requestedRank <= rank)
				return this;
			Block<E> extracted = new Block<E>(requestedRank);
			hash = rankedHash(hash, requestedRank);
			// - extract values
			if (keys == null)
				return extracted;
			for (int i = 0; i < keys.length; i++) {
				if (rankedHash(hashes[i], requestedRank) == hash) {
					extracted.lazyInitValues();
					extracted.hashes[i] = hashes[i];
					extracted.keys[i] = keys[i];
					hashes[i] = 0;
					keys[i] = null;
				}
			}
			// - extract collisions and put them in values, if possible
			if (collisions == null)
				return extracted;
			for (int i = 0; i < collisions.length; i++)
				if (collisions[i] != null) {
					Entry<E> prev = null;
					Entry<E> entry = collisions[i];
					while (entry != null) {
						// - try to put to values
						if (((rankedHash(entry.hash, requestedRank) == hash) ? extracted : this).putToValues(entry.hash, entry.key)) {
							entry = (prev == null) ? (collisions[i] = entry.next) : (prev.next = entry.next);
						}
						// - if not possible - move to collisions in extracted block / leave there
						else {
							if (rankedHash(entry.hash, requestedRank) == hash) {
								final Entry<E> next = (prev == null) ? (collisions[i] = entry.next) : (prev.next = entry.next);
								extracted.lazyInitCollisions();
								entry.next = extracted.collisions[i];
								extracted.collisions[i] = entry;
								entry = next;
							}
							else {
								prev = entry;
								entry = entry.next;
							}
						}
					}
				}
			return extracted;
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
						if (keys == null)
							return false;
						while (nextIndex < keys.length && keys[nextIndex] == null)
							nextIndex++;
						if (nextIndex < keys.length)
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
						if (lastIndex < keys.length) {
							return keys[lastIndex];
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
						if (lastIndex < keys.length) {
							keys[lastIndex] = null;
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
				clone.keys = keys.clone();
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

	private Block<E>[] data;
	private long size;
	
	@SuppressWarnings("unchecked")
	private void init() {
		data = (Block<E>[]) new Block[1];
		data[0] = new Block<E>(0);
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

	private int blockIndex(final int hash) {
		return (hash >>> Block.ADDRESS_BITS) & (data.length - 1);
	}
	
	private void optimize(final int blockIndex, final int hash) {
		if (data[blockIndex].getCollisionUpdates() >= COLLISION_UPDATES_THRESHOLD) {
			data[blockIndex] = data[blockIndex].extract(rank(), hash);
			data[blockIndex].resetCollisionUpdates();
		}
	}
	
	public boolean add(int hash, E entry) {
		final int blockIndex = blockIndex(hash);
		optimize(blockIndex, hash);
		if (data[blockIndex].add(hash, entry)) {
			size++;
			return true;
		}
		return false;
	}
	
	public boolean contains(int hash, E entry) {
		return data[blockIndex(hash)].contains(hash, entry);
	}
	
	public boolean remove(int hash, E entry) {
		final int blockIndex = blockIndex(hash);
		optimize(blockIndex, hash);
		if (data[blockIndex].remove(hash, entry)) {
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
		if (rank() >= 30)
			return;

		final int oldLength = data.length;
		data = Arrays.copyOf(data, 2*oldLength);
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