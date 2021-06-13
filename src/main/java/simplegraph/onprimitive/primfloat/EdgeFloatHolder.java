package simplegraph.onprimitive.primfloat;

import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;


/**
 * Low mempry edge holder, based on primitive.
 * Compress double to float for -30% less memory.
 * Max item: Integer.MAX_VALUES
 * ~8 bytes per 1 edge
 * But required external edge int-Object mapping.
 */
public class EdgeFloatHolder {
    private final TIntArrayList targets; // vertex
    private final TFloatArrayList weights;

    public EdgeFloatHolder() {
        targets=new TIntArrayList();
        weights=new TFloatArrayList();
    }
    
    public void addEdge(int toVertex, float weight) {
        targets.add(toVertex);
        weights.add(weight);
    }
    
    public int getTarget(int edgeId) {
        return targets.get(edgeId);
    }
    public float getWeight(int edgeId) {
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
