package simplegraph.onobject;

import java.util.Collection;
import java.util.HashMap;
import simplegraph.IPathFinder;
import simplegraph.ISimpleGraph;
import simplegraph.util.GraphUtil;

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
    public void addVertex(T obj) {
        Vertex<T> id=new Vertex<>(obj, this);
        indexObjectToId.put(obj, id); //if (indexObjectToId.put(obj, id)!=null) throw new IllegalArgumentException("Value already contain: "+obj);
    }
    
    @Override
    public void addEdge(T from, T to, double weight) {
        Vertex<T> a=vertexForObejct(from);
        Vertex<T> b=vertexForObejct(to);
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
