package simplegraph4j.onprimitive;

import simplegraph4j.onprimitive.PrimitiveGraph;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.IPathFinder;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import simplegraph4j.exception.PathNotFoundException;

/**
 *
 * @author A.K. = playerO1@github.com
 */
public class PrimitiveGraphTest {
    
    public PrimitiveGraphTest() {
    }
    

    @Test
    public void makeGraphTest() {
        ISimpleGraph<String> graph=new PrimitiveGraph<>();
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
        //TODO toString check
    }
    
    @Test
    public void findPathTest() throws PathNotFoundException{
        ISimpleGraph<String> graph=new PrimitiveGraph<>();
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
    
}
