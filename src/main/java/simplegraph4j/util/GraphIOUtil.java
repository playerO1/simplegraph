package simplegraph4j.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;
import javax.imageio.IIOException;
import simplegraph4j.IEdge;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.IVertex;

/**
 * File utils for simplegraph4j object.
 * Save/load from singletext file.
 * 
 * @author playerO1
 */
public class GraphIOUtil {
    protected String FILE_COLUMN_SEPARATOR="\t";
    protected String FILE_NEW_LINE=System.lineSeparator();
    protected boolean FILE_WITH_HEADER=false;


    // ---- static usefull method ----
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
    
    /**
     * Write graph as tab-separated text CSV file
     * @param file
     * @param graph
     * @throws IOException 
     */
    public static void writeGraphToFile(File file, ISimpleGraph<String> graph) throws IOException {
        GraphIOUtil graphIO=new GraphIOUtil();
        try (FileWriter fWriter=new FileWriter(file); BufferedWriter bWriter=new BufferedWriter(fWriter)) {
            graphIO.writeGraphTo(bWriter, graph, new StringAsString());
        }
    }
    /**
     * Read graph as tab-separated text CSV file
     * @param file
     * @param graph
     * @throws IOException 
     */
    public static void readGraphFromFile(File file, ISimpleGraph<String> graph) throws IOException {
        GraphIOUtil graphIO=new GraphIOUtil();
        try (FileReader fReader=new FileReader(file); BufferedReader bReader=new BufferedReader(fReader)) {
            graphIO.readGraphFrom(bReader, graph, new StringAsString());
        }
    }


   // ---- configurable method ----
    
    public <T> void writeGraphTo(Writer writer, ISimpleGraph<T> graph, Function<T, String> toString) throws IOException {
        if (FILE_WITH_HEADER) {
            writer.write("FROM"+FILE_COLUMN_SEPARATOR+"TO"+FILE_COLUMN_SEPARATOR+"WEIGHT");
            writer.append(System.lineSeparator());
        }
        for (IVertex<T> v:graph.getAllVertex()) {
          String fromN=toString.apply(v.getName());
          if (v.edgesCount()==0) { // empty vertex
              writer.append(fromN);
              writer.append(System.lineSeparator());
          } else { // vertex with edges
            for (IEdge<T> e:v.getAdjacencies()) {
              writer.append(fromN);
              writer.append(FILE_COLUMN_SEPARATOR);
              writer.append(toString.apply(e.getTarget().getName()));
              writer.append(FILE_COLUMN_SEPARATOR);
              writer.append(Double.toString(e.getWeight()));
              writer.append(System.lineSeparator());
            }
          }
        }
    }

    public <T> void readGraphFrom(BufferedReader reader, ISimpleGraph<T> graph, Function<String, T> fromString) throws IOException {
        if (FILE_WITH_HEADER) {
            String header=reader.readLine();
            if (!header.equalsIgnoreCase("FROM"+FILE_COLUMN_SEPARATOR+"TO"+FILE_COLUMN_SEPARATOR+"WEIGHT"))
                throw new IOException("Wrong file header: "+header);
        }
        final boolean needCheckVertex=!simplegraph4j.SimpleGraphConfig.isAllowAutoAddVertex();
        String line;
        long n=0;
        while ((line=reader.readLine())!=null) {
            n++;
            if (line.isEmpty()) continue;
            int t1=line.indexOf(FILE_COLUMN_SEPARATOR);
            if (t1<0) {
                graph.addVertex(fromString.apply(line));
            } else {
                int t2=line.indexOf(FILE_COLUMN_SEPARATOR, t1+1);
                if (t2<=t1) throw new IIOException("Error parsing line "+n);
                String fromS=line.substring(0,t1);
                String toS=line.substring(t1+1,t2);
                String wS=line.substring(t2+1, line.length());
                T fromV=fromString.apply(fromS);
                T toV=fromString.apply(toS);
                double w=Double.parseDouble(wS);
                if (needCheckVertex) {
                    if (graph.vertexForObejct(fromV)==null) graph.addVertex(fromV);
                    if (graph.vertexForObejct(toV)==null) graph.addVertex(toV);
                }
                graph.addEdge(fromV, toV, w);
            }
        }
    }
    
    /**
     * for Java 1.7 - lambda not supported. java 1.8+ it was: (s)->s
     */
    static class StringAsString implements Function<String, String> {
        @Override
        public String apply(String s) {
            return s;
        }
    }
}
