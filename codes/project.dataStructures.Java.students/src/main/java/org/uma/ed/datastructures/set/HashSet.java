package org.uma.ed.datastructures.set;

import org.uma.ed.datastructures.hashtable.HashTable;
import org.uma.ed.datastructures.hashtable.LinearProbingHashTable;

import java.util.Iterator;

/**
 * An implementation of the {@link Set} interface using a hash table with linear probing.
 * <p>
 * This class provides near constant-time performance O(1) on average for basic
 * operations like {@code insert}, {@code contains}, and {@code delete}, assuming the
 * hash function disperses elements properly among the buckets.
 * <p>
 * This set does not maintain any specific order for its elements. The iteration order
 * depends on the internal state of the hash table and may appear random.
 * <p>
 * Note: Elements stored in this set must have a proper implementation of
 * {@code equals()} and {@code hashCode()}.
 *
 * @param <T> The type of elements held in this set.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class HashSet<T> extends AbstractSet<T> implements Set<T> {

  /**
   * The underlying hash table that stores the set's elements.
   */
  private final HashTable<T> hashTable;

  /**
   * Private constructor to wrap an existing {@code LinearProbingHashTable}.
   */
  private HashSet(LinearProbingHashTable<T> hashTable) {
    this.hashTable = hashTable;
  }

  /**
   * Constructs an empty {@code HashSet} with a specified initial capacity and load factor.
   * <p> Time complexity: O(capacity) due to array allocation.
   *
   * @param numCells      the initial number of cells (buckets) in the hash table.
   * @param maxLoadFactor the maximum load factor before a rehash is triggered.
   * @throws IllegalArgumentException if numCells is not positive.
   */
  public HashSet(int numCells, double maxLoadFactor) {
    this(new LinearProbingHashTable<>(numCells, maxLoadFactor));
  }

  /**
   * Constructs an empty {@code HashSet} with default capacity and load factor.
   * <p> Time complexity: O(1) (amortized for default capacity).
   */
  public HashSet() {
    this(new LinearProbingHashTable<>());
  }

  /**
   * Creates an empty {@code HashSet}.
   * <p> Time complexity: O(1) (amortized).
   */
  public static <T> HashSet<T> empty() {
    return new HashSet<>();
  }

  /**
   * Creates an empty {@code HashSet} with enough initial capacity to hold the
   * specified number of elements without needing to rehash.
   * <p> Time complexity: O(capacity)
   */
  public static <T> HashSet<T> withCapacity(int capacity) {
    return new HashSet<>(LinearProbingHashTable.withCapacity(capacity));
  }

  /**
   * Creates a new {@code HashSet} from the given elements.
   * <p> Time complexity: Near O(m) on average, where m is the number of elements.
   */
  @SafeVarargs
  public static <T> HashSet<T> of(T... elements) {
    HashSet<T> hashSet = withCapacity(elements.length);
    hashSet.insert(elements);
    return hashSet;
  }

  /**
   * Creates a new {@code HashSet} from an iterable.
   * <p> Time complexity: Near O(m) on average, where m is the number of elements.
   */
  public static <T> HashSet<T> from(Iterable<T> iterable) {
    HashSet<T> hashSet = empty();
    for (T element : iterable) {
      hashSet.insert(element);
    }
    return hashSet;
  }

  /**
   * Creates a new {@code HashSet} that is a copy of the given one.
   * <p> Time complexity: O(n), where n is the capacity of the underlying table.
   */
  public static <T> HashSet<T> copyOf(HashSet<T> that) {
    return new HashSet<>(LinearProbingHashTable.copyOf(that.hashTable));
  }

  /**
   * Creates a new {@code HashSet} from any {@code Set}.
   * <p> Time complexity: Near O(n) on average, where n is the number of elements.
   */
  public static <T> HashSet<T> copyOf(Set<T> that) {
    HashSet<T> hashSet = withCapacity(that.size()*2);

    if(that.isEmpty()){
      return hashSet;
    }

    for(T element: that){
      hashSet.insert(element);
    }

    return hashSet;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public boolean isEmpty() {
    return hashTable.isEmpty();
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public int size() {
    return hashTable.size();
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: Near O(1) on average.
   */
  @Override
  public void insert(T element) {
    hashTable.insert(element);
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: Near O(1) on average.
   */
  @Override
  public boolean contains(T element) {
    return hashTable.contains(element);
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: Near O(1) on average.
   */
  @Override
  public void delete(T element) {
    hashTable.delete(element);
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(n), where n is the capacity of the table.
   */
  @Override
  public void clear() {
    hashTable.clear();
  }

  /**
   * {@inheritDoc}
   * The iterator's order is not specified.
   */
  @Override
  public Iterator<T> iterator() {
    return hashTable.iterator();
  }
}