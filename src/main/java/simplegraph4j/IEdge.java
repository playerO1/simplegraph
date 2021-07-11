package simplegraph4j;

/**
 * Ребро
 */
public interface IEdge<T> {
    IVertex<T> getTarget();
    double getWeight();
}
