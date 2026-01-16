package org.uma.ed.datastructures.heap;

import java.util.Comparator;
import org.uma.ed.datastructures.list.JDKArrayList;
import org.uma.ed.datastructures.queue.JDKQueue;
import org.uma.ed.datastructures.queue.Queue;

/**
 * An implementation of the {@link Heap} interface using a maxiphobic heap.
 * <p>
 * A maxiphobic heap is a type of mergeable heap implemented as a binary tree, first
 * described by Chris Okasaki. It maintains the standard min-heap property (a node is
 * smaller than or equal to its children).
 * <p>
 * Its key feature is its elegant and simple merging strategy. When merging two heaps
 * {@code H1} and {@code H2}, the smaller of the two roots becomes the new root.
 * Then, there are three candidate subtrees to become the children of this new root:
 * the two children of the original root, and the entire other heap. The maxiphobic
 * ("biggest-avoiding") strategy dictates that:
 * <ol>
 *     <li>The largest of these three subtrees (by number of nodes) is kept as one child.</li>
 *     <li>The other two (smaller) subtrees are recursively merged to become the other child.</li>
 * </ol>
 * This strategy leads to logarithmic time for {@code insert} and {@code deleteMinimum} operations.
 *
 * @param <T> Type of elements in heap.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class MaxiphobicHeap<T> implements Heap<T> {
  /**
   * Internal class representing a node in the maxiphobic heap tree.
   */
  private static final class Node<E> {
    E element;
    int weight; // The number of nodes in the subtree rooted at this node (size of subtree).
    Node<E> left, right;

    Node(E element, int weight, Node<E> left, Node<E> right) {
      this.element = element;
      this.weight = weight;
      this.left = left;
      this.right = right;
    }

    // Constructor for a singleton node (a leaf).
    Node(E element) {
      this(element, 1, null, null);
    }
  }

  /*
   * INVARIANT:
   * - Min-Heap Property: For any node, its element is less than or equal to the elements
   *   in its left and right children.
   * - The structure is maintained by the maxiphobic merge algorithm.
   */

  /**
   * Comparator used to order elements in heap.
   */
  private final Comparator<T> comparator;

  /**
   * Reference to root of this heap.
   */
  private Node<T> root;

  private MaxiphobicHeap(Comparator<T> comparator, Node<T> root) {
    this.comparator = comparator;
    this.root = root;
  }

  /**
   * Creates an empty Maxiphobic Heap.
   * <p> Time complexity: O(1)
   */
  public MaxiphobicHeap(Comparator<T> comparator) {
    this(comparator, null);
  }

  /**
   * Creates an empty Maxiphobic Heap with provided comparator.
   * <p> Time complexity: O(1)
   * @param comparator comparator to use.
   * @param <T> type of elements.
   *
   * @return empty maxiphobic heap with provided comparator.
   */
  public static <T> MaxiphobicHeap<T> empty(Comparator<T> comparator) {
    return new MaxiphobicHeap<>(comparator);
  }

  /**
   * Creates an empty Maxiphobic Heap with natural order comparator.
   * <p> Time complexity: O(1)
   * @param <T> type of elements.
   *
   * @return empty maxiphobic heap with natural order comparator.
   */
  public static <T extends Comparable<? super T>> MaxiphobicHeap<T> empty() {
    return new MaxiphobicHeap<T>(Comparator.naturalOrder());
  }

  /**
   * Constructs a Maxiphobic Heap from a list of singleton nodes in O(n) time.
   * @param comparator comparator to use
   * @param nodes list of singleton nodes
   * @param <T> type of elements
   *
   * @return skew heap with elements in nodes
   */
  private static <T> MaxiphobicHeap<T> merge(Comparator<T> comparator, JDKArrayList<Node<T>> nodes) {
      MaxiphobicHeap<T> heap = empty(comparator);

      if(nodes.isEmpty()){
          return heap;
      }

      Queue<Node<T>> q = JDKQueue.from(nodes);

      while(q.size() > 1){
          Node<T> n1 = q.first();
          q.dequeue();
          Node<T> n2 = q.first();
          q.dequeue();

          q.enqueue(heap.merge(n1,n2));
      }

      heap.root = q.first();

      return heap;

  }

  /**
   * Constructs a Maxiphobic Heap from a sequence of elements and a comparator.
   * <p>
   * Time complexity: O(n)
   * @param comparator comparator to use
   * @param elements elements to insert in heap
   * @param <T> type of elements
   *
   * @return maxiphobic heap with elements in sequence
   */
  @SafeVarargs
  public static <T> MaxiphobicHeap<T> of(Comparator<T> comparator, T... elements) {
    JDKArrayList<Node<T>> nodes = JDKArrayList.withCapacity(elements.length);
    for (T element : elements) {
      nodes.append(new Node<>(element));
    }
    return merge(comparator, nodes);
  }

  /**
   * Constructs a Maxiphobic Heap from a sequence of elements and natural order comparator.
   * <p>
   * Time complexity: O(n)
   * @param elements elements to insert in heap
   * @param <T> type of elements
   *
   * @return maxiphobic heap with elements in sequence
   */
  @SafeVarargs
  public static <T extends Comparable<? super T>> MaxiphobicHeap<T> of(T... elements) {
    return of(Comparator.naturalOrder(), elements);
  }

  /**
   * Constructs a Maxiphobic Heap from an iterable of elements and a comparator.
   * <p>
   * Time complexity: O(n)
   * @param comparator comparator to use
   * @param iterable elements to insert in heap
   * @param <T> type of elements
   *
   * @return maxiphobic heap with elements in iterable
   */
  public static <T> MaxiphobicHeap<T> from(Comparator<T> comparator, Iterable<T> iterable) {
    JDKArrayList<Node<T>> nodes = JDKArrayList.empty();
    for (T element : iterable) {
      nodes.append(new Node<>(element));
    }
    return merge(comparator, nodes);
  }

  /**
   * Constructs a Maxiphobic Heap from an iterable of elements and natural order comparator.
   * <p>
   * Time complexity: O(n)
   * @param iterable elements to insert in heap
   * @param <T> type of elements
   *
   * @return maxiphobic heap with elements in iterable
   */
  public static <T extends Comparable<? super T>> MaxiphobicHeap<T> from(Iterable<T> iterable) {
    return from(Comparator.naturalOrder(), iterable);
  }

  /**
   * Creates a new {@code MaxiphobicHeap} containing the same elements as the given {@code MaxiphobicHeap}.
   * <p> Time complexity: O(n)
   */
  public static <T> MaxiphobicHeap<T> copyOf(MaxiphobicHeap<T> that) {
    return new MaxiphobicHeap<>(that.comparator, copyOf(that.root));
  }

  // copies a tree
  private static <T> Node<T> copyOf(Node<T> node) {
      if(node == null){
          return null;
      }

      return new Node<>(node.element, node.weight, copyOf(node.left),copyOf(node.right));

  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public Comparator<T> comparator() {
      return comparator;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public boolean isEmpty() {
      return root == null;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public int size() {
      return weight(root);
  }

  private static int weight(Node<?> node) {
    return node == null ? 0 : node.weight;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   */
  @Override
  public void clear() {
      root = null;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(log n)
   */
  @Override
  public void insert(T element) {
      Node<T> node = new Node<>(element);

      root = merge(root,node);

  }

  /**
   * Merges two maxiphobic heaps.
   */
  private Node<T> merge(Node<T> node1, Node<T> node2) {
      if(node1 == null){
          return node2;
      }
      else if(node2 == null){
          return node1;
      }

      if(comparator.compare(node1.element,node2.element) > 0){ //node1 büyükse swapla
          Node<T> temp = node1;
          node1 = node2;
          node2 = temp;
      }

      node1.weight = node1.weight + node2.weight;

      Node<T> tree1 = node1.left; //en büyük olcak en sonda
      Node<T> tree2 = node1.right;
      Node<T> tree3 = node2;

      if(weight(tree2) > weight(tree1)){
          Node<T> temp = tree1;
          tree1 = tree2;
          tree2 = temp;
      }

      if(weight(tree3) > weight(tree1)){
          Node<T> temp = tree1;
          tree1 = tree3;
          tree3 = temp;
      }

      node1.left = tree1;

      node1.right = merge(tree2,tree3);

      return node1;

  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(1)
   *
   * @throws <code>EmptyHeapException</code> if heap stores no element.
   */
  @Override
  public T minimum() {
      if(isEmpty()){
          throw new EmptyHeapException("minimum on empty heap");
      }

      return root.element;
  }

  /**
   * {@inheritDoc}
   * <p> Time complexity: O(log n)
   *
   * @throws <code>EmptyHeapException</code> if heap stores no element.
   */
  @Override
  public void deleteMinimum() {
      if(isEmpty()){
          throw new EmptyHeapException("deleteMinimum on empty heap");
      }

      root = merge(root.left, root.right);

  }

  /**
   * Returns representation of this heap as a String.
   */
  @Override
  public String toString() {
    String className = getClass().getSimpleName();
    StringBuilder sb = new StringBuilder();
    sb.append(className).append("(");
    toString(sb, root);
    sb.append(")");
    return sb.toString();
  }

  /**
   * Helper method for the toString method. It recursively builds a string representation of the heap.
   *
   * @param sb The StringBuilder to append to.
   * @param node The node to represent.
   */
  private static void toString(StringBuilder sb, Node<?> node) {
    if (node == null) {
      sb.append("null");
    } else {
      String className = node.getClass().getSimpleName();
      sb.append(className).append("(");
      toString(sb, node.left);
      sb.append(", ");
      sb.append(node.element);
      sb.append(", ");
      toString(sb, node.right);
      sb.append(")");
    }
  }
}