package jgraphperfomancetest;

/**
 * Measure memory and CPU time for build and work with graph.
 * todo: wrong decomposition. 
 * GraphTest should not depends from T and do not be abstract. T must be only in builder, IGraphAPI...
 * 
 * @author github.com/playero1 (A.K.)
 */
public abstract class GraphTest<T> {
    public GraphBuilder<T> builder;
    public int n=10;
    public long time1,time2,time3,time4;
    public long memory1,memory2,memory3;

    public final String vertex1,vertex2;
    //todo public final String allVertexObj[];// for save memory when rebuild vertex object reference

    public GraphTest(GraphBuilder<T> builder) {
        this.builder=builder;
        vertex1=builder.vertexName(0);
        vertex2=builder.vertexName(builder.getVertex()-1);
    }
    
    
    abstract protected void findPath(T graph);
    abstract protected String getPath(); // get and clear

    public void selfTest() {
        System.out.println("Build graph "+builder+", path from "+vertex1+" to "+vertex2);
        T graph=builder.build();
        findPath(graph);
        String path=getPath();
        System.out.println("Path: "+path);
    }
    
    public void perfomanceTest() {
        time1=System.nanoTime();
        T graph=builder.build();
        time2=System.nanoTime();
        findPath(graph);
        time3=System.nanoTime();
        String path=getPath();
        System.out.println("Time ns. Build,find:\t"+(time2-time1)+"\t"+(time3-time2)); // +"\t"+(time4-time3)
    }
/*    
    public void memoryTest() {
        Thread.yield();
        System.gc();
        memory1=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        T graph=builder.build();
        memory2=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        findPath(graph);
        memory3=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String path=getPath();
        System.out.println("Memory byte. Build,find:\t"+(memory2-memory1)+"\t"+(memory3-memory2)); // +"\t"+(time4-time3)
        System.gc();
        Thread.yield();
    }
*/
    public void memoryTest() {
        Thread.yield();
        System.gc();
        Thread.yield();
        memory1=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        T graph=builder.build();
        System.gc();
        memory2=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        findPath(graph);
        System.gc();
        memory3=Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        String path=getPath();
        System.out.println("Memory byte. Build,find:\t"+(memory2-memory1)+"\t"+(memory3-memory2)); // +"\t"+(time4-time3)
    }
    
    // todo JVM with enabled instrumentary option allow to check object 'sizeof' with reflection API. It was more precision.
    // see 'compact reference' JVM option when run with Xmx <4Gb
}
