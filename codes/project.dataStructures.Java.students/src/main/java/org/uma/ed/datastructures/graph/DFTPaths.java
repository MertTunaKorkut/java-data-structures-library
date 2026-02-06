package org.uma.ed.datastructures.graph;

import org.uma.ed.datastructures.dictionary.Dictionary;
import org.uma.ed.datastructures.dictionary.JDKHashDictionary;
import org.uma.ed.datastructures.list.JDKArrayList;
import org.uma.ed.datastructures.list.List;
import org.uma.ed.datastructures.set.JDKHashSet;
import org.uma.ed.datastructures.set.Set;
import org.uma.ed.datastructures.stack.JDKStack;
import org.uma.ed.datastructures.stack.Stack;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DFTPaths<V>{
    private final V source; //source of traversal
    private final Set<V> visited; //already visited nodes
    private final List<V> traversal; //traversal
    private final Dictionary<V,V> parentOf; //spanning tree as "parentOf" dictionary

    public DFTPaths(Traversable<V> graph, V source){
        this.source = source;

        visited = JDKHashSet.empty();
        traversal = JDKArrayList.empty();
        parentOf = JDKHashDictionary.empty();


        Stack<DiEdge<V>> stack = JDKStack.of(DiEdge.of(null, source)); //stack initialized with a dummy edge

        while(!stack.isEmpty()){ //while the stack is not empty

            DiEdge<V> diEdge = stack.top(); //take the top of the stack and pop it
            stack.pop();

            if(!visited.contains(diEdge.destination())){ //if destination has not been visited

                traversal.append(diEdge.destination()); //add the edge to traversal
                visited.insert(diEdge.destination());  // and mark it as visited

                parentOf.insert(diEdge.destination(), diEdge.source()); //add the edge to the dictionary reversed to use it later for path-building

                for(V successor : graph.successors(diEdge.destination())){ //push the unvisited successors of destination vertex to the stack

                    if(!visited.contains(successor)) {
                        stack.push(DiEdge.of(diEdge.destination(), successor));
                    }
                }
            }
        }
    }

    public static <V> DFTPaths<V> of(Traversable<V> graph, V source){ //factory method
        return new DFTPaths<>(graph, source);
    }

    public List<V> traversal(){ //traversal getter
        return traversal;
    }

    public List<V> pathTo(V destination){ //return the path to the given destination from the source using a spanning tree

        if(!visited.contains(destination)){
            throw new GraphException("vertex " + destination.toString() + " was not visited");
        }

        List<V> path = JDKArrayList.of(destination);

        V vertex = parentOf.valueOf(destination); //set the vertex as parent of the destination

        while(vertex != null){ //while vertex is not null

            path.prepend(vertex); //prepending the vertex to the path
            vertex = parentOf.valueOf(vertex); //set vertex as its parent
        }

        return path;
    }
}





























































