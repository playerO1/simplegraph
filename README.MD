 Simple Graph java library
========
This library provides simple low memory and fast implementation of direction graph for Java applications.
It wrote for work with large direction graph with 1000-100000 nodes and 1000000000 links between node.
Detail of implementation: each graph node is linked with Object and have integer order, it have link array. Each link it is pair of <int,double>.
Have convert low level integer primitive object into outside external you'r Object identifier on the fly by lightweight API.

Support math alghorithm: Djikstra for search path.

Have 2 implementation: in memory, on hard disk graph (use folder with multiply files).

Library mission
---------------
To be faster, low memory required, and simple graph implementation for Java.

Compare with another Java graph library
---------------------------------------
This comparsion I do at 2020-2021 year.
* Hipster (,) - in-memory graph. Too many features and huge implementation - required a lot of memory. Not have file-storage.
* apache-common-graph (, ) - not have math path (Djikstra), or I not found it. Library not complite. Not have file-storage.
* Guava (,) - have in-memory implementation of direction and bi-direction graph. I not found math implementation of Djikstra and. Not have file-storage.


---------------------------------------

Library Architecture
------------


Thread, random access
---------------------
All implementation of graph is not thread safe.
All write operation is not thread safe. All read operation is thread safe for in-memory implementation, if you graph has finish and do not modify.
If any thread read/write in single node it can be safe (for in-memory and file storage), but required not add new node with it - only link.

In-memory implementation have good perfomance of random access. File implementation have best perfomance withsequence read link.

Memory usage metrix
-------------------

... per node
... per link


Required library
----------------
Required java 1.7 or later.
* throve (see apache-primitive, this library should be good too) for PrimitiveGraph implementation
* JUnit 4 for unit test (only when you compile jar)
* Many of graph implementation classes support JMX interface for easy vatch about graph at runtime (it is part of JRE). You can use jvisualvm and MBean plugin for use it.

How to compile, where is javadoc?
---------------------------------
Use maven for compile.
. cd simplegraph\
. mvn install
. watch into simplegraph\target\
Javadoc is in source.


Example
-------
TODO sample!

### Make simple graph
import simplegraph.*;
...
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);


### Find path between 2 point
import java.util.List;
import simplegraph.*;
import simplegraph.exception.PathNotFoundException;
...
        ISimpleGraph<String> graph=new SimpleGraph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1.0); // from, to, w
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 15.4);
        graph.addEdge("B", "C", 4.32);
        graph.addEdge("C", "B", 6.0);
        
        IPathFinder<String> pathF=graph.pathFinder();
        List<String> path=pathF.computePath("A","C");
        assertEquals(3, path.size());
        assertEquals(1.0+4.32+6.0, pathF.getPathLength(), 0.0000001);


### Graph with file storage
TODO example, but you can watch source.

Roadmap
-------
For future feature.

0.1.4
- more usefull util (visualization, statistic, any more math alhorithms)
- IO: MapReduce file graph - same as file storage graph, but using text for store number. This should bemore useull for map-reduce framework compatibility.

0.1.3
- Split graph implementation from SimpleGraph<Object, Object> to SimpleGraph<Object, Object> wrapper ower IntegerGraph<int,int>
- check package name, rename package.
- Util - copy one graph data to other. For clone and save to file.

0.0.2
- implement Iterator, Iterable interface each implementation
- more unit test
- 

0.0.1
First relase.



License
-------
This project with source code distribute under GNU GPL v2.
Single author at 2021 is github.com/playero1 (in same part of code I wrote A.K.).

Third party license:
* trove4j - http://trove4j.sf.net  License: GNU Lesser General Public License 2.1
* junit - https://junit.org/
* OpenJDK - using source code from OpenJDK(java.io.BufferedOutputStream). License: GNU GPL 2. (Remark: this restriction required distribute only under GNU GPL v2; in oroginal source code has not human author name, only corporation "Sun Microsystems, Inc" and "Oracle and/or its affiliates" https://github.com/openjdk-mirror/jdk7u-jdk/blob/master/src/share/classes/java/io/BufferedOutputStream.java)
* Has been using example of Dijkstra's algorithmwatch from https://stackoverflow.com/questions/17480022/java-find-shortest-path-between-2-points-in-a-distance-weighted-map. But it was a lot of rewrote. (Stackowerflow using CC BY-SA 4.0 after 2018-05-02  https://stackoverflow.com/help/licensing).

TODO Later need to be rewrite class UnsyncBufferedOutputStream and UnsuncBufferedInputStream for change license from GNU GPL v2 to GNU LGPL.
