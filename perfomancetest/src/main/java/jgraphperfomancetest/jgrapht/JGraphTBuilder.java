package jgraphperfomancetest.jgrapht;

import jgraphperfomancetest.GraphBuilder;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;
/*
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-core</artifactId>
    <version>1.5.1</version>
</dependency>
*/
/**
 * See example on https://github.com/jgrapht/jgrapht/blob/master/jgrapht-demo/src/main/java/org/jgrapht/demo/DirectedGraphDemo.java
 * @author github.com/playero1 (A.K.)
 */
public  class JGraphTBuilder extends GraphBuilder<Graph<String, DefaultEdge>>{
//    protected int vertex;
  //  protected int edges;
    //protected T graph;
    
    // --- constructor ---
    protected void init() {
        graph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
//new SimpleGraph<>(); // DefaultWeightedEdge.class or DefaultEdge.class
        // todo read more about diferent types
        for (int i=0;i<vertex;i++) graph.addVertex(vertexName(i));
    }
    
    //abstract protected void addVertex(int n);
    
    protected void addEdge(int fromV, int toV, double weight) {
        String from=vertexName(fromV), to=vertexName(toV);
        //graph.addVertex(from);
        //graph.addVertex(to);
        graph.addEdge(from, to); // todo +weight
    }
    
    protected void finish() {
        //todo првоерить размер графа
    }
    
}
