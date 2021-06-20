package simplegraph.onprimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import simplegraph.IPathFinder;
import simplegraph.PathHandle;
import simplegraph.exception.PathNotFoundException;

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
        PrimitiveVertex a=graph.vertexForObejct(source);
        PrimitiveVertex b=graph.vertexForObejct(target);
        List<PrimitiveVertex> vPath= computePath(a,b);
        List<T> result=new ArrayList<>(vPath.size());
        for (PrimitiveVertex v:vPath) result.add((T)v.name);
        return result;
    }
    
    private void resetGraphPathData() {
        if (pathFound) for (PrimitiveVertex v:graph.getAllVertex()) { // clear
            v.minDistance = Double.POSITIVE_INFINITY;
            v.previous = null;
        }
        pathFound=true;
    }
    
//    @Override
    public List<PrimitiveVertex> computePath(PrimitiveVertex source, PrimitiveVertex target) throws PathNotFoundException {
        resetGraphPathData();
        computePaths(source);
        if (!source.equals(target) && target.previous==null)
            throw new PathNotFoundException();
        lastLength = target.minDistance;
        return getShortestPathTo(target);
    }
    
    public void computeAllPath(T from, PathHandle<T> pathVariantHandle) throws PathNotFoundException{
        resetGraphPathData();
        
        PrimitiveVertex a=graph.vertexForObejct(from);
        computePaths(a);
        int pathCount=0;
        for (PrimitiveVertex b:graph.indexObjectToId.values()) {
            if (b.previous==null) continue; // skeep variant
            pathCount++;
            List<PrimitiveVertex> vPath= getShortestPathTo(b);
            List<T> result=new ArrayList<>(vPath.size());
            for (PrimitiveVertex v:vPath) result.add((T)v.name);
            lastLength = b.minDistance;
            pathVariantHandle.pathVariant(result, b.minDistance);
        }
        if (pathCount<=1) throw new PathNotFoundException();
    }
    
 // Dijkstra
    // todo: алгоритм Дейкстры не оптимален, для графов с тысячами вершин. рекомендуется использовтаь другие - https://cyberleninka.ru/article/n/obzor-algoritmov-poiska-kratchayshego-puti-v-grafe/viewer
    protected void computePaths(PrimitiveVertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<PrimitiveVertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        while (!vertexQueue.isEmpty()) {
            PrimitiveVertex<?> u = vertexQueue.poll();

            // Visit each edge exiting u
            EdgeHolder edges=u.adjacencies;
            for (int i=0;i<edges.size();i++) {
                int targetId=edges.getTarget(i);
                PrimitiveVertex v = graph.getVertexById(targetId);
                double weight = edges.getWeight(i);
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    protected List<PrimitiveVertex> getShortestPathTo(PrimitiveVertex target) {
        List<PrimitiveVertex> path = new ArrayList<>();
        for (PrimitiveVertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }
    
    @Override
    public double getPathLength() {
        return lastLength;
    }
}
