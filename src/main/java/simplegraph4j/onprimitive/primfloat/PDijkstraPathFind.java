package simplegraph4j.onprimitive.primfloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import simplegraph4j.IPathFinder;
import simplegraph4j.PathHandle;
import simplegraph4j.exception.PathNotFoundException;

/**
 * 
 * Based on  https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 * @author A.K. and part of code from stackoverflow.com
 */
public class PDijkstraPathFind<T> implements IPathFinder<T>{
    public final PrimitiveGraph<T> graph;
    protected boolean pathFound=false; // for Dijstra temp variable in use
    protected double lastLength=Double.NaN;

    public PDijkstraPathFind(PrimitiveGraph<T> graph) {
        this.graph = graph;
    }
    
    @Override
    public List<T> computePath(T source, T target) throws PathNotFoundException {
        PrimitiveFloatVertex a=graph.vertexForObejct(source);
        PrimitiveFloatVertex b=graph.vertexForObejct(target);
        List<PrimitiveFloatVertex> vPath= computePath(a,b);
        List<T> result=new ArrayList<>(vPath.size());
        for (PrimitiveFloatVertex v:vPath) result.add((T)v.name);
        return result;
    }
    
    private void resetGraphPathData() {
        if (pathFound) for (PrimitiveFloatVertex v:graph.getAllVertex()) { // clear
            v.minDistance = Float.POSITIVE_INFINITY;
            v.previous = null;
        }
        pathFound=true;
    }
    
//    @Override
    public List<PrimitiveFloatVertex> computePath(PrimitiveFloatVertex source, PrimitiveFloatVertex target) throws PathNotFoundException {
        resetGraphPathData();
        computePaths(source);
        if (!source.equals(target) && target.previous==null)
            throw new PathNotFoundException();
        lastLength = target.minDistance;
        return getShortestPathTo(target);
    }
    
    public void computeAllPath(T from, PathHandle<T> pathVariantHandle) throws PathNotFoundException{
        resetGraphPathData();
        
        PrimitiveFloatVertex a=graph.vertexForObejct(from);
        computePaths(a);
        int pathCount=0;
        for (PrimitiveFloatVertex b:graph.indexObjectToId.values()) {
            if (b.previous==null) continue; // skeep variant
            pathCount++;
            List<PrimitiveFloatVertex> vPath= getShortestPathTo(b);
            List<T> result=new ArrayList<>(vPath.size());
            for (PrimitiveFloatVertex v:vPath) result.add((T)v.name);
            lastLength = b.minDistance;
            pathVariantHandle.pathVariant(result, b.minDistance);
        }
        if (pathCount<=1) throw new PathNotFoundException();
    }
    
 // Dijkstra
    protected void computePaths(PrimitiveFloatVertex source)
    {
        source.minDistance = 0.f;
        PriorityQueue<PrimitiveFloatVertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        while (!vertexQueue.isEmpty()) {
            PrimitiveFloatVertex<?> u = vertexQueue.poll();

            // Visit each edge exiting u
            EdgeFloatHolder edges=u.adjacencies;
            for (int i=0;i<edges.size();i++) {
                int targetId=edges.getTarget(i);
                PrimitiveFloatVertex v = graph.getVertexById(targetId);
                float weight = edges.getWeight(i);
                float distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    protected List<PrimitiveFloatVertex> getShortestPathTo(PrimitiveFloatVertex target) {
        List<PrimitiveFloatVertex> path = new ArrayList<>();
        for (PrimitiveFloatVertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    @Override
    public double getPathLength() {
        return lastLength;
    }
}
