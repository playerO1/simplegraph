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
import simplegraph4j.exception.PathNotFoundException;

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
        //graph.close(); fixme assertion error, cause temp path has not clean after previous test.
    }
    
    @Test
    public void findPathTest() throws PathNotFoundException, IOException{
        FileGraph<String> graph=new FileGraph<>(tempPath); // ISimpleGraph
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
        graph.close();
    }
    
}
