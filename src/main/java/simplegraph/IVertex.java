package simplegraph;

/**
 * Вершина
 */
public interface IVertex<T> {
    T getName();
    Iterable<? extends IEdge<T>> getAdjacencies(); // edges
    
    void addEdge(T to, double weight);
}
