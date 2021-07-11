package simplegraph4j.onfile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import simplegraph4j.util.UnsuncBufferedInputStream;
import simplegraph4j.util.UnsyncBufferedOutputStream;
import simplegraph4j.IEdge;
import simplegraph4j.IVertex;


/**
 * Disk store edge holder, based on primitive.
 * Max item: Integer.MAX_VALUES
 * ~12 bytes per 1 edge
 * But required external edge int-Object mapping.
 * 
 * Multithread write at once node (edge) is not thread safe. But can be thread-safe when each thread per done only.
 */
public class EdgeHolder implements Closeable{
    private static final long BLOCK_SIZE=4+8; // int(4)+double(8)   , long(8), float(same as int 4), see javadoc
    private long size;
    private long filePos; // see file.getFilePointer(), но он чуть медленнее, зато надёжный.
    private boolean flushed;
    
    
    protected static List<EdgeHolder> openIOList=new ArrayList<>();
    protected static long ioSequenceAllId=0; // fixme недопустимо переполнение!
    protected long ioSequenceId=0;
    
    //todo -- buffered stream --
    private UnsyncBufferedOutputStream os;
    private UnsuncBufferedInputStream is;
    private long isPos;
    
    protected final File file;
    
    public EdgeHolder(File fileName) throws IOException {
        file=fileName;
        size=0;
        filePos=0;
        //os=new UnsyncBufferedOutputStream(new FileOutputStream(file));
        flushed=true;
        checkIO();
    }

    // ------- limit control -------
    protected void openIO() throws FileNotFoundException {
        assert os==null;
        if (os!=null) return;// newer!
        if (size==0) {
            os=new UnsyncBufferedOutputStream(new FileOutputStream(file));
        } else {
            assert file.length()==size*BLOCK_SIZE;
            os=new UnsyncBufferedOutputStream(new FileOutputStream(file, true));
            filePos=file.length();
        }
        flushed=true;
        // IO limit control
        synchronized (openIOList) {
            openIOList.add(this);
            ioSequenceId=ioSequenceAllId++;
            assert ioSequenceAllId>=0;
        }
    }

    public void flush() throws IOException {
        if (os!=null && !flushed) {
           os.flush();
           flushed=true;
        }
    }

    protected void closeIO() throws IOException {
        assert os!=null;
        if (os==null) return;// newer!
        //flush();
        os.close();
        flushed=true;
        os=null;
        
        if(is!=null) {
            is.close();
            is=null;
        }
        
        synchronized (openIOList) {
            openIOList.remove(this);
        }
    }
    
    protected void checkIO() {
        if (os==null)  {
            // IO limit control
          try {
            if (openIOList.size()>=FileGraph.MAX_OPEN_FILE) {
                final int N_CLOSE=20;
                ArrayList<EdgeHolder> toClose=new ArrayList<>(N_CLOSE);
                synchronized (openIOList) {
                    for (int i=0;i<openIOList.size() && toClose.size()<N_CLOSE;i++) {
                        if (openIOList.get(i).ioSequenceId<ioSequenceAllId-N_CLOSE) toClose.add(openIOList.get(i));
                    }
                    for (EdgeHolder closeHolder:toClose) closeHolder.closeIO(); // throw IO
                }
            }
            openIO(); // throw IO
          } catch (IOException e) {
              throw new RuntimeException("Reopen stream error", e);
          }
        }
        //todo check limit
    }
    // ------------

    public void addEdge(int toVertex, double weight) throws IOException {
        checkIO();
        os.writeInt(toVertex);
        os.writeDouble(weight);
        filePos+=BLOCK_SIZE;
        size++;
        flushed=false;
    }
        
    public FileVertex.PEdge getEdge(FileGraph vertexIndexResolver, long edgeId) throws IOException {
        flush();
        if(is==null) {
            is=new UnsuncBufferedInputStream(new FileInputStream(file));
            isPos=0;
        }
        if (isPos>edgeId*BLOCK_SIZE) {
            is.close(); // todo слишком много close делает
            is=new UnsuncBufferedInputStream(new FileInputStream(file));
            isPos=0;
            if (edgeId!=0) {
                is.skip(isPos=edgeId*BLOCK_SIZE);
            }
        } else if (isPos<edgeId*BLOCK_SIZE) {
            is.skip(edgeId*BLOCK_SIZE-isPos);
            isPos=edgeId*BLOCK_SIZE;
        }
        int targetId=is.readInt();
        double weight=is.readDouble();
        isPos+=edgeId*BLOCK_SIZE;

        FileVertex<?> vertex=vertexIndexResolver.getVertexById(targetId);
        return new FileVertex.PEdge<>(vertex, weight);
    }

