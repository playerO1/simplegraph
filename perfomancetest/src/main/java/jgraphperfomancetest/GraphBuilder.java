package jgraphperfomancetest;

import java.util.Random;

/**
 *
 * @author github.com/playero1 (A.K.)
 */
public abstract class GraphBuilder<T> {
    protected int vertex;
    protected int edges;
    protected T graph;
    protected Random rand;
    

    public int getVertex() {
        return vertex;
    }

    public void setVertex(int vertex) {
        this.vertex = vertex;
    }

    public int getEdges() {
        return edges;
    }

    public void setEdges(int edges) {
        this.edges = edges;
    }
    
    // --- constructor ---
    abstract protected void init(); // graph=new
    
    //abstract protected void addVertex(int n);
    
    abstract protected void addEdge(int fromV, int toV, double weight);
    
    protected void finish() {
        //todo verify it is realy count of edges/vertex?
    }
    
    public String vertexName(int i) {
        return "v"+i;
    }
    
    public T build() {
        rand=new Random(0);
        init();
        
//        for (int i=0;i<edges; i++) {
//            addEdge(rand.nextInt(vertex), rand.nextInt(vertex), rand.nextDouble()*100.123+400.0);
//        }
//        if (graph.vertex()<vertex) for (int i=graph.vertex()<;i<vertex; i++) addVertex(i);

        if (edges<vertex) throw new IllegalArgumentException("edges "+edges+" < vertex "+vertex);
        for (int i=0;i<=edges-vertex; i++) {
            addEdge(rand.nextInt(vertex), rand.nextInt(vertex), rand.nextDouble()*100.123+400.0);
        }
        for (int i=1;i<vertex; i++) { // path over all graph
            addEdge(i-1, i, rand.nextDouble()*10.123+5000.0);
        }
        
        finish();
        T returnResult = graph;
        graph = null; // clear
        return returnResult;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "vertex=" + vertex + ", adges=" + edges + '}';
    }
    
    
}
