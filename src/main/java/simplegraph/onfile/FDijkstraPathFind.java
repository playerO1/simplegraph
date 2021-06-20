package simplegraph.onfile;

import java.io.IOException;
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
public class FDijkstraPathFind<T> implements IPathFinder<T>{
    //static final org.slf4j.Logger log = LoggerFactory.getLogger(FDijkstraPathFind.class);
    
    public final FileGraph<T> graph;
    protected boolean pathFound=false; // for Dijstra temp variable in use
    protected double lastLength=Double.NaN;

    public FDijkstraPathFind(FileGraph<T> graph) {
        this.graph = graph;
    }
    
    @Override
    public List<T> computePath(T source, T target) throws PathNotFoundException {
        FileVertex a=graph.vertexForObejct(source);
        FileVertex b=graph.vertexForObejct(target);
        List<FileVertex> vPath=computePath(a,b);
        List<T> result=new ArrayList<>(vPath.size());
        for (FileVertex v:vPath) result.add((T)v.name);
        return result;
    }
    
    private void resetGraphPathData() {
        if (pathFound) for (FileVertex v:graph.getAllVertex()) { // clear
            v.minDistance = Double.POSITIVE_INFINITY;
            v.previous = null;
        }
        pathFound=true;
    }
    
//    @Override
    public List<FileVertex> computePath(FileVertex source, FileVertex target) throws PathNotFoundException {
        resetGraphPathData();
        computePaths(source);
        if (!source.equals(target) && target.previous==null)
            throw new PathNotFoundException();
        lastLength = target.minDistance;
        return getShortestPathTo(target);
    }
    
    public void computeAllPath(T from, PathHandle<T> pathVariantHandle) throws PathNotFoundException{
        resetGraphPathData();
        long logTime=System.currentTimeMillis();
        
        FileVertex a=graph.vertexForObejct(from);
        computePaths(a);
        int pathCount=0;
        for (FileVertex b:graph.indexObjectToId.values()) {
            if (b.previous==null) continue; // skeep variant
            pathCount++;
            List<FileVertex> vPath= getShortestPathTo(b);
            List<T> result=new ArrayList<>(vPath.size());
            for (FileVertex v:vPath) result.add((T)v.name);
            lastLength = b.minDistance;
            pathVariantHandle.pathVariant(result, b.minDistance);
            
            if (System.currentTimeMillis()-logTime>60000) {
                //log.debug("Dijkstra vertex {} length {}", pathCount, lastLength);
                logTime=System.currentTimeMillis();
            }
        }
        if (pathCount<=1) throw new PathNotFoundException();
    }
    
 // Dijkstra
    // todo: алгоритм Дейкстры не оптимален, для графов с тысячами вершин. рекомендуется использовтаь другие - https://cyberleninka.ru/article/n/obzor-algoritmov-poiska-kratchayshego-puti-v-grafe/viewer
    protected void computePaths(FileVertex source)
    {
        long logTime=System.currentTimeMillis();
        int pathCount=0;

        source.minDistance = 0.;
        final PriorityQueue<FileVertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        try {
            while (!vertexQueue.isEmpty()) {
                final FileVertex<?> u = vertexQueue.poll();
                // Visit each edge exiting u
                EdgeHolder edges=u.adjacencies;
                edges.forEach(//java 1.8:(int targetId, double weight) -> {
                    new EdgeHolder.EdgeVisitor() {
                        @Override
                        public void acceptEdge(int targetId, double weight) {
                    FileVertex v = graph.getVertexById(targetId);
                    double distanceThroughU = u.minDistance + weight;
                    if (distanceThroughU < v.minDistance) {
                        vertexQueue.remove(v);
                        v.minDistance = distanceThroughU;
                        v.previous = u;
                        vertexQueue.add(v);
                    }
                }});
                
                pathCount++;
                lastLength=u.minDistance;
                if (System.currentTimeMillis()-logTime>60000) {
                    //log.debug("Dijkstra vertex {} length {}", pathCount, lastLength);
                    logTime=System.currentTimeMillis();
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected List<FileVertex> getShortestPathTo(FileVertex target) {
        List<FileVertex> path = new ArrayList<>();
        for (FileVertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }
    
    @Override
    public double getPathLength() {
        return lastLength;
    }
}
