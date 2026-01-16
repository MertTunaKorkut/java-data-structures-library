package org.uma.ed.datastructures.bag;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of the {@link SortedBag} interface using a sorted, singly-linked list.
 * <p>
 * This implementation stores unique elements in sorted nodes, with each node maintaining a
 * count of the element's occurrences. This design allows for duplicates while keeping the
 * underlying structure sorted.
 * <p>
 * Performance characteristics are typical for a linked list: operations that require
 * finding an element ({@code insert}, {@code delete}, {@code occurrences}) have a linear
 * time complexity O(n). However, accessing the minimum and maximum elements is O(1)
 * due to the maintenance of `first` and `last` pointers.
 *
 * @param <T> The type of elements held in this sorted bag.
 *
 * @author Pablo López, Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class SortedLinkedBag<T> extends AbstractSortedBag<T> implements SortedBag<T> {

  /**
   * Internal class representing a node in the sorted linked structure.
   * Each node stores a unique element, its number of occurrences, and a link to the next node.
   */
  private static final class Node<E> {
    E element;
    int occurrences;
    Node<E> next;

    Node(E element, int occurrences, Node<E> next) {
      this.element = element;
      this.occurrences = occurrences;
      this.next = next;
    }
  }

  /*
   * INVARIANT:
   *  - The linked structure starting at `first` maintains nodes in ascending order of their `element`.
   *  - Each node contains a unique element; no two nodes store the same element.
   *  - Each node's `occurrences` count is always greater than zero.
   *  - `first` points to the first node (smallest element) or is null if the bag is empty.
   *  - `last` points to the last node (largest element) or is null if the bag is empty.
   *  - `size` stores the total number of elements in the bag (the sum of all occurrences).
   */

  private final Comparator<T> comparator;
  private Node<T> first;
  private Node<T> last;
  private int size;

  /**
   * Constructs an empty {@code SortedLinkedBag} with a specified comparator.
   * <p> Time complexity: O(1)
   */
  public SortedLinkedBag(Comparator<T> comparator) {
    this.comparator = comparator;
    this.first = null;
    this.last = null;
    this.size = 0;
  }

  /**
   * Creates an empty {@code SortedLinkedBag} with a specified comparator.
   * <p> Time complexity: O(1)
   */
  public static <T> SortedLinkedBag<T> empty(Comparator<T> comparator) {
    return new SortedLinkedBag<>(comparator);
  }

  /**
   * Creates an empty {@code SortedLinkedBag} with natural ordering.
   * <p> Time complexity: O(1)
   */
  public static <T extends Comparable<? super T>> SortedLinkedBag<T> empty() {
    return new SortedLinkedBag<T>(Comparator.naturalOrder());
  }

  /**
   * Creates a new {@code SortedLinkedBag} from the given elements.
   * <p> Time complexity: O(n^2) in the worst case, due to repeated linear-time insertions.
   */
  @SafeVarargs
  public static <T> SortedLinkedBag<T> of(Comparator<T> comparator, T... elements) {
    SortedLinkedBag<T> bag = new SortedLinkedBag<>(comparator);
    bag.insert(elements);
    return bag;
  }

  /**
   * Creates a new {@code SortedLinkedBag} from the given elements with natural ordering.
   * <p> Time complexity: O(n^2)
   */
  @SafeVarargs
  public static <T extends Comparable<? super T>> SortedLinkedBag<T> of(T... elements) {
    return of(Comparator.naturalOrder(), elements);
  }

  /**
   * Creates a new {@code SortedLinkedBag} from an iterable.
   * <p> Time complexity: O(n^2)
   */
  public static <T> SortedLinkedBag<T> from(Comparator<T> comparator, Iterable<T> iterable) {
    SortedLinkedBag<T> bag = new SortedLinkedBag<>(comparator);
    for (T element : iterable) {
      bag.insert(element);
    }
    return bag;
  }

  /**
   * Creates a new {@code SortedLinkedBag} from an iterable with natural ordering.
   * <p> Time complexity: O(n^2)
   */
  public static <T extends Comparable<? super T>> SortedLinkedBag<T> from(Iterable<T> iterable) {
    return from(Comparator.naturalOrder(), iterable);
  }

  /**
   * Creates a new {@code SortedLinkedBag} containing the same elements as the given sorted bag.
   * <p>
   * This is an efficient O(n) operation that leverages the sorted nature of the source bag.
   * <p> Time complexity: O(n)
   */
  public static <T> SortedLinkedBag<T> copyOf(SortedBag<T> that) {
    SortedLinkedBag<T> copy = new SortedLinkedBag<>(that.comparator());
    // Since `that` is sorted, we can use the efficient append operation.
    for (T element : that) {
      copy.append(element);
    }
    return copy;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public Comparator<T> comparator() {
    return this.comparator;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public boolean isEmpty() {
      return size == 0;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public int size() {
      return size;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public void clear() {
      first = null;
      last = null;
      size = 0;
  }

  /**
   * A helper class that encapsulates the logic for finding an element (or its
   * insertion point) within the sorted linked list in a single traversal.
   * <p>
   * Upon instantiation with an element, it traverses the list and sets its public
   * fields (`found`, `previous`, `current`) to reflect the result of the search.
   * This avoids redundant list traversals in methods like {@code insert}, {@code delete},
   * and {@code occurrences}.
   * <p>
   * The state of the Finder's fields after a search for {@code element} is as follows:
   * <ul>
   *     <li><b>If the element is found:</b>
   *         <ul>
   *             <li>{@code found} is {@code true}.</li>
   *             <li>{@code current} points to the node containing the element.</li>
   *             <li>{@code previous} points to the node immediately before {@code current},
   *                 or is {@code null} if {@code current} is the first node in the list.</li>
   *         </ul>
   *     </li>
   *     <li><b>If the element is not found:</b>
   *         <ul>
   *             <li>{@code found} is {@code false}.</li>
   *             <li>{@code current} points to the node that would immediately follow the
   *                 new element's position if it were to be inserted. This will be the
   *                 first node in the list with an element greater than the one searched for.
   *                 It can be {@code null} if the insertion point is at the end of the list.</li>
   *             <li>{@code previous} points to the node that would precede the new element's
   *                 position. It can be {@code null} if the insertion point is at the
   *                 beginning of the list.</li>
   *         </ul>
   *     </li>
   * </ul>
   */
  private final class Finder {
    boolean found;
    Node<T> previous, current;

    Finder(T element) {
      previous = null;
      current = first;

      int cmp = 0;
      while (current != null && (cmp = comparator.compare(element, current.element)) > 0) {
        previous = current;
        current = current.next;
      }
      found = (current != null && cmp == 0);
    }
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(n)
   */
  @Override
  public void insert(T element) {
      Finder f = new Finder(element);

      if(f.found){
          f.current.occurrences++;
      }
      else{
          Node<T> node = new Node<>(element,1, f.current);

          if(isEmpty()){
              first = node;
              last = node;
          }
          else if(f.previous == null){
              first = node;
          }
          else if(f.current == null){
              last.next = node;
              last = node;
          }
          else{
              f.previous.next = node;
          }
      }
      size++;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(n)
   */
  @Override
  public void delete(T element) {
      Finder f  = new Finder(element);

      if(f.found){

          if(f.previous == null ){
              first.occurrences--;

              if(first.occurrences == 0) {
                  first = f.current.next;
              }
          }
          else if(f.current.next == null ){
              last.occurrences--;

              if(last.occurrences == 0){
                  f.previous.next = null;
                  last = f.previous;
              }
          }
          else {
              f.current.occurrences--;

              if(f.current.occurrences == 0){
                  f.previous.next = f.current.next;
              }
          }
          size--;
      }
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(n)
   */
  @Override
  public int occurrences(T element) {
      Finder f = new Finder(element);

      if(!f.found){
          return 0;
      }
      else{
          return f.current.occurrences;
      }
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public T minimum() {
      if(isEmpty()){
          throw new NoSuchElementException("minimum on empty bag");
      }

      return first.element;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public T maximum() {
      if(isEmpty()){
          throw new NoSuchElementException("maximum on empty bag");
      }

      return last.element;
  }

  @Override
  public Iterator<T> iterator() {
    return new BagIterator();
  }

  /**
   * An iterator for this bag that yields each element as many times as its occurrence count.
   */
  private final class BagIterator implements Iterator<T> {
    private Node<T> currentNode;
    private int occurrencesLeft;

    BagIterator() {
      this.currentNode = first;
      if (this.currentNode != null) {
        this.occurrencesLeft = this.currentNode.occurrences;
      }
    }

    @Override
    public boolean hasNext() {
      return currentNode != null;
    }

    @Override
    public T next() {
        if(!hasNext()){
            throw new NoSuchElementException("Iterator has no more elements");
        }

        T e = this.currentNode.element;
        this.occurrencesLeft--;

        if(this.occurrencesLeft == 0){
            this.currentNode = this.currentNode.next;

            if(this.currentNode != null){
                this.occurrencesLeft = this.currentNode.occurrences;
            }
        }

        return e;
    }
  }

  /**
   * Appends an element to the end of the bag.
   * PRECONDITION: The element must be >= the last element in the bag.
   */
  private void append(T element) {
    assert first == null || comparator.compare(element, last.element) >= 0;

    if (isEmpty()) {
      first = new Node<>(element, 1, null);
      last = first;
    } else if (comparator.compare(element, last.element) == 0) {
      last.occurrences++;
    } else { // element > last.element
      Node<T> newNode = new Node<>(element, 1, null);
      last.next = newNode;
      last = newNode;
    }
    size++;
  }

  // --- Set-like Operations ---

  /**
   * Modifies this bag to be the union with the given bag. Inefficient for general bags.
   */
  public void union(Bag<T> bag) {
    for (T x : bag) {
      this.insert(x);
    }
  }

  /**
   * Efficiently modifies this bag to be the union with another {@code SortedLinkedBag}.
   */
  public void union(SortedLinkedBag<T> that) {
      if(this.isEmpty()){
          for(T e : that){
              this.append(e);
          }
      }
      else{
          Node<T> node1Prev = null;
          Node<T> node1Cur = this.first;
          Node<T> node2 = that.first;

          while(node2 != null){

              if(node1Cur == null){
                  this.append(node2.element);
                  node2 = node2.next;
              }

              else if(this.comparator.compare(node1Cur.element, node2.element) < 0){
                  node1Prev = node1Cur;
                  node1Cur = node1Cur.next;
              }

              else if(this.comparator.compare(node1Cur.element, node2.element) > 0){

                  if(node1Prev == null){
                      this.first = new Node<>(node2.element, node2.occurrences, node1Cur);
                      node1Prev = this.first;
                  }
                  else{
                      node1Prev.next = new Node<>(node2.element, node2.occurrences, node1Cur);
                      node1Prev = node1Prev.next;
                  }

                  size+= node2.occurrences;
                  node2 = node2.next;
              }

              else{
                  node1Cur.occurrences += node2.occurrences;
                  size+= node2.occurrences;

                  node1Prev = node1Cur;
                  node1Cur = node1Cur.next;
                  node2 = node2.next;
              }
          }
      }
  }

  /**
   * Modifies this bag to be the intersection with the given bag. Inefficient.
   */
  public void intersection(Bag<T> bag) {
      for(T e : this){
          if(!bag.contains(e)){
              this.delete(e);
          }
      }
  }

  /**
   * Efficiently modifies this bag to be the intersection with another {@code SortedLinkedBag}.
   */
  public void intersection(SortedLinkedBag<T> that) {
      if(that.isEmpty()){
          this.clear();
      }
      else{
          Node<T> node1Prev = null;
          Node<T> node1Cur = this.first;
          Node<T> node2 = that.first;
          int newSize = 0;

          while(node1Cur != null && node2 != null){

                  int cmp = this.comparator.compare(node1Cur.element, node2.element);

                  if (cmp < 0) {
                      if (node1Cur == this.first) { //ilkini siliyoruz
                          this.first = node1Cur.next;

                      } else { // aradan siliyoruz
                          node1Prev.next = node1Cur.next;
                      }

                      node1Cur = node1Cur.next;

                  } else if (cmp > 0) { //ikinci daha büyük
                      node2 = node2.next;

                  } else { //eşitler

                      int occ = Math.min(node1Cur.occurrences, node2.occurrences);
                      node1Cur.occurrences = occ;
                      newSize += occ;

                      node1Prev = node1Cur;
                      node1Cur = node1Cur.next;

                      node2 = node2.next;
                  }

          }

          if(node2 == null){
              if(node1Prev == null){ //her şeyi silmişiz
                  this.first = null;
              }
              else{
                  node1Prev.next = null;
              }
          }

          this.size = newSize;
          this.last = node1Prev;
      }
  }

  /**
   * Modifies this bag to be the difference with the given bag. Inefficient.
   */
  public void difference(Bag<T> bag) {
      for(T e : this){
          if(bag.contains(e)){
              this.delete(e);
          }
      }
  }

  /**
   * Efficiently modifies this bag to be the difference with another {@code SortedLinkedBag}.
   */
  public void difference(SortedLinkedBag<T> that) {

      if(!this.isEmpty() && !that.isEmpty()){
          Node<T> n1Prev = null;
          Node<T> n1Cur = this.first;
          Node<T> n2 = that.first;
          int sizeDel = 0;

          while(n1Cur != null && n2 != null){
              int cmp = this.comparator.compare(n1Cur.element, n2.element);

              if(cmp<0){
                  n1Prev = n1Cur;
                  n1Cur = n1Cur.next;
              }
              else if(cmp>0){
                  n2 = n2.next;
              }
              else{ //eşitler, occurence karşılaştırma

                  int occDif = n1Cur.occurrences - n2.occurrences;

                  if(occDif > 0){
                     sizeDel += n2.occurrences;
                     n1Cur.occurrences = occDif;

                     n1Prev = n1Cur;
                     n1Cur = n1Cur.next;
                     n2 = n2.next;
                  }
                  else{ //occDif <= 0, n1Cur silincek
                      sizeDel += n1Cur.occurrences;

                      if(n1Prev == null){ //ilkini siliyosak
                          first = n1Cur.next;
                      }
                      else{ //ortadan siliyoruz
                          n1Prev.next = n1Cur.next;
                      }

                      n1Cur = n1Cur.next;
                      n2 = n2.next;
                  }
              }
          }

          if(n1Cur == null){

              if(n1Prev == null){ //her şeyi silmişiz
                  this.first = null;
                  this.last = null;
              }
              else {
                  this.last = n1Prev;
                  this.last.next = null;
              }
          }
          this.size -= sizeDel;
      }
  }
}