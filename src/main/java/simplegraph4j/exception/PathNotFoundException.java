package simplegraph4j.exception;

/**
 * Graph not contain any path from A to target
 * @see simplegraph.IPathFinder
 */
public class PathNotFoundException extends Exception {
    public PathNotFoundException() {}
    public PathNotFoundException(String string) {super(string);}
}
