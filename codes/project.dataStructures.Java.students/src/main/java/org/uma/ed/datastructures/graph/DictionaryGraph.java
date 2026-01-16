package org.uma.ed.datastructures.graph;

import org.uma.ed.datastructures.dictionary.Dictionary;
import org.uma.ed.datastructures.dictionary.JDKHashDictionary;
import org.uma.ed.datastructures.set.JDKHashSet;
import org.uma.ed.datastructures.set.Set;

import java.util.StringJoiner;

/**
 * An implementation of the {@link Graph} interface using an adjacency list representation.
 * <p>
 * The adjacency list is stored in a {@link Dictionary}, where each key is a vertex in the
 * graph, and the corresponding value is a {@link Set} of its adjacent vertices (neighbors).
 * <p>
 * This representation is efficient for sparse graphs (graphs with relatively few edges).
 * Operations like adding a vertex or an edge, and finding the neighbors of a vertex,
 * are typically very fast.
 *
 * @param <V> The type of the vertices in the graph.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class DictionaryGraph<V> implements Graph<V> {

  /**
   * The core data structure: a dictionary mapping each vertex to its set of neighbors.
   */
  private final Dictionary<V, Set<V>> adjacentsOf;

  /**
   * Constructs an empty {@code DictionaryGraph}.
   */
  public DictionaryGraph() {
    this.adjacentsOf = JDKHashDictionary.empty();
  }

  /**
   * Creates an empty {@code DictionaryGraph}.
   *
   * @param <V> The type for vertices in the graph.
   * @return An empty {@code DictionaryGraph}.
   */
  public static <V> DictionaryGraph<V> empty() {
    return new DictionaryGraph<>();
  }

  /**
   * Creates a new {@code DictionaryGraph} with a given set of vertices and edges.
   *
   * @param <V>      The type for vertices in the graph.
   * @param vertices The initial set of vertices.
   * @param edges    The initial set of edges.
   * @return A new {@code DictionaryGraph} populated with the given data.
   */
  public static <V> DictionaryGraph<V> of(Set<V> vertices, Set<Edge<V>> edges) {
    DictionaryGraph<V> graph = new DictionaryGraph<>();
    for (V vertex : vertices) {
      graph.addVertex(vertex);
    }
    for (Edge<V> edge : edges) {
      graph.addEdge(edge.vertex1(), edge.vertex2());
    }
    return graph;
  }

  /**
   * Creates a new {@code DictionaryGraph} that is a copy of the given graph.
   *
   * @param <V>   The type for vertices in the graph.
   * @param graph The graph to be copied.
   * @return A new {@code DictionaryGraph} with the same vertices and edges.
   */
  public static <V> DictionaryGraph<V> copyOf(Graph<V> graph) {
    return of(graph.vertices(), graph.edges());
  }

  @Override
  public boolean isEmpty() {
    return adjacentsOf.isEmpty();
  }

  @Override
  public void addVertex(V vertex) {
    if(!adjacentsOf.isDefinedAt(vertex)) {
      adjacentsOf.insert(vertex, JDKHashSet.empty());
    }
  }

  @Override
  public void addEdge(V vertex1, V vertex2) {

    if(!adjacentsOf.isDefinedAt(vertex1)) {
      throw new GraphException("addEdge: vertex " + vertex1.toString()+" is not in the graph.");
    }

    if(!adjacentsOf.isDefinedAt(vertex2)){
      throw new GraphException("addEdge: vertex " + vertex2.toString()+" is not in the graph.");
    }
    adjacentsOf.valueOf(vertex1).insert(vertex2);
    adjacentsOf.valueOf(vertex2).insert(vertex1);
  }

  @Override
  public void deleteEdge(V vertex1, V vertex2) {

    if(adjacentsOf.valueOf(vertex1).contains(vertex2) && adjacentsOf.valueOf(vertex2).contains(vertex1)) {
      adjacentsOf.valueOf(vertex1).delete(vertex2);
      adjacentsOf.valueOf(vertex2).delete(vertex1);
    }

  }

  @Override
  public void deleteVertex(V vertex) {
    if(adjacentsOf.isDefinedAt(vertex)){
      adjacentsOf.delete(vertex);
    }

    Iterable<V> keys = adjacentsOf.keys();

    for(V key : keys){
      adjacentsOf.valueOf(key).delete(vertex);
    }
  }

  @Override
  public Set<V> vertices() {
    return JDKHashSet.from(adjacentsOf.keys());
  }

  @Override
  public Set<Edge<V>> edges() {
    Set<Edge<V>> newSet = JDKHashSet.empty();

    Iterable<V> keys = adjacentsOf.keys();

    for(V key : keys){

      Set<V> successors = adjacentsOf.valueOf(key);

      for(V successor : successors){
        newSet.insert(new Edge<>(key, successor));
      }
    }

    return newSet;
  }

  @Override
  public int numberOfVertices() {
    return vertices().size();
  }

  @Override
  public int numberOfEdges() {
    return edges().size();
  }

  @Override
  public Set<V> successors(V vertex) {
    if(!adjacentsOf.isDefinedAt(vertex)){
      throw new GraphException("successors: vertex "+vertex.toString()+" is not in the graph.");
    }
    return adjacentsOf.valueOf(vertex);
  }

  @Override
  public int degree(V vertex) {
    if(!adjacentsOf.isDefinedAt(vertex)){
      throw new GraphException("degree: vertex "+ vertex.toString()+ " is not in the graph.");
    }
    return adjacentsOf.valueOf(vertex).size();
  }

  @Override
  public String toString() {
    String className = getClass().getSimpleName();

    StringJoiner verticesSJ = new StringJoiner(", ", "vertices(", ")");
    for (V vertex : vertices()) {
      verticesSJ.add(vertex.toString());
    }

    StringJoiner edgesSJ = new StringJoiner(", ", "edges(", ")");
    for (Edge<V> edge : edges()) {
      edgesSJ.add(edge.toString());
    }

    StringJoiner sj = new StringJoiner(", ", className + "(", ")");
    sj.add(verticesSJ.toString());
    sj.add(edgesSJ.toString());
    return sj.toString();
  }
}