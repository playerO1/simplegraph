package simplegraph4j.onfile;

/**
 * For real time debug MBean interface
 * @author A.K.
 */
public interface FileGraphMBean {
    String getName();
    String getWorkPath();
    int getVertices();
    long getEdges();
    int getMaxOpenIO();
    int getOpenIO();
    long getIOSequenceID();
}
