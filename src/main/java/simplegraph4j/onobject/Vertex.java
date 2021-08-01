package simplegraph4j.onobject;

import java.util.ArrayList;
import java.util.Collection;
import simplegraph4j.IEdge;
import simplegraph4j.IVertex;
import simplegraph4j.SimpleGraphConfig;

/**
 * Вершина
 * https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 */
public class Vertex<T> implements IVertex<T>, Comparable<Vertex>
{
    public final T name; // external vertex name
    protected final Collection<Edge<T>> adjacencies=new ArrayList<>();
    protected SimpleGraph<T> vertexResolver; // reference only for support addEdge()
    
    // foe Dijkstra
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex<T> previous; // IDijkstraVertex
    
    
    public Vertex(T argName, SimpleGraph<T> ownGraph) {
        name = argName;
        vertexResolver = ownGraph;
    }
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
    
    @Override
    public long edgesCount() {
        return adjacencies.size();
    }

    @Override
    public void addEdge(T to, double weight) {
        Vertex<T> b=vertexResolver.vertexForObejct(to);
        if (b==null) {
            if (SimpleGraphConfig.isAllowAutoAddVertex()) {
                b=vertexResolver.addVertex(to);
            } else {
                throw new IllegalArgumentException("Vertex not found: "+to);
            }
        }
        adjacencies.add(new Edge(b, weight));
    }
}
