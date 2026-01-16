package org.uma.ed.datastructures.bag;

import org.uma.ed.datastructures.set.SortedArraySet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of the {@link SortedBag} interface using a sorted dynamic array of bins.
 * <p>
 * Each "bin" stores a unique element and a count of its occurrences. The array of bins
 * is kept sorted according to the element's natural order or a provided comparator.
 * <p>
 * This structure provides logarithmic time complexity O(log n) for search operations
 * like {@code occurrences} and {@code contains}. However, {@code insert} and {@code delete}
 * operations require shifting elements in the array, resulting in linear time complexity O(n).
 * Accessing the minimum and maximum elements is an efficient O(1) operation.
 *
 * @param <T> The type of elements held in this sorted bag.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class SortedArrayBag<T> extends AbstractSortedBag<T> implements SortedBag<T> {

  /**
   * Default initial capacity for the underlying array.
   */
  private static final int DEFAULT_INITIAL_CAPACITY = 16;

  /**
   * An internal record to store a unique element and its number of occurrences.
   */
  private record Bin<E>(E element, int occurrences) {

    static <E> Bin<E> of(E element, int occurrences) {
      return new Bin<>(element, occurrences);
    }

    /**
     * A factory for creating a "search key" bin. The occurrence count is irrelevant for searching.
     */
    static <E> Bin<E> withElement(E element) {
      return new Bin<>(element, 0); // Occurrences can be a dummy value.
    }

    /**
     * Returns a comparator that compares {@code Bin} objects based on their element component.
     */
    static <E> Comparator<Bin<E>> byElement(Comparator<E> elementComparator) {
      return (b1, b2) -> elementComparator.compare(b1.element, b2.element);
    }
  }

  private final Comparator<T> comparator;
  private Bin<T>[] elements;
  private int bins; // Number of bins in use
  private int size; // Total number of elements including duplicates

  /**
   * Constructs an empty {@code SortedArrayBag} with a specified comparator and initial capacity.
   * <p>Time complexity: O(1)</p>
   */
  @SuppressWarnings("unchecked")
  public SortedArrayBag(Comparator<T> comparator, int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0");
    }
    this.comparator = comparator;
    this.elements = (Bin<T>[]) new Bin[initialCapacity];
    this.bins = 0;
    this.size = 0;
  }

  /**
   * Constructs an empty {@code SortedArrayBag} with a specified comparator.
   * <p>Time complexity: O(1)</p>
   */
  public SortedArrayBag(Comparator<T> comparator) {
    this(comparator, DEFAULT_INITIAL_CAPACITY);
  }

  /**
   * Creates an empty {@code SortedArrayBag} with natural ordering.
   * <p>Time complexity: O(1)</p>
   */
  public static <T extends Comparable<? super T>> SortedArrayBag<T> empty() {
    return new SortedArrayBag<T>(Comparator.naturalOrder());
  }

  /**
   * Creates an empty {@code SortedArrayBag} with a specified comparator.
   * <p>Time complexity: O(1)</p>
   */
  public static <T> SortedArrayBag<T> empty(Comparator<T> comparator) {
    return new SortedArrayBag<>(comparator);
  }

  /**
   * Creates a new {@code SortedArrayBag} from the given elements.
   * <p>Time complexity: O(m^2) in the worst case due to repeated linear-time insertions.</p>
   */
  @SafeVarargs
  public static <T> SortedArrayBag<T> of(Comparator<T> comparator, T... elements) {
    SortedArrayBag<T> bag = new SortedArrayBag<>(comparator);
    bag.insert(elements);
    return bag;
  }

  /**
   * Creates a new {@code SortedArrayBag} from the given elements with natural ordering.
   * <p>Time complexity: O(m^2) in the worst case.</p>
   */
  @SafeVarargs
  public static <T extends Comparable<? super T>> SortedArrayBag<T> of(T... elements) {
    return of(Comparator.naturalOrder(), elements);
  }

  /**
   * Creates a new {@code SortedArrayBag} from an iterable.
   * <p>Time complexity: O(m^2) in the worst case.</p>
   */
  public static <T> SortedArrayBag<T> from(Comparator<T> comparator, Iterable<T> iterable) {
    SortedArrayBag<T> bag = new SortedArrayBag<>(comparator);
    for (T element : iterable) {
      bag.insert(element);
    }
    return bag;
  }

  /**
   * Creates a new {@code SortedArrayBag} from an iterable with natural ordering.
   * <p>Time complexity: O(m^2) in the worst case.</p>
   */
  public static <T extends Comparable<? super T>> SortedArrayBag<T> from(Iterable<T> iterable) {
    return from(Comparator.naturalOrder(), iterable);
  }

  /**
   * Creates a new {@code SortedArrayBag} from any {@code SortedBag}.
   * This is an efficient O(m) operation that leverages the sorted nature of the source.
   * <p>Time complexity: O(m), where m is the total number of elements in the source bag.</p>
   */
    /**
     * Creates a new {@code SortedArrayBag} from any {@code SortedBag}.
     * This is an efficient O(m) operation that leverages the sorted nature of the source.
     * <p>Time complexity: O(m), where m is the total number of elements in the source bag.</p>
     */
    public static <T> SortedArrayBag<T> copyOf(SortedBag<T> that) {
        // 1. 'that'in karşılaştırıcısını kullanarak yeni bir 'bag' oluştur.
        SortedArrayBag<T> newBag = new SortedArrayBag<>(that.comparator());

        // 2. Kaynak 'bag' boşsa, yeni boş 'bag'i hemen döndür.
        if (that.isEmpty()) {
            return newBag;
        }

        // 3. 'that'in sıralı iteratörünü al.
        Iterator<T> iterator = that.iterator();

        // 4. Döngüyü başlatmak için ilk elemanı al.
        //    (Boş olmadığını bildiğimiz için .next() güvenlidir)
        T currentElement = iterator.next();
        int currentOccurrences = 1;

        // 5. Kalan elemanları dolaş
        while (iterator.hasNext()) {
            T nextElement = iterator.next();

            if (newBag.comparator.compare(nextElement, currentElement) == 0) {
                // Eleman bir öncekiyle aynı, sayacı artır.
                currentOccurrences++;
            } else {
                // Eleman değişti. Bir önceki 'Bin'i (kutuyu) kaydet.
                newBag.ensureCapacity(); // 'elements' dizisinde yer aç
                // 'Bin'i (kutuyu) doğrudan diziye ekle
                newBag.elements[newBag.bins] = Bin.of(currentElement, currentOccurrences);
                newBag.bins++; // 'bin' (kutu) sayısını artır

                // Sayacı yeni eleman için sıfırla
                currentElement = nextElement;
                currentOccurrences = 1;
            }
        }

        // 6. DÖNGÜ SONRASI: Son 'Bin'i (kutuyu) ekle.
        // Döngü bittiğinde, 'currentElement' ve 'currentOccurrences' içinde
        // bekleyen son eleman grubu vardır. Onu da eklemeliyiz.
        newBag.ensureCapacity();
        newBag.elements[newBag.bins] = Bin.of(currentElement, currentOccurrences);
        newBag.bins++;

        // 7. Toplam boyutu (size) doğrudan kaynaktan ayarla.
        newBag.size = that.size();

        return newBag;
    }

  @Override
  public Comparator<T> comparator() {
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
   * A helper class that uses binary search to find a bin's position.
   */
  private class Finder {
    final int index;
    final boolean found;

    Finder(T element) {
      // Arrays.binarySearch returns the index if found, or -(insertion point) - 1 if not found
      int index = Arrays.binarySearch(elements, 0, bins,
          Bin.withElement(element),
          Bin.byElement(comparator)
      );
      this.found = index >= 0;
      this.index = this.found ? index : -(index + 1);
    }
  }

  private void ensureCapacity() {
    if (bins == elements.length) {
      elements = Arrays.copyOf(elements, elements.length * 2);
    }
  }

  @Override
  public int occurrences(T element) {
    Finder f = new Finder(element);

    if(!f.found){
        return 0;
    }
    else {
        return elements[f.index].occurrences;
    }
  }

  @Override
  public void insert(T element) {
    Finder f = new Finder(element);

    if(f.found){
        elements[f.index] = Bin.of(element, elements[f.index].occurrences+1 );
    }
    else{
        ensureCapacity();

        for(int i = size - 1; i >= f.index; i--){
            elements[i+1] = elements[i];
        }
        elements[f.index] = Bin.of(element,1);

        bins++;
    }
    size++;
  }

  @Override
  public void delete(T element) {
    Finder f = new Finder(element);

    if(f.found){

        elements[f.index] = Bin.of(element, elements[f.index].occurrences-1 ); //occ azalt

        if(elements[f.index].occurrences == 0) { //occ 0 olduysa bini sil
            for (int i = f.index; i < size; i++) {
                elements[i] = elements[i + 1];
            }
            bins--;
        }
        size--;
    }
  }

  @Override
  public void clear() {

      if(!isEmpty()){
          for(Bin<T> e : elements){
              e = null;
          }
          bins = 0;
          size = 0;
      }
  }

  @Override
  public T minimum() {
      if(isEmpty()){
          throw new NoSuchElementException("minimum on empty bag");
      }
      return elements[0].element;
  }

  @Override
  public T maximum() {
      if(isEmpty()){
          throw new NoSuchElementException("maximum on empty bag");
      }
      return elements[size-1].element;
  }

  @Override
  public Iterator<T> iterator() {
    return new BagIterator();
  }

  private final class BagIterator implements Iterator<T> {
    private int binIndex;
    private int occurrencesLeft;

    BagIterator() {
      this.binIndex = 0;
      if (bins > 0) {
        this.occurrencesLeft = elements[0].occurrences();
      } else {
        this.occurrencesLeft = 0;
      }
    }

    @Override
    public boolean hasNext() {
      return binIndex < bins;
    }

    @Override
    public T next() {
      if(!hasNext()){
          throw new NoSuchElementException("Iterator has no more elements");
      }

      T e = elements[binIndex].element;
      occurrencesLeft--;

      if(occurrencesLeft == 0){
          binIndex++;

          if(binIndex != bins){
              occurrencesLeft = elements[binIndex].occurrences;
          }
      }

      return e;
    }
  }
}