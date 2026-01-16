package org.uma.ed.datastructures.dictionary;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of the {@link SortedDictionary} interface using a sorted dynamic array
 * of key-value entries.
 * <p>
 * The array of entries is kept sorted by key, according to the dictionary's comparator.
 * This ordering allows for logarithmic time complexity O(log n) for search operations like
 * {@code valueOf} and {@code isDefinedAt}.
 * <p>
 * However, {@code insert} and {@code delete} operations require shifting elements in the array
 * to maintain order and contiguity, resulting in a linear time complexity O(n).
 * Accessing the minimum and maximum entries is an efficient O(1) operation.
 *
 * @param <K> The type of keys maintained by this dictionary.
 * @param <V> The type of mapped values.
 */
public class SortedArrayDictionary<K, V> extends AbstractSortedDictionary<K, V> implements SortedDictionary<K, V> {

  /**
   * Default initial capacity for the underlying array.
   */
  private static final int DEFAULT_INITIAL_CAPACITY = 16;

  private final Comparator<K> comparator;
  private Entry<K, V>[] elements;
  private int size;

  /*
   * INVARIANT:
   *  - `elements[0...size-1]` stores the key-value pairs.
   *  - The array is sorted in ascending order of keys according to the `comparator`.
   *  - There are no duplicate keys.
   */

