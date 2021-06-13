package simplegraph;

import java.util.Collection;

/**
 * The shared graph interface, API
 * @author A.K. = github.com/playero1
 */
public interface ISimpleGraph<T> {
    void addVertex(T name);
    public IVertex<T> vertexForObejct(T obj);
    //connect
    //void addEdge(T vertex1, T vertex2, double veight);
    //void addEdge(IVertex<T> vertex1, IVertex<T> vertex2, double veight);
    void addEdge(T from, T to, double weight);

    public Collection<? extends IVertex<T>> getAllVertex();
    public int vertices();
    public long edges();

    IPathFinder<T> pathFinder();
    
    //TODO @Override public abstract String toString();
}
