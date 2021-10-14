package jgraphperfomancetest.hipster4j;

import java.util.List;
import jgraphperfomancetest.GraphBuilder;
import jgraphperfomancetest.GraphTest;
import es.usc.citius.hipster.graph.HipsterDirectedGraph;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.problem.SearchProblem;
/**
 * @author github.com/playero1 (A.K.)
 */
public class Hipster4jTester extends GraphTest<HipsterDirectedGraph<String,Double>> {
    Object thePath; // Algorithm.SearchResult
    public Hipster4jTester(GraphBuilder<HipsterDirectedGraph<String,Double>> builder) {
        super(builder);
    }
 
    protected void findPath(HipsterDirectedGraph<String,Double> graph) {
        SearchProblem p = GraphSearchProblem
                           .startingFrom(vertex1)
                           .in(graph)
                           .takeCostsFromEdges()
                           .build();
        thePath=Hipster.createDijkstra(p).search(vertex2);
    }
    protected String getPath() {
        String str=String.valueOf(thePath);
        thePath=null;
        return str;
    }
}
