package simplegraph4j.util;

import simplegraph4j.IEdge;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.IVertex;
import java.io.File;
import java.io.IOException;

/**
 * Usefull procedure with ISimpleGraph
 * @author A.K.
 */
public class GraphUtil {

    /**
     * String graph representation
     **/
    public static <T> String toString(ISimpleGraph<T> graph) {
        return toString(graph, 10000000); // I think, you don't need more
    }
    
    /**
     * String graph representation.
     * @param graph this graph
     * @param maxSymbols limit string length. If string will be too lagre - will be cut by "..."
     **/
    public static <T> String toString(ISimpleGraph<T> graph, int maxSymbols) {
        assert maxSymbols>0;
        StringBuilder str=new StringBuilder();
        boolean appenedV=false;
        boolean lengthCut=false;
        lblCycle: for (IVertex<T> v:graph.getAllVertex()) {
          appenedV=true;
          str.append(v.getName()); // toString
          str.append("["); // toString
          boolean appenedE=false;
          for (IEdge e:v.getAdjacencies()) {
            str.append(e.getTarget().getName()); // toString
            str.append("=");
            str.append(e.getWeight());
            str.append("; ");
            appenedE=true;
            if (str.length()>maxSymbols) {
                lengthCut=true;
                str.setLength(str.length()-2); // "; "
                break lblCycle;
            }
          }
          if (appenedE) {
            str.setLength(str.length()-2); // "; "
          }
          str.append("]; ");
        }
        if (appenedV) {
            str.setLength(str.length()-2); // "; "
        }
        if (str.length()>maxSymbols || lengthCut) {
           if (str.length()+3>maxSymbols) str.setLength(Math.max(0, maxSymbols-3)); // for ...
           str.append("...");
           if (str.length()>maxSymbols) str.setLength(maxSymbols); // Do you realy want empty string?
        }
        return str.toString();
    }
    
    // todo look like good: void fromString(String str, ISimpleGraph<String> dest)
    
    /**
     * Copy all vertex/edges from one graph to other. It add each edges from src into dest.
     * @param src copy structure
     * @param dest into another graph object
     **/
    public static <T> void appendGraph(ISimpleGraph<T> src, ISimpleGraph<T> dest) {
      // 1. copy all vertex getName
      if (dest.vertices()==0) {// dest.getAllVertex().size().isEmpty()
        for (IVertex<T> v:src.getAllVertex()) dest.addVertex(v.getName());
      } else {
        for (IVertex<T> v:src.getAllVertex())
          if (dest.vertexForObejct(v.getName())==null)
            dest.addVertex(v.getName()); // todo add assert into addVertex() for check double call
      }
      // 2. copy edges
      for (IVertex<T> v:src.getAllVertex()) {
        IVertex<T> destV=dest.vertexForObejct(v.getName());
        for (IEdge<T> e:v.getAdjacencies()) {
            destV.addEdge(e.getTarget().getName(), e.getWeight());
          //or dest.addEdge(v.getName(), e.getTarget().getName(), e.getWeight());
        }
      }
    }

    /**
     * HashCode, based on Vertex object (getName) and edge weight.
     *
     **/
    public static <T> int hashCode(ISimpleGraph<T> src) {
        int hash=0;
        for (IVertex<T> v:src.getAllVertex()) {
            int fromVertexHash = v.getName().hashCode();
            for (IEdge<T> e:v.getAdjacencies()) {
                long weight = Double.doubleToLongBits(e.getWeight());
                int valueHash = (int)(weight^(weight>>>32)); // see Double.hashCode()
                int edgeHash = fromVertexHash ^ e.getTarget().getName().hashCode() ^ valueHash;
                // todo: think about vertexA^vertexA=0, do it has enought hash entropy for self-linked vertex?
                hash += edgeHash;
            }
        }
        return hash;
    }

    /**
     * Clear all files in path (not recursive, only files).
     * @throws IOException when can not delete file
     **/
    public void clearFilesFromPath(File path) throws IOException {
      if (!path.isDirectory()) throw new IOException ("Path is not a directory: "+path);
      for (File f:path.listFiles()) if (f.isFile()) {
        if (!f.delete()) new IOException ("Can not delete file: "+f);
      }
    }
}
