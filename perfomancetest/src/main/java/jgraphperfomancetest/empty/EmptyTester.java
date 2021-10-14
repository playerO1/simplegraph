package jgraphperfomancetest.empty;

import java.util.ArrayList;
import jgraphperfomancetest.simplegraph4j.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jgraphperfomancetest.GraphBuilder;
import jgraphperfomancetest.GraphTest;
import simplegraph4j.IPathFinder;
import simplegraph4j.exception.PathNotFoundException;
import simplegraph4j.onobject.SimpleGraph;

/**
 * @author github.com/playero1 (A.K.)
 */
public class EmptyTester extends GraphTest<Object> {
    List<String> thePath;
    public EmptyTester(GraphBuilder<Object> builder) {
        super(builder);
    }
 
    @Override
    protected void findPath(Object graph) {
        thePath=new ArrayList<>();
    }
    @Override
    protected String getPath() {
        String str=String.valueOf(thePath);
        thePath=null;
        return str;
    }
}
