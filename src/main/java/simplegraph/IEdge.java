package simplegraph;

/**
 * Ребро
 */
public interface IEdge<T> {
    IVertex<T> getTarget();
    double getWeight();
}
