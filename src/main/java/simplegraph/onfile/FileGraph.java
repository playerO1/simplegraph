package simplegraph.onfile;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.management.*;
import simplegraph.IPathFinder;
import simplegraph.ISimpleGraph;
import simplegraph.ITrimToSize;

/**
 * File storage graph with simple Dijkstra path search implementation
 * ITrimToSize.trimToSize() do flush to disk all not flushed data.
 * 
 * @author A.K.
 */
public class FileGraph<T> implements ISimpleGraph<T>, ITrimToSize, Closeable, FileGraphMBean{
    //final static Logger log=LoggerFactory.getLogger(FileGraph.class);
    public final static int MAX_OPEN_FILE = 1200; // For watchout OS limit open file. See Linux /etc/secure/limits.conf.
    
    protected final File workPath;
    // mapping external key
    protected final HashMap<T,FileVertex<T>> indexObjectToId;
    protected final List<FileVertex<T>> indexOfVertex;

    public FileGraph(File workPath) throws IOException {
        this.indexObjectToId=new HashMap<>();
        this.indexOfVertex=new ArrayList<>();
        this.workPath=workPath;
        //log.debug("Graph temp path \"{}\"", workPath.getCanonicalPath());
        if (!workPath.isDirectory()) {
            //log.info("Graph make temp path \"{}\"", workPath.getCanonicalPath());
            if (!workPath.mkdir()) throw new IOException("Can not make directory "+workPath);
        }
        // debug MBean interface
        try { 
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            mbs.registerMBean(this, new ObjectName("util.simplegraph:type=onFile-"+getName()));
        } catch (MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex) {
            //log.warn("Error register MBean", ex);
        }
    }
    // todo cleanup method

    @Override
    public FileVertex<T> vertexForObejct(T obj) {
        return indexObjectToId.get(obj); // NullPointer possible!
    }
    protected FileVertex<T> getVertexById(int id) {
        return indexOfVertex.get(id);
    }
    
    @Override
    public void addVertex(T obj) {
        try {
            int id=indexOfVertex.size();
            FileVertex<T> vertex=new FileVertex<>(this, id, obj);
            indexObjectToId.put(obj, vertex); //if (indexObjectToId.put(obj, id)!=null) throw new IllegalArgumentException("Value already contain: "+obj);
            indexOfVertex.add(vertex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void addEdge(T from, T to, double weight) {
        FileVertex<T> a=vertexForObejct(from);
        FileVertex<T> b=vertexForObejct(to);
        try {
            a.adjacencies.addEdge(b.id, weight);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    // size
    @Override
    public Collection<FileVertex<T>> getAllVertex() {
        return indexObjectToId.values();
    }
    
    @Override
    public int vertices() {
        return getAllVertex().size();
    }
    
    @Override
    public long edges() {
        long summ=0;
        for (FileVertex v:getAllVertex())
            summ+=v.adjacencies.size();
        return summ;
    }

    @Override
    public void trimToSize() {
        try {
            for (FileVertex v:getAllVertex()) v.adjacencies.flush();
        } catch (IOException ex) {
            throw new RuntimeException("When flush graph",ex);
        }
    }
    
    public void clear() {
        try {
            for (FileVertex v:getAllVertex()) v.clear();
        } catch (IOException ex) {
            throw new RuntimeException("Can not clear graph",ex);
        }
    }

    /* todo toString() using for MBeans. It should be unical as super.toString() without data
    @Override
    public String toString() {
        return "FileGraph{" + GraphUtil.toString(this) + '}';
    }*/

    @Override
    public IPathFinder<T> pathFinder() {
        return new FDijkstraPathFind<>(this);
    }

    @Override
    public void close() throws IOException {
        try {
            for (FileVertex v:getAllVertex()) v.close();
        } catch (IOException ex) {
            throw new RuntimeException("Can not clear graph",ex);
        } finally {
            try { // debug MBean
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
                mbs.unregisterMBean(new ObjectName("util.simplegraph:type=onFile-"+getName()));
            } catch (InstanceNotFoundException | MalformedObjectNameException | MBeanRegistrationException ex) {
                //log.warn("Error unregister MBean", ex);
            }
        }
    }
    
    // MBean debug interface
    @Override
    public String getName() {
        return this.toString();
    }
    @Override
    public String getWorkPath() {
        return workPath.toString();
    }
    @Override
    public int getVertices() {
        return vertices();
    }
    @Override
    public long getEdges() {
        return edges();//todo concurrent ?
    }
    @Override
    public int getMaxOpenIO() {
        return MAX_OPEN_FILE;
    }
    @Override
    public int getOpenIO() {
        return EdgeHolder.openIOList.size();
    }
    @Override
    public long getIOSequenceID() {
        return EdgeHolder.ioSequenceAllId;
    }
}
