package simplegraph4j.onobject;

import java.util.Collection;
import java.util.HashMap;
import simplegraph4j.IPathFinder;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.SimpleGraphConfig;
import simplegraph4j.util.GraphUtil;

/**
 * Primitive graph with simple Dijkstra path search implementation
 * 
 * 
 * Thanks https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 * 
 * @author A.K.(github.com/playerO1) and same code from stackoverflow
 */
public class SimpleGraph<T> implements ISimpleGraph<T>{
    // mapping external key
    protected final HashMap<T,Vertex<T>> indexObjectToId;
    //protected final HashMap<Vertex,T> indexIdToObject;
    
    public SimpleGraph() {
        this.indexObjectToId=new HashMap<>();
    }

    @Override
    public Vertex<T> vertexForObejct(T obj) {
        return indexObjectToId.get(obj); // NullPointer possible!
    }
    
    @Override
    public Vertex<T> addVertex(T obj) {
        Vertex<T> id=new Vertex<>(obj, this);
        Vertex<T> hasOverride=indexObjectToId.put(obj, id);
        assert hasOverride==null;  //if (indexObjectToId.put(obj, id)!=null) throw new IllegalArgumentException("Value already contain: "+obj);
        return id;
    }
    
    @Override
    public void addEdge(T from, T to, double weight) {
        Vertex<T> a=vertexForObejct(from);
        Vertex<T> b=vertexForObejct(to);
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
        a.adjacencies.add(new Edge(b, weight));
    }
    
    // size
    @Override
    public Collection<Vertex<T>> getAllVertex() {
        return indexObjectToId.values();
    }
    
    @Override
    public int vertices() {
        return getAllVertex().size();
    }
    
    @Override
    public long edges() {
        int summ=0;
        for (Vertex v:getAllVertex())
            summ+=v.adjacencies.size();
        return summ;
    }

    @Override
    public String toString() {
        return super.toString()+ "{" + GraphUtil.toString(this) + '}';
    }

    @Override
    public IPathFinder<T> pathFinder() {
        return new DijkstraPathFind<>(this);
    }
}
