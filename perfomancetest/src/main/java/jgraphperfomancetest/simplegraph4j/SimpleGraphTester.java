package jgraphperfomancetest.simplegraph4j;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgraphperfomancetest.GraphBuilder;
import jgraphperfomancetest.GraphTest;
import simplegraph4j.IPathFinder;
import simplegraph4j.exception.PathNotFoundException;
import simplegraph4j.ISimpleGraph;

/**
 * @author github.com/playero1 (A.K.)
 */
public class SimpleGraphTester extends GraphTest<ISimpleGraph> {
    List<String> thePath;
    public SimpleGraphTester(GraphBuilder<ISimpleGraph> builder) {
        super(builder);
    }
 
    protected void findPath(ISimpleGraph graph) {
        try {
            IPathFinder<String> pf=graph.pathFinder();
            thePath=pf.computePath(vertex1, vertex2);
        } catch (PathNotFoundException ex) {
            //ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    protected String getPath() {
        String str=String.valueOf(thePath);
        thePath=null;
        return str;
    }
}
