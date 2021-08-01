package simplegraph4j;

import simplegraph4j.onfile.FileGraph;

/**
 * Settings for simplegraph4j implementation.
 * @author A.K. (github - playerO1)
 */
public final class SimpleGraphConfig {
    private static boolean allowAutoAddVertex = true;
    private static boolean autoClearFileGraphTempFolder = true;
    
    private SimpleGraphConfig() {}

    /**
     * Allow using addEdge with new vertex (without call addVertex)
     * Default true.
     * If you want build graph multithread, recomended FALSE.
     * @see ISimpleGraph#addVertex(java.lang.Object) 
     */
    public static boolean isAllowAutoAddVertex() {
        return allowAutoAddVertex;
    }

    /**
     * Allow using addEdge with new vertex (without call addVertex)
     * @param allowAutoAddVertex if false then call addEdge() with uncknown vertex will produce IllegalArgumentException
     */
    public static void setAllowAutoAddVertex(boolean allowAutoAddVertex) {
        SimpleGraphConfig.allowAutoAddVertex = allowAutoAddVertex;
    }

    /**
     * FileGraph implelentation will be auto clean temp directory
     * Default true.
     * todo if FileGraph call .save() method - it will be not temp directory and do not auto clear.
     */
    public static boolean isAutoClearFileGraphTempFolder() {
        return autoClearFileGraphTempFolder;
    }

    /**
     * FileGraph implelentation will be auto clean temp directory
     * @param autoClearFileGraphTempFolder 
     */
    public static void setAutoClearFileGraphTempFolder(boolean autoClearFileGraphTempFolder) {
        SimpleGraphConfig.autoClearFileGraphTempFolder = autoClearFileGraphTempFolder;
    }

    /**
     * @return Limit open IO for FileGraph
     */
    public static int getSetMaxOpenIO() {
        return FileGraph.MAX_OPEN_FILE;
    }

    /**
     * Set limit open IO for FileGraph
     * @param setMaxOpenIO required great that 0
     */
    public static void setSetMaxOpenIO(int setMaxOpenIO) {
        if (setMaxOpenIO<1) throw new IllegalArgumentException("Required>0 IO");
        FileGraph.MAX_OPEN_FILE = setMaxOpenIO;
    }
    
    
    
}
