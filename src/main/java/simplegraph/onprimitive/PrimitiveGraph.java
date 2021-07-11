package simplegraph.onprimitive;

import java.io.Closeable;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.management.*;
import simplegraph.IPathFinder;
import simplegraph.ISimpleGraph;
import simplegraph.ITrimToSize;

/**
 * Primitive graph with simple Dijkstra path search implementation
 * 
 * @author A.K.(github.com/playerO1)
 */
public class PrimitiveGraph<T> implements ISimpleGraph<T>, ITrimToSize, Closeable, PrimitiveGraphMBean{
    //final static Logger log=LoggerFactory.getLogger(PrimitiveGraph.class);
    // mapping external key
    protected final HashMap<T,PrimitiveVertex<T>> indexObjectToId;
    protected final List<PrimitiveVertex<T>> indexOfVertex;

    public PrimitiveGraph() {
        this.indexObjectToId=new HashMap<>();
        this.indexOfVertex=new ArrayList<>();
        // debug MBean interface, todo check memory leak byMBean reference
        try { 
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            mbs.registerMBean(this, new ObjectName("util.simplegraph:type="+getName()));
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            //log.warn("Error register MBean", ex);
            ex.printStackTrace();
        }
    }

    @Override
    public PrimitiveVertex<T> vertexForObejct(T obj) {
        return indexObjectToId.get(obj); // NullPointer possible!
    }
    protected PrimitiveVertex<T> getVertexById(int id) {
        return indexOfVertex.get(id);
    }
    
    @Override
    public void addVertex(T obj) {
        int id=indexOfVertex.size();
        PrimitiveVertex<T> vertex=new PrimitiveVertex<>(this, id, obj);
        indexObjectToId.put(obj, vertex); //if (indexObjectToId.put(obj, id)!=null) throw new IllegalArgumentException("Value already contain: "+obj);
        indexOfVertex.add(vertex);
    }
    
    @Override
    public void addEdge(T from, T to, double weight) {
        PrimitiveVertex<T> a=vertexForObejct(from);
        PrimitiveVertex<T> b=vertexForObejct(to);
        a.adjacencies.addEdge(b.id, weight);
    }
    
    // size
    @Override
    public Collection<PrimitiveVertex<T>> getAllVertex() {
        return indexObjectToId.values();
    }
    
    @Override
    public int vertices() {
        return getAllVertex().size();
    }
    
    @Override
    public long edges() {
        long summ=0;
        for (PrimitiveVertex v:getAllVertex())
            summ+=v.adjacencies.size();
        return summ;
    }

    @Override
    public void trimToSize() {
        for (PrimitiveVertex v:getAllVertex()) v.trimToSize();
    }
    
    public void clear() {
        for (PrimitiveVertex v:getAllVertex()) v.clear();
    }

    /* todo toString() using for MBeans. It should be unical as super.toString() without data
    @Override
    public String toString() {
        return "PrimitiveGraph{" + GraphUtil.toString(this) + '}';
    }*/

    @Override
    public IPathFinder<T> pathFinder() {
        return new PDijkstraPathFind<>(this);
    }
    
    // MBean debug
    boolean alreadyClosed=false;
    @Override
    public void close(){
        if (!alreadyClosed) {
            clear();
            try { // debug MBean
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
                mbs.unregisterMBean(new ObjectName("util.simplegraph:type="+getName()));
            } catch (InstanceNotFoundException | MalformedObjectNameException | MBeanRegistrationException ex) {
                //log.warn("Error unregister MBean", ex);
            }
            alreadyClosed=true;
        }
    }
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        close();
    }
    
    // MBean debug interface
    @Override
    public String getName() {
        return "primitiveGraph-"+this.toString();
    }
    @Override
    public int getVertices() {
        return vertices();
    }
    @Override
    public long getEdges() {
        return edges();//todo concurrent ?
    }
}
