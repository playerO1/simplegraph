package jgraphperfomancetest.hipster4j;

import jgraphperfomancetest.GraphBuilder;

//import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;

/*
        <dependency>
            <groupId>es.usc.citius.hipster</groupId>
            <artifactId>hipster-core</artifactId> <!-- hipster-all -->
            <version>1.0.1</version>
        </dependency>
*/

/**
 *
 * @author github.com/playero1 (A.K.)
 */
public class Hipster4jBuilder extends GraphBuilder<HipsterDirectedGraph<String,Double>>{
    //protected T graph;
    protected es.usc.citius.hipster.graph.GraphBuilder<String,Double> hBuilder;
    // todo Hipster builder too slow. Need using directed call of HipsterDirectedGraph
    
    // --- constructor ---
    protected void init() {
       hBuilder= es.usc.citius.hipster.graph.GraphBuilder.<String,Double>create();
       //graph = new HipsterDirectedGraph<String,Double>();
    }
    
    //abstract protected void addVertex(int n);
    
    protected void addEdge(int fromV, int toV, double weight) {
        hBuilder.connect(vertexName(fromV)).to(vertexName(toV)).withEdge(weight);
    }
    
    protected void finish() {
        graph = hBuilder.createDirectedGraph();
        hBuilder=null;
    }
    
}
