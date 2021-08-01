package simplegraph4j.onprimitive.primfloat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import simplegraph4j.IPathFinder;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.ITrimToSize;
import simplegraph4j.SimpleGraphConfig;

/**
 * Primitive graph with simple Dijkstra path search implementation
 * 
 * 
 * Thanks https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 * 
 * @author A.K.(github.com/playerO1)
 */
public class PrimitiveGraph<T> implements ISimpleGraph<T>, ITrimToSize{
    // mapping external key
    protected final HashMap<T,PrimitiveFloatVertex<T>> indexObjectToId;
    protected final List<PrimitiveFloatVertex<T>> indexOfVertex;

    public PrimitiveGraph() {
        this.indexObjectToId=new HashMap<>();
        this.indexOfVertex=new ArrayList<>();
    }

    @Override
    public PrimitiveFloatVertex<T> vertexForObejct(T obj) {
        return indexObjectToId.get(obj); // NullPointer possible!
    }
    protected PrimitiveFloatVertex<T> getVertexById(int id) {
        return indexOfVertex.get(id);
    }
    
    @Override
    public PrimitiveFloatVertex<T> addVertex(T obj) {
        int id=indexOfVertex.size();
        PrimitiveFloatVertex<T> vertex=new PrimitiveFloatVertex<>(this, id, obj);
        PrimitiveFloatVertex<T> hasOverride = indexObjectToId.put(obj, vertex); //if (indexObjectToId.put(obj, id)!=null) throw new IllegalArgumentException("Value already contain: "+obj);
        assert hasOverride==null;
        indexOfVertex.add(vertex);
        return vertex;
    }
    
    @Override
    public void addEdge(T from, T to, double weight) {
        PrimitiveFloatVertex<T> a=vertexForObejct(from);
        PrimitiveFloatVertex<T> b=vertexForObejct(to);
        if (a==null) {
            if (SimpleGraphConfig.isAllowAutoAddVertex()) {
                a=addVertex(from);
            } else {
                throw new IllegalArgumentException("Vertex not found: "+from);
            }
        }
        if (b==null) {
            if (SimpleGraphConfig.isAllowAutoAddVertex()) {
                b=addVertex(to);
            } else {
                throw new IllegalArgumentException("Vertex not found: "+to);
            }
        }
        a.adjacencies.addEdge(b.id, (float)weight);
    }
    
    // size
    @Override
    public Collection<PrimitiveFloatVertex<T>> getAllVertex() {
        return indexObjectToId.values();
    }
    
    @Override
    public int vertices() {
        return getAllVertex().size();
    }
    
    @Override
    public long edges() {
        int summ=0;
        for (PrimitiveFloatVertex v:getAllVertex())
            summ+=v.adjacencies.size();
        return summ;
    }

    @Override
    public void trimToSize() {
        for (PrimitiveFloatVertex v:getAllVertex()) v.trimToSize();
    }
    
    public void clear() {
        for (PrimitiveFloatVertex v:getAllVertex()) v.clear();
    }

    //todo to string. But MBeans required stateless toString,

    @Override
    public IPathFinder<T> pathFinder() {
        return new PDijkstraPathFind<>(this);
    }
}