  /**
   * Constructs an empty {@code SortedArrayDictionary} with a specified comparator and initial capacity.
   * <p>Time complexity: O(1)</p>
   */
  @SuppressWarnings("unchecked")
  public SortedArrayDictionary(Comparator<K> comparator, int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0");
    }
    this.comparator = comparator;
    this.elements = (Entry<K, V>[]) new Entry[initialCapacity];
    this.size = 0;
  }

  /**
   * Constructs an empty {@code SortedArrayDictionary} with a specified comparator.
   * <p>Time complexity: O(1)</p>
   */
  public SortedArrayDictionary(Comparator<K> comparator) {
    this(comparator, DEFAULT_INITIAL_CAPACITY);
  }

  /**
   * Creates an empty {@code SortedArrayDictionary} with natural key ordering.
   * <p>Time complexity: O(1)</p>
   */
  public static <K extends Comparable<? super K>, V> SortedArrayDictionary<K, V> empty() {
    return new SortedArrayDictionary<K, V>(Comparator.naturalOrder());
  }

  /**
   * Creates an empty {@code SortedArrayDictionary} with a specified comparator.
   * <p>Time complexity: O(1)</p>
   */
  public static <K, V> SortedArrayDictionary<K, V> empty(Comparator<K> comparator) {
    return new SortedArrayDictionary<>(comparator);
  }

  /**
   * Creates a new {@code SortedArrayDictionary} from the given entries.
   * <p>Time complexity: O(n log n) due to sorting the initial entries.</p>
   */
  @SafeVarargs
  public static <K, V> SortedArrayDictionary<K, V> of(Comparator<K> comparator, Entry<K, V>... entries) {
    // More efficient O(n log n) construction by sorting first.
    Arrays.sort(entries, Entry.onKeyComparator(comparator));

    SortedArrayDictionary<K, V> dict = new SortedArrayDictionary<>(comparator, entries.length > 0 ? entries.length : DEFAULT_INITIAL_CAPACITY);

    if (entries.length > 0) {
      dict.elements[0] = entries[0];
      dict.size = 1;
      for (int i = 1; i < entries.length; i++) {
        // Handle duplicates: only add if key is different from the previous one
        if (comparator.compare(entries[i].key(), entries[i-1].key()) != 0) {
          dict.elements[dict.size++] = entries[i];
        } else {
          // If keys are same, the last one in the sorted array wins.
          dict.elements[dict.size-1] = entries[i];
        }
      }
    }
    return dict;
  }

  /**
   * Creates a new {@code SortedArrayDictionary} from the given entries with natural key ordering.
   * <p>Time complexity: O(n log n).</p>
   */
  @SafeVarargs
  public static <K extends Comparable<? super K>, V> SortedArrayDictionary<K, V> of(Entry<K, V>... entries) {
    return of(Comparator.naturalOrder(), entries);
  }

  /**
   * Creates a new {@code SortedArrayDictionary} from an iterable of entries.
   * <p>Time complexity: O(n log n) due to sorting.</p>
   */
  @SuppressWarnings("unchecked")
  public static <K, V> SortedArrayDictionary<K, V> from(Comparator<K> comparator, Iterable<Entry<K, V>> iterable) {
    java.util.ArrayList<Entry<K,V>> tempList = new java.util.ArrayList<>();
    iterable.forEach(tempList::add);
    return of(comparator, tempList.toArray((Entry<K, V>[]) new Entry[0]));
  }

  /**
   * Creates a new {@code SortedArrayDictionary} from an iterable of entries with natural key ordering.
   * <p>Time complexity: O(n log n).</p>
   */
  public static <K extends Comparable<? super K>, V> SortedArrayDictionary<K, V> from(Iterable<Entry<K, V>> iterable) {
    return from(Comparator.naturalOrder(), iterable);
  }

  /**
   * Creates a new {@code SortedArrayDictionary} that is a copy of the given one.
   * <p>Time complexity: O(n)</p>
   */
  public static <K, V> SortedArrayDictionary<K, V> copyOf(SortedArrayDictionary<K, V> dict) {
    SortedArrayDictionary<K,V> dic = empty(dict.comparator);

    if(dict.isEmpty()){
        return dic;
    }

    for(Entry<K,V> ent: dict){
        dic.ensureCapacity();
        dic.elements[dic.size] = ent;
        dic.size++;
    }
    return dic;
  }

  @Override
  public Comparator<K> comparator() {
    return comparator;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public int size() {
    return size;
  }

  /**
   * A helper class that uses binary search to find an entry's position by its key.
   */
  private class Finder {
    final int index;
    final boolean found;

    Finder(K key) {
      int index = Arrays.binarySearch(elements, 0, size,
          Entry.withKey(key),
          Entry.onKeyComparator(comparator)
      );
      this.found = index >= 0;
      this.index = found ? index : -(index + 1);
    }
  }

  @Override
  public V valueOf(K key) {
    Finder f = new Finder(key);

    if(f.found){
        return elements[f.index].value();
    }
    else{
        return null;
    }
  }

  @Override
  public boolean isDefinedAt(K key) {
    Finder f = new Finder(key);

    return f.found;
  }

  private void ensureCapacity() {
    if (size == elements.length) {
      elements = Arrays.copyOf(elements, size * 2);
    }
  }

  @Override
  public void insert(Entry<K, V> entry) {
    Finder f = new Finder(entry.key());


    if(!f.found){
        ensureCapacity();

        for(int i = size - 1 ; i >= f.index; i--){
            elements[i+1] = elements[i];
        }
        size++;
    }
    elements[f.index] = entry;
  }

  @Override
  public void delete(K key) {
    Finder f = new Finder(key);

    if(f.found){
        for(int i = f.index; i<size; i++){
            elements[i] = elements[i+1];
        }
        size--;
    }
  }

  @Override
  public void clear() {
    if(!isEmpty()){
        for(Entry<K,V> ent: elements){
            ent = null;
        }
    }
  }

  @Override
  public Entry<K, V> minimum() {
    if(isEmpty()){
        throw new NoSuchElementException("minimum on empty dictionary");
    }

    return elements[0];
  }

  @Override
  public Entry<K, V> maximum() {
      if(isEmpty()){
          throw new NoSuchElementException("maximum on empty dictionary");
      }

      return elements[size-1];
  }

  @Override
  public Iterable<K> keys() {
    return () -> new KeyIterator(iterator());
  }

  @Override
  public Iterable<V> values() {
    return () -> new ValueIterator(iterator());
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return this;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new EntryIterator();
  }

  // --- Private Iterator Helper Classes ---

  private final class EntryIterator implements Iterator<Entry<K,V>> {
    private int currentIndex = 0;
    @Override public boolean hasNext() { return currentIndex < size; }
    @Override public Entry<K, V> next() {
      if (!hasNext()) throw new NoSuchElementException();
      return elements[currentIndex++];
    }
  }

  private abstract class BaseIterator<T> implements Iterator<T> {
    protected final Iterator<Entry<K, V>> entryIterator;
    BaseIterator(Iterator<Entry<K, V>> iterator) { this.entryIterator = iterator; }
    @Override public boolean hasNext() { return entryIterator.hasNext(); }
  }

  private final class KeyIterator extends BaseIterator<K> {
    KeyIterator(Iterator<Entry<K, V>> iterator) { super(iterator); }
    @Override public K next() { return entryIterator.next().key(); }
  }

  private final class ValueIterator extends BaseIterator<V> {
    ValueIterator(Iterator<Entry<K, V>> iterator) { super(iterator); }
    @Override public V next() { return entryIterator.next().value(); }
  }
}