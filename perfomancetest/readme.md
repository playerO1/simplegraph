Perfomance test of Java graph library
=====================================

This project was check memory usage and perfomanc between same of Java graph library.

Will checking:
* memory usage per edge/node, memory for search path
* time to build graph, time for search short path


List of another library
---------------------

* Guava 30.1.1
* Hipster4j 1.0.1
* JGraphT 1.5.1
* Cassovary 7.1.0 ()
* Simplegraph4J  0.0.4 (my project)
* simple-graphs, SimpleGraphs, etc...

Links on source code:

1. https://github.com/jgrapht/jgrapht (alot of classes, i think it is big project)
1. https://github.com/twitter/cassovary "Cassovary is a simple big graph processing library for the JVM" (Twitter inc)
(that's best for compare with simplegraph4j)
1. https://github.com/earlygrey/simple-graphs (BFS,DFS,A* search, and other)
(that's best for compare with simplegraph4j)
1. https://github.com/javaronok/SimpleGraph (directed,undirected; DFS,AFS  search; simple graph on object)




About speed and memory measurement in Java(tm) applications.
------------------------------------------------------------
JVM has JIT compiller. This is reason think about same warm up test cycle before take measure real speed of programm. See JMetter maven plugin for perfomance test.

Is not default 'sizeof' method in Java (Do you know it from C?). Java can compress object reference when JVM using less that 4Gb. It was possible with instrumentary JVM mode ower reflection get 'sizeof' each object.
I know only 3 hard and not precission wayto know memory of object:

1. Use (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) for get current memory usage with garbage. Try call Runtime.gc() for minimize garbage (not garantedtake efect) between measure.
2. Manualy look on each variable and structure and wrote calculation on the paper. Yes, not easy way for check.
3. Use reflection API when JVM run in instrumental mode. It was moretrue information about size of object, but not stable.
4. Use memory dump (JVisualVM, Eclipse memory analyzer).

About testing
-------------




How to run test? System requirement
-----------------------------------
Compile:
JDK 1.8
Maven
command line:

    cd perfomancetest
    mvn clean package
    #will be produce folder /target
    
    #way 1
    cd target
    java -Xmx1G -jar perfomancetest.jar > perfomanceresult.txt
    
    #or way 2
    java -Xmx1G -jar target/perfomancetest.jar > perfomanceresult.txt

Then copy text from file perfomanceresult.txt to any table processor (Open Office Calc, or MS Excel).

See also JVM options: -Xmx1G; -Xms1G; -verbose:gc; . JVisualVM.
https://docs.oracle.com/cd/E13150_01/jrockit_jvm/jrockit/geninfo/diagnos/tune_footprint.html
https://www.oracle.com/technetwork/java/javase/memorymanagement-whitepaper-150215.pdf


Memory: >2Gb, must be enought for test.


The plan (TODO)
---------------
1. compare with another library
2. wrote text about Java grpah library
3. publish open-access text in any web-sites. For example see: https://www.researchgate.net/topic/Computer-Science/publications


The compare result
==================

Comparing by:

1. Function, available alhorithm, 
2. Benchmark: perfomance
3. Benchmark: memory use


*TODO compare table*

*TODO Benchmark*

*TODO article*
