SimpleGraph4J java library
========
This library provides *simple* **low memory** and **fast** implementation of direction graph for Java applications. Each edge have "weight" for find nearest path between (using for Djikstra).
It wrote for work with large direction graph with 1000-100000 nodes and 1000000000 links between node.
Detail of implementation: each graph node is linked with Object and have integer order, it have link array. Each link it is pair of <int,double>.
Have convert low level integer primitive into outside external you'r Object identifier on the fly by lightweight API.

Support math alghorithm: Djikstra for search path.

Have 2 implementation:

* in memory
* on hard disk graph (use folder with multiply files for store huge graph).

Library mission
---------------
To be **faster**, **low memory** required, and **simple** graph implementation for Java. Use Djkstra path find. Work with *big graph* in single personal computer.

Compare with another Java graph library
---------------------------------------
This comparsion I do at 2020-2021 year.

* Hipster (1.0.1, https://citius.usc.es/transferencia/software/hipster4j) - "Hipster: An Open Source Java Library for Heuristic Search", in-memory graph. Too many features and huge implementation - required a lot of memory. Not have file-storage.
* apache-common-graph (?, https://commons.apache.org/sandbox/commons-graph) - not have math path (Djikstra), or I not found it. Library not complite. In memory. Not have file-storage.
* Guava (28.0, https://github.com/google/guava) - have in-memory implementation of direction and bi-direction graph. Have path find implementation on class Traverser. I do not undestand at first time he's API, it might be not safe memory too. Not have file-storage.
* jgrapht.org  don't know about it... TODO learn.


---------------------------------------

Support CMD run mode
--------------------
As another Java(tm) jar file can be use with 2 case: as library, as executable.
You can use  this simplegraph4j-*.jar from command line without coding Java.

Example: you have test.csv (TSV) file with next format: vertex(tab)vertex(tab)weight

    1
    2
    3
    4
    5
    1	2	0.002327983
    2	1	0.009753869
    2	3	0.041628566
    1	4	0.050269039
    2	4	0.049847454
    1	3	0.054585952
    3	4	0.065975218
    4	1	0.072496691
    3	2	0.093024444


Command line for find nearest path from vertex 1 to 3:

    java -jar simplegraph4j-0.0.4.jar -i test.csv -f 1 -t 3

Output:

    1 2 3

Command line for find nearest path from vertex 1 to 5:

    java -jar simplegraph4j-0.0.4.jar -i test.csv -f 1 -t 5

Output is empty, but error code is 4 (linux "echo $?" windows "echo %errorlever%").
For user friendly output use --verbose option (or read --help):

    java -jar simplegraph4j-0.0.4.jar --verbose -i test.csv -f 1 -t 5

Output:

    Search shortest path from 1 to 5
    Path not found.

It will be usefull for batch processing graph data, or with map-reduce. Don't forgot add dependencys near simplegraph4j*.jar: trove4j-3.0.3.jar.

Library Architecture
--------------------
Interface:

* ISimpleGraph<T> - base graph operation (add vertex, add node).
* IPathFinder<T> - provide path find method. It is agregated 1 to 1 with ISimpleGraph.
* IVertex<T> - vertex, edge's collection container
* IEdge<T> - high level edge representation (usualy, it is read only copy of low level graph data).

Implementation:

* SimpleGraph<T> - java pojo-object based graph. It is simple implementation. ≈28 byte per 1 edge, +0-15% owerhead by ArrayList.
* PrimitiveGraph<T> - using primitive array for minimize in memory usage. ≈12 bytes per 1 edge, +0-30% owerhead by T*ArrayList but support trimToSize().
* FileGraph<T> - file-storage graph. Using temp path with multiply binary files. 12 bytes per 1 edge on disk + 1-16kB per vertex for IO bufer. OS may have limit on open file. This graph implementation will close not using open file and flush cache after FileGraph.MAX_OPEN_FILE = 1200, when this limit is overflow. But random read/write in over that MAX_OPEN_FILE vertex can be decreace perfomance (up to x1000!).
* SimpleGraphConfig - one place for configurate a little different behavior.

Support operation: fast build wery big directed graph with edge weight (floating point precission 'double'), add edges, add vertex, iterate over edges/wertex; find shortest path (Djikstra alhorithm), find all path from point (Djikstra alhorithm).

Not supported operation: modify exist vertex, remove vertex; load graph from file (future feature).

Operation time
--------------
This library do provide fast and low system resource required graph. Fast create, fast iterate over vertex/edges. Minimum check and minimal verify of data.
* Insert new vertex O(Ln(n)), when n - number of edges (using HashMap).
* Add vertex between edge O(2*Ln(n))+O(1) - when n - number of vertex, (may be O(m) with array memeory reallocate, m - number of edges from vertex)
* Get first vertex O(1)
* Iterave over n edge from single vertex O(n)
* find vertex between edge O(Ln(m))+O(n)  (not implemented yet)
* Get vertex by name vertexForObejct(Object) Ln(n) (using HashMap)


Multithread, random access
--------------------------
All implementation of graph is not thread safe. But if you realy know what you do...

All write operation is not thread safe. All read operation is thread safe for in-memory implementation, if you graph has finish and do not modify.
If any thread read/write as single thread per single node (vertex) it can be safe (for in-memory and file storage), but required not add new node with it - only link exist vertex.

Detail:

* Add new vertex is not thread safe. Strong recomended disable options for auto add vertex: SimpleGraphConfig.setAllowAutoAddVertex(false).
* Do not read (iterate) and write in single vertex - it is not thread safe
* Add new edge is not thread safe per vertex. But safe when eatch single thread add edges from single vertex.
* All read operation is thread safe when graph is read only (except FileGraph).
  * In FileGaph read operation by single thread with single edge is safe. Do not read from single edge from multiply thread - it is not safe.

In-memory implementation have good perfomance of random access read. File implementation have best perfomance with sequence read link (have limited cache up to MAX_OPEN_FILE then perfomance was down).

Memory usage metrix
-------------------
Table of memory usage by graph implementation class:

| Implementation | Memory per vertex                                                                       | Memory per edge               |
|----------------|-----------------------------------------------------------------------------------------|-------------------------------|
| SimpleGraph    | ? <100byte                                                                              | 28 byte + 0-15% of ArrayList* |
| PrimitiveGraph | ? <100byte                                                                              | 12 byte + 0-30% of TIntList*  |
| FileGraph      | In memory: 8-16 kB per first 1200 vertex; over 1201 is ≈1kB. In disk: 1 file per vertex | 12 byte on file               |

(*) ArrayList and TIntList at vertex was using for store edges. This class contain dynamic reallocate array, usualy it can be up to 30% memory overhead. PrimitiveGraph have method trimToSize() for decrease memory usabe by unused array space.
Java Object reference size is platform deppendence. Usualy it is 8 byte per Object link. But with special java option (compress link) and low parameter Xmx it can be using only 4 byte per link.

Absolutly max vertex and edges per vertex is Integer.MAX_VALUE=2,147,483,647

Required library dependency
---------------------------
Required Java 1.7 or later.

* trove4j (see also apache-primitive, this library should be good too) for PrimitiveGraph implementation
* JUnit 4 for unit test (only when you compile jar)
* Many of graph implementation classes support JMX interface for easy vatch about graph at runtime (it is part of JRE). You can use jvisualvm and MBean plugin for use it.

How to compile, where is javadoc?
---------------------------------
Use Apache Maven for compile (see pom.xml file).

1. download sources, unzip into simplegraph4j.
2. cd simplegraph4j\
3. mvn install
4. watch into simplegraph4j\target\


Example
-------
At the first step you should create Graph object. Then add all vertex. Then add edges. Then you can using graph.pathFinder().computePath("A","C") for take List with path vertex sequence between 2 point.
At ISimpleGraph<T> as T you can using not only String. Any class that implement equals() and hashCode() has acceptable.

### Make simple graph

    import simplegraph4j.*;
    import simplegraph4j.onobject.SimpleGraph;
    ...
       ISimpleGraph<String> graph=new SimpleGraph<>();
       graph.addEdge("A", "B", 1.0); // from, to, w
       graph.addEdge("A", "B", 1.0);
       graph.addEdge("A", "C", 15.4);
       graph.addEdge("B", "C", 4.32);
       graph.addEdge("C", "B", 6.0);


### Find path between 2 point

    import java.util.List;
    import simplegraph4j.*;
    import simplegraph4j.exception.PathNotFoundException;
    ...
        ISimpleGraph<String> graph=...add data to graph...
        IPathFinder<String> pathF=graph.pathFinder();
        List<String> path=pathF.computePath("A","C");
        double distance = pathF.getPathLength();

### Graph with file storage

    ...
    File tempPath=new File("exist or not exist folder");
    ISimpleGraph<String> graph=new SimpleGraph<>(tempPath);
    ...work with graph...
    ((Closeable)graph).close(); // recomended do close for free resource
    // should be write code for clean tempPath manualy.

WARNING: for debugging I do not clear temp file, for large graph it can be problem contains 100000 files in once folder for file system.

### Extra use-case
See *DijkstraPathFind implementation for iterate details.

Using usefull GraphUtil class:
        
        ISimpleGraph<String> graph=...add data to graph...
        System.out.println(GraphUtil.toString(graph));
        // Console output:
        // A[B=1.0; B=1.0; C=15.4]; B[C=4.32]; C[B=6.0]


Iterate over vertex and edge's:

    ISimpleGraph<String> graph=...
    for (IVertex<String> v:graph.getAllVertex()) {
      for (IEdge<String> e:v.getAdjacencies())
        System.out.println(v.getName()+" -> "+e.getTarget().getName()+"  "+e.getWeight());
    }

output:

    A -> B  1.0
    A -> B  1.0
    A -> C  15.4
    B -> C  4.32
    C -> B  6.0

    
For FileGraph can use EdgeHolder.forEach() for max perfomance (it is low level API, can be rewrite later).

Select all path from one point with specific length then processing it:
    PathLightFilter findBestPath=new PathHandle<String>(){
        public int pathProcessed=0;
        public int pathWithLengthCriteria=0;
        public List<String> bestPath;
        public double distance=Double.POSITIVE_INFINITY;

        @Override
        public void pathVariant(List<String> path, double d) {
            pathProcessed++;
            if (path.size()>=lightTarget.lightMinimalLength && path.size()<=lightTarget.lightMaximalLength) {
                pathWithLengthCriteria++;
                d = lightTarget.function(path.size(), d);
                if (d<distance) {
                   bestPath=path;
                   distance=d;
                }
            }
        }

        @Override
        public String toString() {
            return "PathLightFilter{" + "pathProcessed=" + pathProcessed + 
            ", pathWithLengthCriteria=" + pathWithLengthCriteria + 
            ", bestPath=" + bestPath + ", distance=" + distance + '}';
        }
    };
    graph.pathFinder().computeAllPath(startPoint, findBestPath);
    List<String> selectedPath = findBestPath.bestPath;
    System.out.println(findBestPath);

For watch graph as image you can using this usefull util:
    ISimpleGraph<String> graph=...
    GraphVisualizator visualizator = new GraphVisualizator();
    java.awt.image.BufferedImage image = visualizator.vizualize(1024,1024, graph, "A", null);
    ...

It work fine with small graph less that 1000 point. Otherwise you will be wait a long time.
It is not using special math alhorithm, I just vrote it for my graph, he's perfect split by layer. But in you'r case it can me not good implementation.
        
Project Roadmap
-------
For future feature.

(future 2 final)

- more usefull util (visualization, statistic, any more math alhorithms)
- IO: support Java Serializable, (I think without serialVersionUID)
- implemen equals() method.
- check package name, rename package.
- More JavaDoc
- may be rewrite same class and change license on GNU LGPL (see using sources under GNU GPL v2 restriction).

(future 1)

- Split graph implementation from ISimpleGraph<Object> to ISimpleGraph<Object> wrapper ower IntegerGraph<int>. IntegerGraph should be ultimate faster API, more CPU-friendly, It was implemented by PrimitiveGraph and FileGraph.
- new API: IVertex.findEdge(IVertex to), removeEdge(IVertex to), setEdge(IVertex to, double newW). May be not fast.
- full implement Iterator, Iterable interface, List iterator (check interface, may be enought for java 1.7)
- FileGraph: support load graph from exist files (full support save/load)
- more unit test.

0.0.4 (_current_)

- Add GraphIOUtil usefull util for read and qrite graph into TSV (CSV-like) file format. This should be more useull for map-reduce framework compatibility. Usefull for export/import data.
- maven projec pom.xml: less warnings

0.0.3

- More simple API, now you don't need define all vertex addVertex(...) before addEdge(...). Add SimpleGraphConfig class for configure same behavior.
- IVertex.addEdge() - should be fast for some use-case.
- Add usefull function incomeEdgeCount(T to) outcomeEdgeCount(T from) into IPathFinder.
- FileGraph fix assert check on close. Small Iterator improvement.
- More JavaDoc


0.0.2

- Util - copy one graph to other, for clone and save to file. Implement toString(), hashCode().
- Add method addEdge(to, weight) into IVertex<T>, it should improvement perfomance for manual graph build.
- Rename library and packages from "simplegraph" to "simplegraph4j" - it was a more unusual library name. Representation that it was best optimize only for Java implementation.

0.0.1

- First relase.



License
-------
This project with source code distribute under GNU GPL v2.
Single author at 2021 is github.com/playerO1 (in same part of code I wrote A.K.).

Third party license:

* Trove4J - http://trove4j.sf.net  License: GNU Lesser General Public License 2.1
* JUnit - https://junit.org/ using only for testing, not include on release. Common Public License Version 1.0
* Java (tm) - programming language.
* OpenJDK - the Java platform. Using source code from OpenJDK(java.io.BufferedOutputStream). License: GNU GPL 2. (Remark: this restriction required distribute only under GNU GPL v2; in oroginal source code has not human author name, only corporation "Sun Microsystems, Inc" and "Oracle and/or its affiliates" https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/io/BufferedOutputStream.java)
* People from stackoverflow.com - has been using example of Dijkstra's algorithmwatch from https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map. But it was a lot of rewrote. (Stackowerflow using CC BY-SA 4.0 after 2018-05-02  https://stackoverflow.com/help/licensing).

TODO Later need to be rewrite class UnsyncBufferedOutputStream and UnsuncBufferedInputStream for change license from GNU GPL v2 to GNU LGPL.
