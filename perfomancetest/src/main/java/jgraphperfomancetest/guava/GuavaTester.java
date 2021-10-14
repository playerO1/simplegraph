package jgraphperfomancetest.guava;

import java.util.List;
import jgraphperfomancetest.GraphBuilder;
import jgraphperfomancetest.GraphTest;
/**
 * @author github.com/playero1 (A.K.)
 */
public class GuavaTester extends GraphTest<Object> { // todo MutableValueGraph<String, Double>
    Object thePath; //?
    public GuavaTester(GraphBuilder<Object> builder) { // todo MutableValueGraph<String, Double>
        super(builder);
    }
 
    protected void findPath(Object graph) {
        // Traversing a directed graph
        //todo Where is a default implementation path find for Guava?
        
    }
    protected String getPath() {
        String str=String.valueOf(thePath);
        thePath=null;
        return str;
    }
}
