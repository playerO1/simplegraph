package jgraphperfomancetest.empty;

import jgraphperfomancetest.simplegraph4j.*;
import java.util.Random;
import jgraphperfomancetest.GraphBuilder;
import simplegraph4j.onobject.SimpleGraph;

/**
 *
 * @author github.com/playero1 (A.K.)
 */
public  class EmptyBuilder extends GraphBuilder<Object>{
    // --- constructor ---
    protected void init() {
        graph = new Object();
    }
    
    //abstract protected void addVertex(int n);
    
    protected void addEdge(int fromV, int toV, double weight) {
        vertexName(fromV);
        vertexName(toV);
    }
    
    protected void finish() {
    }
    
}
