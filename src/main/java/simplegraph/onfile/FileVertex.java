package simplegraph.onfile;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import simplegraph.IEdge;
import simplegraph.IVertex;

public class FileVertex<T> implements IVertex<T>, Comparable<FileVertex>, Iterable<FileVertex.PEdge<T>>
{
    private final FileGraph vertexIndexResolver; // only for getAdjacencies()
    public final int id;
    
    public final T name; // external vertex name
    protected final EdgeHolder adjacencies;
    
    // foe Dijkstra
    public double minDistance = Double.POSITIVE_INFINITY;
    public FileVertex<T> previous; // IDijkstraVertex
    
    
    public FileVertex(FileGraph vertexIndexResolver, int id, T argName) throws IOException {
        this.vertexIndexResolver=vertexIndexResolver;
        this.id=id;
        this.name=argName; 
        adjacencies=new EdgeHolder(usedFile()); // warning - method call from constructor
    }
    
    protected File usedFile() {
        return new File(vertexIndexResolver.workPath,id+".vertex.dat");
    }
    
    public String toString() { return name.toString(); }
    public int compareTo(FileVertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

    @Override
    public T getName() {
        return name;
    }

    @Override
    public Iterable<PEdge<T>> getAdjacencies() {
        return this;//todo not effective! Use Visitor
    }

    public PEdge<T> getEdge(long i) throws IOException {
        return adjacencies.getEdge(vertexIndexResolver, i);
    }

    /**
     * Not effective. Use adjacencies.forEach(visitor)
     * @return 
     */
    @Override
    public Iterator<PEdge<T>> iterator() { // todo итератор реализовать в adjacencies(EdgeHolder)
        return (Iterator)adjacencies.iterator(vertexIndexResolver);
//        return new Iterator() {
//            long i=0;
//            @Override
//            public boolean hasNext() {
//                return i<adjacencies.size();
//            }
//            @Override
//            public Object next() {
//                try {
//                    if (i>=adjacencies.size()) throw new NoSuchElementException();
//                    return getEdge(i++);
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        };
    }
    
    public void clear() throws IOException {
        adjacencies.clear();
    }

    void close() throws IOException {
        adjacencies.close();
//todo пока для отладки оставлю файлы.        File file=usedFile();
//        if (!file.delete()) {
//            throw new IOException("Can not delete temp file "+file);
//        }
    }

    public static class PEdge<T> implements IEdge<T>{
        public final FileVertex<T> target;
        public final double weight;
        public PEdge(FileVertex<T> argTarget, double argWeight){
            target = argTarget; weight = argWeight;
        }
        public FileVertex<T> getTarget() {
            return target;
        }
        public double getWeight() {
            return weight;
        }
    }
}
