package simplegraph4j.onprimitive.primfloat;

import java.util.Iterator;
import java.util.NoSuchElementException;
import simplegraph4j.IEdge;
import simplegraph4j.IVertex;

public class PrimitiveFloatVertex<T> implements IVertex<T>, Comparable<PrimitiveFloatVertex>, Iterable<PrimitiveFloatVertex.PEdge<T>>
{
    private final PrimitiveGraph vertexIndexResolver; // only for getAdjacencies()
    public final int id;
    
    public final T name; // external vertex name
    protected final EdgeFloatHolder adjacencies=new EdgeFloatHolder();
    
    // foe Dijkstra
    public float minDistance = Float.POSITIVE_INFINITY;
    public PrimitiveFloatVertex<T> previous; // IDijkstraVertex
    
    
    public PrimitiveFloatVertex(PrimitiveGraph vertexIndexResolver, int id, T argName) {
        this.vertexIndexResolver=vertexIndexResolver;
        this.id=id;
        this.name = argName; 
    }
    public String toString() { return name.toString(); }
    public int compareTo(PrimitiveFloatVertex other)
    {
        return Float.compare(minDistance, other.minDistance);
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
        PrimitiveFloatVertex<T> vertex=vertexIndexResolver.getVertexById(adjacencies.getTarget(i));
        return new PEdge<>(vertex, adjacencies.getWeight(i));
    }

    @Override
    public void addEdge(T to, double weight) {
        PrimitiveFloatVertex<T> b=vertexIndexResolver.vertexForObejct(to);
        adjacencies.addEdge(b.id, (float)weight);
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
    
    public void trimToSize() {
        adjacencies.trimToSize();
    }
    
    public void clear() {
        adjacencies.clear();
    }

    public static class PEdge<T> implements IEdge<T>{
        public final PrimitiveFloatVertex<T> target;
        public final float weight;
        public PEdge(PrimitiveFloatVertex<T> argTarget, float argWeight){
            target = argTarget; weight = argWeight;
        }
        public PrimitiveFloatVertex<T> getTarget() {
            return target;
        }
        public double getWeight() {
            return weight; // float to double
        }
    }
}
