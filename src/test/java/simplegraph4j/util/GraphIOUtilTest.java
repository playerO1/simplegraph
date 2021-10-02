package simplegraph4j.util;

import java.io.BufferedReader;
import simplegraph4j.util.GraphUtil;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
import static org.junit.Assert.*;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.onobject.SimpleGraph;

/**
 * @author A.K.
 */
public class GraphIOUtilTest {
    
    public GraphIOUtilTest() {}

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
    public void testWriteTo() throws IOException {
        ISimpleGraph<String> graph=makeTestGraph();
        GraphIOUtil graphIO=new GraphIOUtil();
        StringWriter writer=new StringWriter();
        graphIO.writeGraphTo(writer, graph, new GraphIOUtil.StringAsString());
        String inFile=writer.toString();
        String expected=("A\tB\t1.0\n"
                + "A\tB\t1.0\n"
                + "A\tC\t15.4\n"
                + "B\tC\t4.32\n"
                + "C\tB\t6.0\n"
                ).replace("\n", graphIO.FILE_NEW_LINE);
        assertFalse(graphIO.FILE_NEW_LINE.isEmpty());
        assertEquals(expected, inFile);
    }

    @Test
    public void testReadFrom() throws IOException {
        ISimpleGraph<String> graph=new SimpleGraph<>();
        GraphIOUtil graphIO=new GraphIOUtil();
        StringReader reader=new StringReader("A\tB\t1.0\n"
                + "A\tB\t1.0\n"
                + "A\tC\t15.4\n"
                + "B\tC\t4.32\n"
                + "C\tB\t6.0\n"
                );
        graphIO.readGraphFrom(new BufferedReader(reader), graph, new GraphIOUtil.StringAsString());
        assertEquals("A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph));
    }

    
    @Test
    public void testWriteReadFromFile() throws IOException {
        File tmpFile=new File("target/tmp_graphIO.csv");
        try {
            ISimpleGraph<String> graph=makeTestGraph();
            GraphIOUtil.writeGraphToFile(tmpFile, graph);
            ISimpleGraph<String> graph2=new SimpleGraph<>();
            GraphIOUtil.readGraphFromFile(tmpFile, graph2);
            assertEquals("A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]", GraphUtil.toString(graph2));
            assertEquals(GraphUtil.toString(graph), GraphUtil.toString(graph2));
            assertEquals(GraphUtil.hashCode(graph),GraphUtil.hashCode(graph2));
        } finally{
            if (tmpFile.exists()) {
                tmpFile.delete(); // delete manualy or mvn clean should cleanup temp folder too
            }
        }
    }
}
