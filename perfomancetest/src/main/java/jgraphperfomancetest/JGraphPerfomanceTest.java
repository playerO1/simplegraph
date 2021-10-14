package jgraphperfomancetest;

import jgraphperfomancetest.empty.EmptyBuilder;
import jgraphperfomancetest.empty.EmptyTester;
import jgraphperfomancetest.simplegraph4j.ASimpleGraph4jBuilder;
import jgraphperfomancetest.simplegraph4j.SimpleGraphTester;
import jgraphperfomancetest.hipster4j.Hipster4jBuilder;
import jgraphperfomancetest.hipster4j.Hipster4jTester;
import jgraphperfomancetest.guava.GuavaBuilder;
import jgraphperfomancetest.guava.GuavaTester;
import jgraphperfomancetest.jgrapht.JGraphTBuilder;
import jgraphperfomancetest.jgrapht.JGraphTTester;

/**
 * 
 * @author playerO1 (A.K.)
 */
public class JGraphPerfomanceTest {
    static final int N=10;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int edges=500000, vertex = 500;
        System.out.println("Test case " +vertex+" vertex, "+edges+" edges.");
        {
            System.out.println("Test 1. Empty.");
            EmptyBuilder builder=new EmptyBuilder();
            builder.setEdges(edges);
            builder.setVertex(vertex);
            EmptyTester test=new EmptyTester(builder);
            test.selfTest();
            for (int i=0;i<N;i++) test.perfomanceTest();
            for (int i=0;i<N;i++) test.memoryTest();
        }
        
        {
            System.out.println("Test 2. SimpleGraph.");
            GraphBuilder builder=new ASimpleGraph4jBuilder();
            builder.setEdges(edges);
            builder.setVertex(vertex);
            GraphTest test=new SimpleGraphTester(builder);
            for (int i=0;i<N;i++) test.perfomanceTest();
            for (int i=0;i<N;i++) test.memoryTest();
        }
        
        {
            System.out.println("Test 3. Hipster4J.");
            GraphBuilder builder=new Hipster4jBuilder();
            builder.setEdges(edges);
            builder.setVertex(vertex);
            GraphTest test=new Hipster4jTester(builder);
            for (int i=0;i<N;i++) test.perfomanceTest();
            for (int i=0;i<N;i++) test.memoryTest();
        }
        
        
        {//tood guava test - no self-loop edge, not path find available. How do it? Or it is not support?
            System.out.println("Test 4. Guava.");
            GraphBuilder builder=new GuavaBuilder();
            builder.setEdges(edges);
            builder.setVertex(vertex);
            GraphTest test=new GuavaTester(builder);
            for (int i=0;i<N;i++) test.perfomanceTest();
            for (int i=0;i<N;i++) test.memoryTest();
        }
        {//todo jgrapht test - not true Djikstra test - without edge weight.
            System.out.println("Test 5. JGraphT.");
            GraphBuilder builder=new JGraphTBuilder();
            builder.setEdges(edges);
            builder.setVertex(vertex);
            GraphTest test=new JGraphTTester(builder);
            for (int i=0;i<N;i++) test.perfomanceTest();
            for (int i=0;i<N;i++) test.memoryTest();
        }
        
    }
    
}