    public void forEach(EdgeVisitor visitor) throws IOException {
       flush(); // ensure all data has wroted
       assert filePos==size*BLOCK_SIZE;
       assert filePos==file.length();
       if (is==null) {
           is=new UnsuncBufferedInputStream(new FileInputStream(file));
       }
       try {
        for (long i=0;i<size;i++) { // or while not hrow EOFException
            int target=is.readInt();
            double weight=is.readDouble();
            visitor.acceptEdge(target, weight);
        }
       } finally {
           is.close();
           is=null;
       }
    }
    
    public Iterator<FileVertex.PEdge> iterator(final FileGraph vertexIndexResolver) {
        try {
            flush(); // ensure all data has wroted
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       assert filePos==size*BLOCK_SIZE;
       assert filePos==file.length();
        return new Iterator<FileVertex.PEdge>() {
            UnsuncBufferedInputStream is;
            long i=0;
            {
                try {
                    is=new UnsuncBufferedInputStream(new FileInputStream(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public boolean hasNext() {
                return i<size && is!=null;
                // todo close empty iterator
            }

            @Override
            public FileVertex.PEdge next() {
                if (i>=size || is==null) throw new NoSuchElementException();
                FileVertex.PEdge edge;
                try {
                    int target=is.readInt();
                    double weight=is.readDouble();
                    FileVertex t=vertexIndexResolver.getVertexById(target);
                    edge = new FileVertex.PEdge(t, weight);
                    i++;
                    if (i>=size) {
                        is.close();
                        is=null;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return edge;
            }
            @Override
            public void forEachRemaining(final Consumer<? super FileVertex.PEdge> action) {
                Objects.requireNonNull(action);
//                while (hasNext())
//                    action.accept(next());
                try {
                    if (is!=null) {// todo переиспользовать is.
                        is.close();
                        is=null;
                    }
                    EdgeHolder.this.forEach(// java 1.8:(target, weight)-> {
                      new EdgeVisitor() {
                        @Override
                        public void acceptEdge(int target, double weight) {
                            FileVertex t=vertexIndexResolver.getVertexById(target);
                            action.accept(new FileVertex.PEdge(t, weight));
                    }});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void finalize() throws Throwable {
                try {
                    if (is!=null) is.close();
                } finally {
                    super.finalize();
                }
            }
        };
    }
    
    public long size() {
        //assert file.length()==size*BLOCK_SIZE;
        //assert filePos==file.getFilePointer();
        return size;
    }
    
    public void clear() throws IOException {
        if (os!=null) {
            os.close();
            os=new UnsyncBufferedOutputStream(new FileOutputStream(file));
            flushed=true;
        }
        filePos=0;
        size=0;
    }
    
    @Override
    public void close() throws IOException {
        assert filePos==file.length();
        try {
          if (os!=null) os.close();
          os=null;
        } finally {
          if (is!=null) is.close();
          is=null;
        }
        flushed=true;
    }
    // -- buffered stream --
    
    /* // -- direct file -- 
    private final RandomAccessFile file;

    public EdgeHolder(File fileName) throws IOException {
        file=new RandomAccessFile(fileName, "rw");
        size=0;
        filePos=file.getFilePointer(); // default 0
    }
    
    public void addEdge(int toVertex, double weight) throws IOException {
        if (filePos!=size*BLOCK_SIZE) {
            file.seek(filePos=size*BLOCK_SIZE);
        }
        file.writeInt(toVertex);
        file.writeDouble(weight);
        filePos+=BLOCK_SIZE;
        size++;
    }
    
    public int getTarget(long edgeId) throws IOException {
        if (filePos!=edgeId*BLOCK_SIZE) {
            file.seek(filePos=edgeId*BLOCK_SIZE);
        }
        filePos+=4;
       return file.readInt();
    }
    public double getWeight(long edgeId) throws IOException {
        if (filePos!=4+edgeId*BLOCK_SIZE) {
            file.seek(filePos=4+edgeId*BLOCK_SIZE);
        }
        filePos+=8;
       return file.readDouble();
    }

    public void forEach(EdgeVisitor visitor) throws IOException {
       //fixme ??? assert file.length()==size*BLOCK_SIZE;
       assert filePos==file.getFilePointer();
       file.seek(filePos=0);
       for (long i=0;i<size;i++) { // or while not hrow EOFException
           int target=file.readInt();
           double weight=file.readDouble();
           filePos+=BLOCK_SIZE;
           visitor.acceptEdge(target, weight);
       }
       assert filePos==file.getFilePointer();
    }
    
    public long size() {
        //assert file.length()==size*BLOCK_SIZE;
        //assert filePos==file.getFilePointer();
        return size;
    }
    
    public void clear() throws IOException {
        file.setLength(0L);
        filePos=0;
        size=0;
    }
    
    @Override
    public void close() throws IOException {
        assert filePos==file.getFilePointer();
        file.close();
    }
    // -- direct file -- (C) Aleksey K. 2020 */


    public interface EdgeVisitor {
        void acceptEdge(/*long edgeId,*/ int target, double weight);
    }
}
