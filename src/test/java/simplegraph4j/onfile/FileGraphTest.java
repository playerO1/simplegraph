package simplegraph4j.onfile;

import simplegraph4j.onfile.FileGraph;
import simplegraph4j.IPathFinder;
import java.util.List;
import java.io.File;
import java.io.IOException;
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
 * @author A.K. = playerO1@github.com
 */
public class FileGraphTest {
    static File tempPath;
    
    public FileGraphTest() {
    }
    
    @BeforeClass // todo should be use BeforeEach/AfterEach and true clean temp directory?
    public static void setUpClass() {
        tempPath=new File("target/tmp_testpath");
        if (!tempPath.isDirectory()) {
          if (!tempPath.mkdir()) throw new RuntimeException("Can not make temp directory "+tempPath);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (tempPath.isDirectory()) {
          // todo clear directory. Maven "mvn clean" should do clean target/* too.
        }
    }

    @Test
    public void makeGraphTest() throws IOException{
        FileGraph<String> graph=new FileGraph<>(tempPath);
        assertTrue("Not exist directory should be creaded by FileGraph", tempPath.isDirectory());
        try {
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
        
        assertEquals("A[B=1.0; B=1.0; C=15.399992370605469]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph));
        //todo Why only on FileGraphTest C=15.4 <> C=15.399992370605469?
        } finally {
            graph.close();
        }
        assertArrayEquals(new String[0], tempPath.list()); // should clean up SimpleGraphConfig.isAutoClearFileGraphTempFolder()
    }
    
    @Test
    public void makeGraphCheckConfigTest() throws IOException {
        boolean lastConfigValue=SimpleGraphConfig.isAllowAutoAddVertex();
        FileGraph<String> graph=new FileGraph<>(tempPath);
        try {
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
            graph.close();
        }
    }

    
    @Test
    public void findPathTest() throws PathNotFoundException, IOException{
        try (FileGraph<String> graph=new FileGraph<>(tempPath)) { // ISimpleGraph, Cloasable try with resource
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

    @Test
    public void pathFinder2Test() throws PathNotFoundException, IOException{
        try (FileGraph<String> graph=new FileGraph<>(tempPath)) { // ISimpleGraph, Cloasable try with resource
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
    
}
