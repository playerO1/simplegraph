package simplegraph4j;


import java.util.ArrayList;
import simplegraph4j.onprimitive.EdgeHolder;

/**
 * Test for memory usage of graph structure
 * It is not unit test!
 */
public class GraphMemoryTest {
    static final int COUNT=5000000;
    public static void main(String[] args) {
        System.out.println("Memory hand test.");
        
         Runtime.getRuntime().gc();
         /*
        printMemory();
        {
          ArrayList<Edge> list=new ArrayList<>();
          for (int i=0;i<COUNT;i++) {
              list.add(new Edge(null, 10.0+i));
          }
          printMemory();
            System.out.println("Trim to size");
            list.trimToSize();
            Runtime.getRuntime().gc();
          printMemory();
          System.out.println("Done.Collection count="+list.size());
        }
        
        
        Runtime.getRuntime().gc();
        */
        printMemory();
        {
            EdgeHolder eHolder=new EdgeHolder();
          for (int i=0;i<COUNT;i++) {
              eHolder.addEdge(5+i, 10.0+i);
          }
          printMemory();
            System.out.println("Trim to size");
            eHolder.trimToSize();
            Runtime.getRuntime().gc();
          printMemory();
          System.out.println("Done.Collection count="+eHolder.size());
        }
    }
    
    /*
    100000 Object
      5620952-376032=5244920
     trim+gc 3114632-376032=2738600
    
    100000 Primitive 
      5032560-313920=4718640
     trim+gc 1574576-313920=1260656
      6536592-376176=6160416
     trim+gc 1573280-376176=1197104
    
    5000000 Object
      200312880-376176=199936704
     trim+gc 140314104-376176=139937928
    
    5000000 Primitive 
      128433568-376032=128057536
     trim+gc 60373136-376032=59997104
    
    
    100000 Object: 27.3-52.4/edge
    100000 Primitive: 12.0-12.6-47.1-61.6/edge
    5000000 Object: 28.0-40.0/edge
    5000000 Primitive: 12.0-25.6/edge
    
    С использованием примитивов экономия в 2 раза по памяти. Но явные ограничения Integer.MAX_VALUE=2147483647 на количество.
    Primitive 2147483647 *12= 25769803764 байт = 24 Гб на 1 вершину максимум.
    Object 2147483647 *28= 256 Гб на 1 вершину но без ограничений.
    */
    
    
    
    
    static void printMemory() {
         // very not precission!
        long usedMemory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        System.out.println("USED MEM="+usedMemory+";  maxMemory: "+Runtime.getRuntime().maxMemory()+" freeMemory:"+Runtime.getRuntime().freeMemory()+" totalMemory"+Runtime.getRuntime().totalMemory());
    }
}
