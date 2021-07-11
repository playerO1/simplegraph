package simplegraph4j.onprimitive;

import java.util.Iterator;
import java.util.NoSuchElementException;
import simplegraph4j.IEdge;
import simplegraph4j.IVertex;
import simplegraph4j.onfile.FileVertex;

public class PrimitiveVertex<T> implements IVertex<T>, Comparable<PrimitiveVertex>,
        Iterable<PrimitiveVertex.PEdge<T>>
{
    private final PrimitiveGraph vertexIndexResolver; // only for getAdjacencies()
    public final int id;
    
    public final T name; // external vertex name
    protected final EdgeHolder adjacencies=new EdgeHolder();
    
    // foe Dijkstra
    public double minDistance = Double.POSITIVE_INFINITY;
    public PrimitiveVertex<T> previous; // IDijkstraVertex
    
    
    public PrimitiveVertex(PrimitiveGraph vertexIndexResolver, int id, T argName) {
        this.vertexIndexResolver=vertexIndexResolver;
        this.id=id;
        this.name = argName; 
    }
    public String toString() { return name.toString(); }
    public int compareTo(PrimitiveVertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

    @Override
    public T getName() {
        return name;
    }

    @Override
    public Iterable<PEdge<T>> getAdjacencies() {
        return this;
    }

    public PEdge<T> getEdge(int i) {
        PrimitiveVertex<T> vertex=vertexIndexResolver.getVertexById(adjacencies.getTarget(i));
        return new PEdge<>(vertex, adjacencies.getWeight(i));
    }

    @Override
    public Iterator<PEdge<T>> iterator() {
        return new Iterator() {
            int i=0;
            @Override
            public boolean hasNext() {
                return i<adjacencies.size();
            }
            @Override
            public Object next() {
                if (i>=adjacencies.size()) throw new NoSuchElementException();
                return getEdge(i++);
            }
        };
    }
    
    @Override
    public void addEdge(T to, double weight) {
        PrimitiveVertex<T> b=vertexIndexResolver.vertexForObejct(to);
        adjacencies.addEdge(b.id, weight);
    }
    
    public void trimToSize() {
        adjacencies.trimToSize();
    }
    
    public void clear() {
        adjacencies.clear();
    }

    public static class PEdge<T> implements IEdge<T>{
        public final PrimitiveVertex<T> target;
        public final double weight;
        public PEdge(PrimitiveVertex<T> argTarget, double argWeight){
            target = argTarget; weight = argWeight;
        }
        public PrimitiveVertex<T> getTarget() {
            return target;
        }
        public double getWeight() {
            return weight;
        }
    }
}
