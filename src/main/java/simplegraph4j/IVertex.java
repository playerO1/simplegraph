package simplegraph4j;

/**
 * Вершина
 */
public interface IVertex<T> {
    /**
     * @return Linked external object, associated with this vertex.
     */
    T getName();
    Iterable<? extends IEdge<T>> getAdjacencies(); // edges
    long edgesCount(); // todo maybe wrap getAdjacencies onto List?

    /**
     * More fast for add batch edges from single vertex.
     * @see ISimpleGraph#addEdge(java.lang.Object, java.lang.Object, double) 
     * @param to
     * @param weight
     */
    void addEdge(T to, double weight);
}
