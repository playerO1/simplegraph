package simplegraph.onobject;

import java.util.ArrayList;
import java.util.Collection;
import simplegraph.IEdge;
import simplegraph.IVertex;

/**
 * Вершина
 * https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 */
public class Vertex<T> implements IVertex<T>, Comparable<Vertex>
{
    public final T name; // external vertex name
    protected final Collection<Edge<T>> adjacencies=new ArrayList<>();
    
    // foe Dijkstra
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex<T> previous; // IDijkstraVertex
    
    
    public Vertex(T argName) { name = argName; }
    public String toString() { return name.toString(); }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

    @Override
    public T getName() {
        return name;
    }

    @Override
    public Iterable<IEdge<T>> getAdjacencies() {
        return (Iterable)adjacencies; // todo check type
    }

}
