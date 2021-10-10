/*
 * For fast demonstration and more usefull without special coding,
 */
package simplegraph4j.executable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import simplegraph4j.IPathFinder;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.PathHandle;
import simplegraph4j.exception.PathNotFoundException;
import simplegraph4j.onobject.SimpleGraph;
import simplegraph4j.onprimitive.PrimitiveGraph;
import simplegraph4j.util.GraphIOUtil;

/**
 * Main executable class.
 * You know how to run:
 *  java -jar simplegraph4j-1.0.4.jar --input file.csv --from 1 --to 10 --verbose
 *  java -jar simplegraph4j-1.0.4.jar --help
 *  java -jar simplegraph4j-1.0.4.jar -i file.csv -f 1 -t 10
 * 
 * @author A.K. (github.com/playerO1)
 */
public class CMDLauncher {
    public static final int EXIT_CODE_ERROR=1;
    public static final int EXIT_CODE_IOERROR=2;
    public static final int EXIT_CODE_BAD_PARAMETER=3;
    public static final int EXIT_CODE_PATH_NOT_FOUND=4;
    
    protected String tFrom, tTo; // vertex target
    protected String graphFileName;
    protected boolean verbose=false;

    protected ISimpleGraph<String> graph;
    protected List<String> path;

    public void printHelp() {
        System.out.println("Graph finder parameters:");
        System.out.println(" -h --help           print this message");
        System.out.println(" -i --input {file}   load graph from CAV file.");
        System.out.println(" -f --from {vertex}  find path from vertex.");
        System.out.println(" -t --to {vertex}    target vertex. If not set - will be print all possible path.");
        System.out.println(" -v --verbose        print more text.");
        System.out.println("");
        System.out.println("CSV file format: from<tab>to<tab>weight");
    }

    protected boolean parseArgs(String[] args) {
        // You may make it with Apache CLI library. But for less dependencies I do it as hardcode.
        if (args.length<=1) return false;
        for (int i=0;i<args.length;i++) {
            if ("-v".equals(args[i]) || "--verbose".equals(args[i])) {
                verbose=true;
            } else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                printHelp();
                System.exit(0); // no error
                //return false; // show help, error code
            } else if ("-i".equals(args[i]) || "--input".equals(args[i])) {
                if (i+1>=args.length) {
                    System.out.println("Expected argument after --input");
                    return false;
                }
                graphFileName=args[++i];
            } else if ("-f".equals(args[i]) || "--from".equals(args[i])) {
                if (i+1>=args.length) {
                    System.out.println("Expected argument after --from");
                    return false;
                }
                tFrom=args[++i];
            } else if ("-t".equals(args[i]) || "--to".equals(args[i])) {
                if (i+1>=args.length) {
                    System.out.println("Expected argument after --to");
                    return false;
                }
                tTo=args[++i];
            } else {
                System.out.println("Bad argument "+(i+1)+": "+args[i]);
                return false;
            }
        }
        // validate
        if (graphFileName==null) {
            System.out.println("Required --input");
            return false;
        }
        if (tFrom==null) {
            System.out.println("Required --from");
            return false;
        }
        return true;
    }
    
    public void loadGraph() {
        File f=new File(graphFileName);
        try {
            graph = new PrimitiveGraph<>();
        } catch (NoClassDefFoundError noClass) { // ClassNotFoundException
            System.err.println("Warning: "+noClass.getMessage()+". Check jar library. Retry with Object class.");
            if (verbose) noClass.printStackTrace();
            graph = new SimpleGraph<>();
        }
        try {
            try {
                GraphIOUtil.readGraphFromFile(f, graph);
            } catch (NoClassDefFoundError noClass) { // ClassNotFoundException
                System.err.println("Warning: "+noClass.getMessage()+". Check jar library. Retry with Object class.");
                if (verbose) noClass.printStackTrace();
                graph = new SimpleGraph<>();
                GraphIOUtil.readGraphFromFile(f, graph);
            }
        } catch (IOException ex) {
            System.err.println("Error read graph from \""+graphFileName+"\": "+ex.getMessage());
            if (verbose) ex.printStackTrace();
            System.exit(EXIT_CODE_IOERROR);
        }
    }
    
    protected void printPath(List<String> path, double length) {
        if (verbose) System.out.print("Path: ");
        if (!path.isEmpty()) {
            System.out.print(path.get(0));
            for (int i=1;i<path.size();i++) {
               System.out.print(" ");
               System.out.print(path.get(i));
            }
        }
        System.out.println();
        if (verbose) System.out.println("Length of path: "+length);
    }
    
    public void execute() {
        if (graph.vertexForObejct(tFrom)==null) {
            System.err.println("Vertex not found: "+tFrom);
            System.exit(EXIT_CODE_BAD_PARAMETER);
        }
        if (tTo!=null) {
            if (graph.vertexForObejct(tTo)==null) {
                System.err.println("Vertex not found: "+tTo);
                System.exit(EXIT_CODE_BAD_PARAMETER);
            }
        }
        IPathFinder<String> pathFinder=graph.pathFinder();
        try {
            if (tTo!=null) {
                if (verbose) System.out.println("Search shortest path from "+tFrom+" to "+tTo);
                List<String> path=pathFinder.computePath(tFrom, tTo);
                printPath(path, pathFinder.getPathLength());
            } else {
                if (verbose) System.out.println("Search all path from "+tFrom);
                pathFinder.computeAllPath(tFrom, new PathHandle<String>() {
                    @Override
                    public void pathVariant(List<String> path, double d) {
                        printPath(path, d);
                    }
                });
            }
        } catch (PathNotFoundException noPath) {
            if (verbose) System.out.println("Path not found.");
            System.exit(EXIT_CODE_PATH_NOT_FOUND);
        }
    }

    public static void main(String[] args) {
        CMDLauncher launcher=new CMDLauncher();
        // parse args
        if (!launcher.parseArgs(args)) {
            launcher.printHelp();
            System.exit(EXIT_CODE_BAD_PARAMETER);
        } else {
            // load
            launcher.loadGraph();
            // execute
            launcher.execute();
        }
    }
}
