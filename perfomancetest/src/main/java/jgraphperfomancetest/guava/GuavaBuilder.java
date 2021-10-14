package jgraphperfomancetest.guava;

import jgraphperfomancetest.GraphBuilder;

//import com.google.common.graph.ValueGraphBuilder;
import com.google.common.graph.*;

/*
<dependency>
  <groupId>com.google.guava</groupId>
  <artifactId>guava</artifactId>
  <version>30.1.1-jre</version>
  <!-- or, for Android:
  <version>30.1.1-android</version> -->
</dependency>
*/
/**
 * tip: Guava has a lot of check when build graph. For example not allow self-loop edge by default.
 * todo see https://programmer.ink/think/the-shortest-path-problem-dijkstra-algorithm.html
 * @author github.com/playero1 (A.K.)
 */
public class GuavaBuilder extends GraphBuilder<MutableValueGraph>{ // todo MutableValueGraph<String, Double>
    //protected T graph;
    
    // --- constructor ---
    protected void init() {
        ValueGraphBuilder gBuilder=ValueGraphBuilder.directed();
        gBuilder.allowsSelfLoops(true);//fixme why not work? for "IllegalArgumentException: Cannot add self-loop edge on node v67, as self-loops are not allowed."
        graph = gBuilder.build();
    }
    
    //abstract protected void addVertex(int n);
    
    protected void addEdge(int fromV, int toV, double weight) {
        //weightedGraph.addNode(vertexName(fromV));
        if (fromV==toV) return; // fixme not allow self-loop.
        graph.putEdgeValue(vertexName(fromV), vertexName(toV), weight);
    }
    
    protected void finish() {
        // nothing
    }
    
}
