package simplegraph4j.onobject;

import simplegraph4j.onobject.SimpleGraph;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.IPathFinder;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simplegraph4j.SimpleGraphConfig;
import simplegraph4j.exception.PathNotFoundException;
import simplegraph4j.util.GraphUtil;
/**
 *
 * @author A.K.
 */
public class SimpleGraphTest {
    //todo most of test cone was ame between PrimitiveGraph, SimpleGraph, ileGraph. Maybe refactor test for share code using?
    
    public SimpleGraphTest() {
    }
    
    @Test
    public void makeGraphTest() {
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);
        
        assertEquals(3, graph.vertices());
        assertEquals(5, graph.edges());
        assertEquals(graph.vertices(), graph.getAllVertex().size());
        assertNotNull(graph.vertexForObejct("A"));
        assertEquals("A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph));
    }

    @Test
    public void makeGraphCheckConfigTest() {
        boolean lastConfigValue=SimpleGraphConfig.isAllowAutoAddVertex();
        try {
            ISimpleGraph<String> graph=new SimpleGraph<>();
            assertEquals(0, graph.vertices());
            SimpleGraphConfig.setAllowAutoAddVertex(true);
            graph.addEdge("A", "B", 1.0); // from, to, w
            graph.addEdge("A", "B", 1.0);
            assertEquals(2, graph.vertices());
            assertEquals(2, graph.edges());
            assertNotNull(graph.vertexForObejct("A"));
            assertNotNull(graph.vertexForObejct("B"));
            assertNull(graph.vertexForObejct("C"));

            SimpleGraphConfig.setAllowAutoAddVertex(false);
            try {
                graph.addEdge("A", "C", 15.4);
                fail("Expected exception");
            } catch (IllegalArgumentException expected) {
                assertEquals("Vertex not found: C", expected.getMessage());
            }
            try {
                graph.addEdge("C", "B", 6.0);
                fail("Expected exception");
            } catch (IllegalArgumentException expected) {
                assertEquals("Vertex not found: C", expected.getMessage());
            }
            // graph structure must be not modify:
            assertNotNull(graph.vertexForObejct("A"));
            assertNotNull(graph.vertexForObejct("B"));
            assertNull(graph.vertexForObejct("C"));
            assertEquals(2, graph.vertices());
            assertEquals(2, graph.edges());
        } finally {
            SimpleGraphConfig.setAllowAutoAddVertex(lastConfigValue);
        }
    }
    
    @Test
    public void findPathTest() throws PathNotFoundException{
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);
        
        IPathFinder<String> pathF=graph.pathFinder();
        List<String> path=pathF.computePath("A","C");
        assertEquals(3, path.size());
        assertEquals(1.0+4.32, pathF.getPathLength(), 0.0000001);
        assertEquals("[A, B, C]",path.toString()); // String check it is not best solution, but fast and human readable.
    }
    
    
    @Test
    public void pathFinder2Test() {
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);

        IPathFinder<String> pathF=graph.pathFinder();
            
        assertEquals(3, pathF.incomeEdgeCount("B"));
        assertEquals(1, pathF.outcomeEdgeCount("B"));
    }
}
