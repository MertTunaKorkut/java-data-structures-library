package org.uma.ed.datastructures.graph;

import org.uma.ed.datastructures.dictionary.Dictionary;
import org.uma.ed.datastructures.dictionary.JDKHashDictionary;
import org.uma.ed.datastructures.list.JDKArrayList;
import org.uma.ed.datastructures.list.List;
import org.uma.ed.datastructures.queue.JDKQueue;
import org.uma.ed.datastructures.queue.Queue;
import org.uma.ed.datastructures.set.JDKHashSet;
import org.uma.ed.datastructures.set.Set;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BFTPaths<V> {
    private final V source;
    private final Set<V> visited;
    private final List<V> traversal;
    private final Dictionary<V,V> parentOf;

    public BFTPaths(Traversable<V> graph, V source){  //constructor
        this.source = source;

        visited = JDKHashSet.empty();  //initialize them empty
        traversal = JDKArrayList.empty();
        parentOf = JDKHashDictionary.empty();

        Queue<DiEdge<V>> q = JDKQueue.of(DiEdge.of(null, source)); //initialize a queue with a dummy edge

        while(!q.isEmpty()){  //while queue is not empty

            DiEdge<V> diEdge = q.first();  //take the first from queue and dequeue
            q.dequeue();

            if(!visited.contains(diEdge.destination())){

                traversal.append(diEdge.destination());  //add the destination to traversal and mark is as visited
                visited.insert(diEdge.destination());

                parentOf.insert(diEdge.destination(), diEdge.source());  //add the edge to the dictionary reversed to be used for path-building later

                for(V successor : graph.successors(diEdge.destination())){  //enqueue the unvisited successors of the destination

                    if(!visited.contains(successor)){
                        q.enqueue(DiEdge.of(diEdge.destination(), successor));
                    }
                }
            }
        }
    }

    public static <V> BFTPaths<V> of(Traversable<V> graph, V source){  //factory method
        return new BFTPaths<>(graph, source);
    }

    public List<V> traversal(){  //traversal getter
        return traversal;
    }

    public List<V> pathTo(V destination){  //return the path to the given destination from the source using a spanning tree
        List<V> path = JDKArrayList.of(destination);

        V vertex = parentOf.valueOf(destination);  //initialize vertex as the parent of destination

        while(vertex != null){  //while vertex is not null

            path.prepend(vertex);  //add the vertex to start of the path
            vertex = parentOf.valueOf(vertex);  //... and advance it to its parent
        }

        return path;
    }
}
