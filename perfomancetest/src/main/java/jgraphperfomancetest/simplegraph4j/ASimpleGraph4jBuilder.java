package jgraphperfomancetest.simplegraph4j;

import java.util.Random;
import jgraphperfomancetest.GraphBuilder;
import simplegraph4j.onobject.SimpleGraph;
import simplegraph4j.onprimitive.PrimitiveGraph;
import simplegraph4j.onfile.FileGraph;
import simplegraph4j.ISimpleGraph;
import java.io.File;

/*
            <dependency>
                <groupId>com.github.playerO1.simplegraph4j</groupId>
                <artifactId>simplegraph4j</artifactId>
                <version>0.0.3</version>
            </dependency>
*/
/**
 *
 * @author github.com/playero1 (A.K.)
 */
public  class ASimpleGraph4jBuilder extends GraphBuilder<ISimpleGraph<String>>{
    //protected T graph;
    
    // --- constructor ---
    protected void init() {
        graph = new PrimitiveGraph<>(); 
        //graph = new SimpleGraph<String>();
//        try { 
//            graph = new FileGraph<>(new File("tmp1"));
//        } catch (Exception e) {// IOException
//            throw new RuntimeException(e);
//        }
    }
    
    //abstract protected void addVertex(int n);
    
    protected void addEdge(int fromV, int toV, double weight) {
        graph.addEdge(vertexName(fromV), vertexName(toV), weight);
    }
    
    protected void finish() {
        //todo verify it is realy count of edges/vertex?
        if (graph.vertices()!=vertex) 
            throw new IllegalStateException("grapg has "+graph.vertices()+" vertex expected "+vertex);
        if (graph.edges()!=edges) 
            throw new IllegalStateException("grapg has "+graph.edges()+" edges expected "+edges);
    }
    
}
