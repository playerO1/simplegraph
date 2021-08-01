package simplegraph4j.onobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import simplegraph4j.IPathFinder;
import simplegraph4j.IVertex;
import simplegraph4j.PathHandle;
import simplegraph4j.exception.PathNotFoundException;

/**
 * 
 * Based on  https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map
 * @author A.K. and sample code from stackowerflow
 */
public class DijkstraPathFind<T> implements IPathFinder<T>{
    public final SimpleGraph<T> graph;
    boolean pathFound=false; // for Dijstra temp variable in use
    protected double lastLength=Double.NaN;

    public DijkstraPathFind(SimpleGraph<T> graph) {
        this.graph = graph;
    }
    
    @Override
    public List<T> computePath(T source, T target) throws PathNotFoundException {
        Vertex a=graph.vertexForObejct(source);
        Vertex b=graph.vertexForObejct(target);
        List<Vertex> vPath= computePath(a,b);
        List<T> result=new ArrayList<>(vPath.size());
        for (Vertex v:vPath) result.add((T)v.name);
        return result;
    }
    
    private void resetGraphPathData() {
        if (pathFound) for (Vertex v:graph.getAllVertex()) { // clear
            v.minDistance = Double.POSITIVE_INFINITY;
            v.previous = null;
        }
        pathFound=true;
    }
    
    //@Override
    public List<Vertex> computePath(Vertex source, Vertex target) throws PathNotFoundException {
        resetGraphPathData();
        computePaths(source);
        if (!source.equals(target) && target.previous==null)
            throw new PathNotFoundException();
        lastLength = target.minDistance;
        return getShortestPathTo(target);
    }
    
    public void computeAllPath(T from, PathHandle<T> pathVariantHandle) throws PathNotFoundException{
        resetGraphPathData();
        
        Vertex a=graph.vertexForObejct(from);
        computePaths(a);
        int pathCount=0;
        for (Vertex b:graph.indexObjectToId.values()) {
            if (b.previous==null) continue; // skeep variant
            pathCount++;
            List<Vertex> vPath= getShortestPathTo(b);
            List<T> result=new ArrayList<>(vPath.size());
            for (Vertex v:vPath) result.add((T)v.name);
            lastLength = b.minDistance;
            pathVariantHandle.pathVariant(result, b.minDistance);
        }
        if (pathCount<=1) throw new PathNotFoundException();
    }
    
 // Dijkstra
    // алгоритм Дейкстры не оптимален, для графов с тысячами вершин. рекомендуется использовтаь другие - https://cyberleninka.ru/article/n/obzor-algoritmov-poiska-kratchayshego-puti-v-grafe/viewer
    protected void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);
        while (!vertexQueue.isEmpty()) {
            Vertex<?> u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) {
                Vertex v = e.target;
                double weight = e.weight;
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

    protected List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    @Override
    public double getPathLength() {
        return lastLength;
    }
    
    
    @Override
    public long incomeEdgeCount(T to) {
        final Vertex<T> toV=graph.vertexForObejct(to);
        if (toV==null) throw new IllegalArgumentException("Vertex not foundfor: "+to);
        long counter=0;
        for (Vertex<T> v:graph.getAllVertex()) {
            for (Edge e : v.adjacencies) {
                if (toV==e.target)
                    counter++;
            }
        }
        return counter;
    }
    @Override
    public long outcomeEdgeCount(T from) {
        IVertex<T> v=graph.vertexForObejct(from);
        if (v==null) return 0;
        return v.edgesCount();
    }
}
