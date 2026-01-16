package org.uma.ed.datastructures.graph;

import org.uma.ed.datastructures.dictionary.Dictionary;
import org.uma.ed.datastructures.dictionary.JDKHashDictionary;
import org.uma.ed.datastructures.list.JDKArrayList;
import org.uma.ed.datastructures.list.List;
import org.uma.ed.datastructures.priorityqueue.JDKPriorityQueue;
import org.uma.ed.datastructures.priorityqueue.PriorityQueue;
import org.uma.ed.datastructures.set.JDKHashSet;
import org.uma.ed.datastructures.set.Set;
import org.uma.ed.datastructures.tuple.Tuple2;

/**
 * A utility class for computing shortest paths in a weighted graph using Dijkstra's algorithm.
 * <p>
 * Dijkstra's algorithm finds the shortest paths from a single source vertex to all other
 * vertices in a graph with non-negative edge weights.
 * <p>
 * This specific implementation maintains two sets of vertices:
 * <ul>
 *     <li>A set of vertices for which the shortest path has already been finalized.</li>
 *     <li>A set of vertices that have not yet been finalized.</li>
 * </ul>
 * In each step, it considers all possible "extensions" (edges) from the finalized set to the
 * unfinalized set, chooses the one that results in the shortest total path, and moves the
 * destination vertex of that extension to the finalized set.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public final class Dijkstra {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private Dijkstra() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Represents a potential shortest path "extension" from a vertex in the known set
   * to an undiscovered vertex. Implements {@code Comparable} to be used in a priority queue.
   */
  private record Extension<V>(V source, V destination, Integer totalCost) implements Comparable<Extension<V>> {
    @Override
    public int compareTo(Extension<V> that) {
      return this.totalCost.compareTo(that.totalCost);
    }

    static <V> Extension<V> of(V source, V destination, Integer totalCost) {
      return new Extension<>(source, destination, totalCost);
    }
  }

  /**
   * Computes the costs of the shortest paths from a source vertex to all other vertices.
   * <p>
   * Note: This algorithm assumes all edge weights are non-negative.
   *
   * @param <V>           the type of the vertices.
   * @param weightedGraph the weighted graph.
   * @param source        the source vertex.
   * @return a {@code Dictionary} mapping reachable vertices to their shortest path costs.
   */
  public static <V> Dictionary<V, Integer> dijkstra(WeightedGraph<V, Integer> weightedGraph, V source) {
    Dictionary<V, Integer> dic = JDKHashDictionary.empty();

    PriorityQueue<Extension<V>> queue = JDKPriorityQueue.empty();

    Set<V> vertices = JDKHashSet.copyOf(weightedGraph.vertices());
    vertices.delete(source);

    Set<V> verticesOpt = JDKHashSet.of(source);

    dic.insert(source,0);

    for(WeightedGraph.Successor<V, Integer> neighbor: weightedGraph.successors(source)){
      queue.enqueue(new Extension<>(source, neighbor.vertex(), neighbor.weight() ));
    }

    while(!vertices.isEmpty()){
      Extension<V> minEx = queue.first();
      queue.dequeue();

      if(!verticesOpt.contains(minEx.destination())){

        verticesOpt.insert(minEx.destination());

        vertices.delete(minEx.destination());

        dic.insert(minEx.destination(), minEx.totalCost());

        for(WeightedGraph.Successor<V, Integer> neighbor: weightedGraph.successors(minEx.destination())){
          queue.enqueue(new Extension<>(minEx.destination(), neighbor.vertex(), neighbor.weight()+ minEx.totalCost()));
        }

      }
    }

    return dic;
  }

  /**
   * A version of Extension that also carries the full path from the source.
   */
  private record PathExtension<V>(V source, V destination, Integer totalCost, List<V> path) implements Comparable<PathExtension<V>> {
    @Override
    public int compareTo(PathExtension<V> that) {
      return this.totalCost.compareTo(that.totalCost);
    }

    static <V> PathExtension<V> of(V source, V destination, Integer totalCost, List<V> path) {
      return new PathExtension<>(source, destination, totalCost, path);
    }
  }

  /**
   * Computes the shortest paths (costs and vertex sequences) from a source vertex to all other vertices.
   *
   * @param <V>           the type of the vertices.
   * @param weightedGraph the weighted graph.
   * @param source        the source vertex.
   * @return a {@code Dictionary} mapping reachable vertices to a pair containing the
   *         minimum cost and the list of vertices in the shortest path.
   */
  public static <V> Dictionary<V, Tuple2<Integer, List<V>>> dijkstraPaths(WeightedGraph<V, Integer> weightedGraph, V source) {
    Dictionary<V, Tuple2<Integer, List<V>>> dic = JDKHashDictionary.empty();

    List<V> path = JDKArrayList.of(source);

    PriorityQueue<PathExtension<V>> queue = JDKPriorityQueue.empty();

    Set<V> vertices = JDKHashSet.copyOf(weightedGraph.vertices());
    vertices.delete(source);

    Set<V> verticesOpt = JDKHashSet.of(source);

    dic.insert(source, new Tuple2<>(0, JDKArrayList.empty()));

    for(WeightedGraph.Successor<V, Integer> neighbor : weightedGraph.successors(source)){
      List<V> NeighborPath = JDKArrayList.copyOf(path);

      NeighborPath.append(neighbor.vertex());

      queue.enqueue(new PathExtension<>(source, neighbor.vertex(), neighbor.weight(), NeighborPath ));
    }

    while(!vertices.isEmpty()){

      PathExtension<V> minPathEx = queue.first();
      queue.dequeue();

      if(!verticesOpt.contains(minPathEx.destination())){

        verticesOpt.insert(minPathEx.destination());

        vertices.delete(minPathEx.destination());

        dic.insert(minPathEx.destination(), new Tuple2<>(minPathEx.totalCost(), minPathEx.path()));

        for(WeightedGraph.Successor<V, Integer> neighbor : weightedGraph.successors(minPathEx.destination())){
          List<V> NeighborPath = JDKArrayList.copyOf(minPathEx.path());

          NeighborPath.append(neighbor.vertex());

          queue.enqueue(new PathExtension<>(minPathEx.destination(), neighbor.vertex(), minPathEx.totalCost()+ neighbor.weight(), NeighborPath ));
        }

      }
    }

    return dic;
  }
}