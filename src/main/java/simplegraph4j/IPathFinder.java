package simplegraph4j;

import java.util.List;
import simplegraph4j.onobject.Vertex;
import simplegraph4j.exception.PathNotFoundException;

/**
 * Method for find optimal path
 * @author A.K. = github.com/playero1
 */
public interface IPathFinder<T> {
    //List<? extends IVertex> computePath(IVertex from, IVertex to) throws PathNotFoundException;
    List<T> computePath(T from, T to) throws PathNotFoundException;
    double getPathLength(); // возврщает длину последнего найденного пути (computePath или computeAllPath->pathVariantHandle)
    
    /**
     * Watch all path from A to all another point.
     * You can use it for same satisfaction experiment.
     * 
     * @param from first initiation point
     * @param pathVariantHandle watch all possible path to all another linked point
     * @throws PathNotFoundException if no one path found - this point has noany link
     */
    void computeAllPath(T from, PathHandle<T> pathVariantHandle) throws PathNotFoundException;
    
    long incomeEdgeCount(T to);
    long outcomeEdgeCount(T from);
}
