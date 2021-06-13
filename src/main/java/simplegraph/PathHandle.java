package simplegraph;

import java.util.List;

/**
 * For IPathFinder
 * @author A.K.
 */
public interface PathHandle<T> {
    void pathVariant(List<T> path, double d);
}
