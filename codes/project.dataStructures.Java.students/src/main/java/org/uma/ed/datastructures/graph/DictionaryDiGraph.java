package org.uma.ed.datastructures.graph;

import org.uma.ed.datastructures.dictionary.Dictionary;
import org.uma.ed.datastructures.dictionary.JDKHashDictionary;
import org.uma.ed.datastructures.set.JDKHashSet;
import org.uma.ed.datastructures.set.Set;

import java.util.StringJoiner;

/**
 * An implementation of the {@link DiGraph} interface using an adjacency list representation.
 * <p>
 * The adjacency list is stored in a {@link Dictionary}, where each key is a vertex,
 * and the corresponding value is a {@link Set} of its successor vertices (i.e., the
 * vertices reachable via a direct outgoing edge).
 * <p>
 * This representation is efficient for sparse graphs.
 *
 * @param <V> The type of the vertices in the graph.
 *
 * @author Pepe Gallardo, Data Structures, Grado en Informática. UMA.
 */
public class DictionaryDiGraph<V> implements DiGraph<V> {

  /**
   * The core data structure: a dictionary mapping each vertex to its set of successors.
   */
  private final Dictionary<V, Set<V>> successorsOf;

  /**
   * Constructs an empty {@code DictionaryDiGraph}.
   */
  public DictionaryDiGraph() {
    this.successorsOf = JDKHashDictionary.empty();
  }

  /**
   * Creates an empty directed graph.
   *
   * @param <V> Type for vertices in the graph.
   * @return An empty {@code DictionaryDiGraph}.
   */
  public static <V> DictionaryDiGraph<V> empty() {
    return new DictionaryDiGraph<>();
  }

  /**
   * Creates a new {@code DictionaryDiGraph} with a given set of vertices and edges.
   *
   * @param <V>      The type for vertices.
   * @param vertices The initial set of vertices.
   * @param edges    The initial set of directed edges.
   * @return A new {@code DictionaryDiGraph} populated with the given data.
   */
  public static <V> DictionaryDiGraph<V> of(Set<V> vertices, Set<DiEdge<V>> edges) {
    DictionaryDiGraph<V> diGraph = new DictionaryDiGraph<>();
    for (V vertex : vertices) {
      diGraph.addVertex(vertex);
    }
    for (DiEdge<V> edge : edges) {
      diGraph.addDiEdge(edge.source(), edge.destination());
    }
    return diGraph;
  }

  /**
   * Creates a new {@code DictionaryDiGraph} that is a copy of the given directed graph.
   *
   * @param <V>     The type for vertices.
   * @param diGraph The directed graph to be copied.
   * @return A new {@code DictionaryDiGraph} with the same vertices and edges.
   */
  public static <V> DictionaryDiGraph<V> copyOf(DiGraph<V> diGraph) {
    return of(diGraph.vertices(), diGraph.edges());
  }

  @Override
  public boolean isEmpty() {
    return successorsOf.isEmpty();
  }

  @Override
  public void addVertex(V vertex) {
    successorsOf.insert(vertex, JDKHashSet.empty());
  }

  @Override
  public void addDiEdge(V source, V destination) {
    if(!successorsOf.isDefinedAt(source)){
      throw new GraphException("addDiEdge: source vertex "+ source.toString()+" is not in the graph.");
    }
    if(!successorsOf.isDefinedAt(destination)){
      throw new GraphException("addDiEdge: destination vertex "+ destination.toString()+" is not in the graph.");
    }

    successorsOf.valueOf(source).insert(destination);
  }

  @Override
  public void deleteDiEdge(V source, V destination) {

    successorsOf.valueOf(source).delete(destination);
  }

  @Override
  public void deleteVertex(V vertex) {
    if(successorsOf.isDefinedAt(vertex)){
      successorsOf.delete(vertex);

      Iterable<V> keys = successorsOf.keys();

      for(V key : keys){
        successorsOf.valueOf(key).delete(vertex);
      }
    }
  }

  @Override
  public Set<V> vertices() {
    return JDKHashSet.from(successorsOf.keys());
  }

  @Override
  public Set<DiEdge<V>> edges() {
    Set<DiEdge<V>> edgeSet = JDKHashSet.empty();

    Iterable<V> sources = successorsOf.keys();

    for(V source : sources){

      Set<V> destinationSet = successorsOf.valueOf(source);

      for(V destination : destinationSet){
        edgeSet.insert(new DiEdge<>(source, destination));
      }
    }

    return edgeSet;
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
  public Set<V> successors(V source) {
    if(!successorsOf.isDefinedAt(source)){
      throw new GraphException("successors: source " + source.toString()+ " is not in the graph.");
    }
    return successorsOf.valueOf(source);
  }

  @Override
  public Set<V> predecessors(V destination) {
    if(!successorsOf.isDefinedAt(destination)){
      throw new GraphException("successors: destination " + destination.toString()+ " is not in the graph.");
    }

    Set<V> newSet = JDKHashSet.empty();

    Iterable<V> keys = successorsOf.keys();

    for(V key: keys){

      if(successorsOf.valueOf(key).contains(destination)){
        newSet.insert(key);
      }
    }

    return newSet;
  }

  @Override
  public int inDegree(V vertex) {
    if(!successorsOf.isDefinedAt(vertex)){
      throw new GraphException("outDegree: vertex "+ vertex.toString()+" is not in the graph.");
    }

    return predecessors(vertex).size();
  }

  @Override
  public int outDegree(V vertex) {
    if(!successorsOf.isDefinedAt(vertex)){
      throw new GraphException("outDegree: vertex "+ vertex.toString()+" is not in the graph.");
    }
    return successors(vertex).size();
  }

  @Override
  public String toString() {
    String className = getClass().getSimpleName();

    StringJoiner verticesSJ = new StringJoiner(", ", "vertices(", ")");
    for (V vertex : this.vertices()) {
      verticesSJ.add(vertex.toString());
    }

    StringJoiner edgesSJ = new StringJoiner(", ", "edges(", ")");
    for (DiEdge<V> edge : this.edges()) {
      edgesSJ.add(edge.toString());
    }

    StringJoiner sj = new StringJoiner(", ", className + "(", ")");
    sj.add(verticesSJ.toString());
    sj.add(edgesSJ.toString());
    return sj.toString();
  }
}