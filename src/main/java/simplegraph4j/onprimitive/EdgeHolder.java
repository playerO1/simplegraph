package simplegraph4j.onprimitive;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;


/**
 * Low mempry edge holder, based on primitive.
 * Max item: Integer.MAX_VALUES
 * ~12 bytes per 1 edge
 * But required external edge int-Object mapping.
 */
public class EdgeHolder {
    private final TIntArrayList targets; // vertex
    private final TDoubleArrayList weights;

    public EdgeHolder() {
        targets=new TIntArrayList();
        weights=new TDoubleArrayList();
    }
    
    public void addEdge(int toVertex, double weight) {
        targets.add(toVertex);
        weights.add(weight);
    }
    
    public int getTarget(int edgeId) {
        return targets.get(edgeId);
    }
    public double getWeight(int edgeId) {
        return weights.get(edgeId);
    }

    
    public int size() {
        assert targets.size()==weights.size();
        return targets.size();
    }
    
    public void clear() {
        targets.clear();
        weights.clear();
    }
    
    public void trimToSize() {
        targets.trimToSize();
        weights.trimToSize();
    }
}
