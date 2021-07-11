package simplegraph4j.util;

import simplegraph4j.util.GraphUtil;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.onobject.SimpleGraph;

/**
 * @author A.K.
 */
public class GraphUtilTest {
    
    public GraphUtilTest() {}

    private ISimpleGraph<String> makeTestGraph() {
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);
        return graph;
    }

    @Test
    public void testToString_ISimpleGraph() {
        ISimpleGraph<String> graph=makeTestGraph();
        assertEquals("A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph));
    }

    @Test
    public void testToString_ISimpleGraph_int() {
        ISimpleGraph<String> graph=makeTestGraph();
        assertEquals("A[B=1.0; B=1.0; C=15...", GraphUtil.toString(graph, 23));//fixme wherrr is ...?
    }

    @Test
    public void testAppendGraph() {
        ISimpleGraph<String> graph1=makeTestGraph();
        ISimpleGraph<String> graph2=new SimpleGraph<>();
        GraphUtil.appendGraph(graph1, graph2);
        assertEquals("A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph2));
        
        // double add
        GraphUtil.appendGraph(graph1, graph2);
        assertEquals("A[B=1.0; B=1.0; C=15.4; B=1.0; B=1.0; C=15.4]; B[C=4.32; C=4.32]; C[B=6.0; B=6.0]", GraphUtil.toString(graph2));
    }

    @Test
    public void testHashCode() {
        ISimpleGraph<String> graph=makeTestGraph();
        assertEquals(-1589270799, GraphUtil.hashCode(graph));
        ISimpleGraph<String> graph2=makeTestGraph();
        assertEquals(GraphUtil.hashCode(graph), GraphUtil.hashCode(graph2)); // should determinate only by data and structure, do not different by Object memory if data is same.
    }

}
