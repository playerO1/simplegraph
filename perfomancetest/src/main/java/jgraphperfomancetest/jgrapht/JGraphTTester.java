package jgraphperfomancetest.jgrapht;

import jgraphperfomancetest.GraphBuilder;
import jgraphperfomancetest.GraphTest;

import org.jgrapht.*;
import org.jgrapht.alg.connectivity.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.graph.*;

/**
 * //todo see https://www.programcreek.com/java-api-examples/?api=org.jgrapht.alg.DijkstraShortestPath
 * @author github.com/playero1 (A.K.)
 */
public class JGraphTTester extends GraphTest<Graph> {
    GraphPath thePath;
    public JGraphTTester(GraphBuilder<Graph> builder) {
        super(builder);
    }
 
    protected void findPath(Graph graph) {
        thePath= DijkstraShortestPath.findPathBetween(graph, vertex1, vertex2);
    }
    protected String getPath() {
        String str=String.valueOf(thePath);
        thePath=null;
        return str;
    }
}
