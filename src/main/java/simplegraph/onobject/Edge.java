package simplegraph.onobject;

import simplegraph.IEdge;

/**
 * https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 * 
 * ~28 byte per 1 edge
 */
public class Edge<T> implements IEdge<T>{
    public final Vertex<T> target;
    public final double weight;
    public Edge(Vertex<T> argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }

    public Vertex<T> getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }
    
    
}
