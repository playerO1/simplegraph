package simplegraph4j;

import java.util.Collection;

/**
 * The shared graph interface, API
 * @author A.K. = github.com/playero1
 */
public interface ISimpleGraph<T> {
    /**
     * Register new vertex on this graph. Do not double call with same "name" object.
     * @param name External object, associated with vertex. Vertex name.
     * @return Graph vertex "native" implementation. Youcan skip returnvalue.
     */
    IVertex<T> addVertex(T name);
    /**
     * 
     * @param obj External object, vertex name.
     * @return Graph vertex, associated with <code>obj</code>. Or null.
     */
    public IVertex<T> vertexForObejct(T obj);
    //connect
    /**
     * Connect 2 vertex by directed link.
     * @see IVertex#addEdge(java.lang.Object, double) 
     * @param from
     * @param to
     * @param weight 
     */
    void addEdge(T from, T to, double weight);

    public Collection<? extends IVertex<T>> getAllVertex();
    /**
     * @return Number of all vertex in graph.
     */
    public int vertices();
    /**
     * @return Number of all edges in graph.
     */
    public long edges();

    /**
     * Action interface. Provide math method with this graph.
     * @return new IPathFinder.
     */
    IPathFinder<T> pathFinder();
}
