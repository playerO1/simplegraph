package simplegraph4j.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import simplegraph4j.IEdge;
import simplegraph4j.ISimpleGraph;
import simplegraph4j.IVertex;

/**
 * Draw graph link
 * @author A.K.
 */
public class GraphVisualizator {
    //Logger log=LoggerFactory.getLogger(this.getClass());
    
    Color color_background=Color.WHITE;
    Color color_point=Color.RED;
    Color color_line=new Color(25,0,255, 20);
    boolean randomize_coord=true; // неровное расположение точек
    boolean randomize_layer=false; // смешать слои
    boolean line_weight=true; // учитывать вес линии
    
    
    protected Color colorMap(double pos) {
//        assert pos>=0;
//        assert pos<=1.0;
        if (pos>1 || pos<0) {
            //log.warn("color pos={}",pos);
            if (pos>1) pos=1;
            if (pos<0) pos=0;
        }
        return new Color(25,(int)(255.0*pos),255, (int)(5+100.0*pos));
    }
    
    protected double mathW(double x) { // нормализация перед расчётом.
        return x; // Math.log(x);
    }
    
    public <T> BufferedImage vizualize(int width, int height, ISimpleGraph<T> graph, T startPoint, List<T> markPoint) {
        IVertex<T> startV=startPoint!=null?graph.vertexForObejct(startPoint):null;
        List<IVertex<T>> markV=null;
        if (markPoint!=null) {
            markV=new ArrayList<>();
            for (T obj:markPoint) markV.add(graph.vertexForObejct(obj));
        }
        return vizualize(width, height, graph, startV, markV);
    }
    
    /**
     * Draw graph
     * @param width  image size
     * @param height image size
     * @param graph  draw this
     * @param startPoint start point for directed graph
     * @param markPoint mark this vertex. Can be null.
     * @return 
     */
    public <T> BufferedImage vizualize(int width, int height, ISimpleGraph<T> graph, IVertex<T> startPoint, List<IVertex<T>> markPoint) {
        List<List<IVertex>> graphLayers=new ArrayList<>(); // Упорядоченный список слоёв
        //log.trace("Group graph vertex for drawing...");
        // 1. Разбить/сгруппировать вершины по слоям
        double minW, maxW;
       {
        Set<IVertex> markVertex=new HashSet<IVertex>(); // jnvtxtyyst dthibys
        if (randomize_layer) { // в общую кучу, без учёта слоёв
            List<IVertex> prevLayer=new ArrayList<>();
            prevLayer.addAll(graph.getAllVertex());
            graphLayers.add(prevLayer);
            markVertex.addAll(prevLayer);
            if (line_weight) {
                final double minMax[]={Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
                for (IVertex vertex:prevLayer) {
                    //java 1.8: vertex.getAdjacencies().forEach((edge)-> {
                    for (Object edge:vertex.getAdjacencies()) {
                        double w=((IEdge)edge).getWeight();
                        w=mathW(w);
                        if (minMax[0]>w) minMax[0]=w;
                        if (minMax[1]<w) minMax[1]=w;
                    };
                }
                minW=minMax[0];
                maxW=minMax[1];
            } else {
                minW = maxW = Double.NaN;
            }
        } else { // Разделять по слоям
            List<IVertex> prevLayer=new ArrayList<>();
            if (startPoint!=null) prevLayer.add(startPoint);
            else prevLayer.add(graph.getAllVertex().iterator().next()); // fixme может быть все или половину?
            markVertex.addAll(prevLayer);
            final double minMax[]={Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
            while (!prevLayer.isEmpty()) {
                graphLayers.add(prevLayer);
                // 1.1 составить список следующих слоёв
                List<IVertex> nextLayer=new ArrayList<>();
                for (IVertex vertex:prevLayer) {
                    // jaa 1.8: vertex.getAdjacencies().forEach((edge)-> {
                    for (Object edge:vertex.getAdjacencies()) {
                        IVertex to = ((IEdge)edge).getTarget();
                        if (!markVertex.contains(to)) {
                            nextLayer.add(to);
                            markVertex.add(to);
                            double w=((IEdge)edge).getWeight();
                            w=mathW(w);
                            if (minMax[0]>w) minMax[0]=w;
                            if (minMax[1]<w) minMax[1]=w;
                        }
                    };
                }
                prevLayer=nextLayer;
            }
            minW=minMax[0];
            maxW=minMax[1];
        }
        // validate, ничего не потеряли - проверка
        List<IVertex> lostVertex=new ArrayList<>();
        for (Object vertex:graph.getAllVertex()) {
            IVertex v=(IVertex) vertex;
            if (!markVertex.contains(v)) {
                lostVertex.add(v);
                markVertex.add(v);
            }
        }
        if (!lostVertex.isEmpty()) {
            //log.warn("Found {} lost vertex when split by layer", lostVertex.size());
            graphLayers.add(lostVertex);
        }
       }

        // 2. Разложить точки по координатам (graphLayers)
        //log.trace("Group by {} layers. Calculate x,y...", graphLayers.size());
        Map<IVertex, Point> coord=new HashMap<>();
        int between=width/graphLayers.size();
        for (int i=0;i<graphLayers.size();i++) {
            int x=i*between;
            List<IVertex> layer=graphLayers.get(i);
            for (int j=0;j<layer.size();j++) {
                int y=j*height/(layer.size()+1);
                if (randomize_coord) {
                    double randRange=randomize_layer? 1.0 : 0.3; // 1/3 между слоями, или весь экран
                    x=i*between+(int)(between*randRange*Math.random());
                    if (x<0) x=0;
                    if (x>=width) x=width-1;
                }
                coord.put(layer.get(j), new Point(x,y));
            }
        }
        
        //log.trace("Make image {}x{}.", width, height);
        BufferedImage image=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // todo or TYPE_INT_RGB?
        Graphics2D canvas=image.createGraphics();
        canvas.setBackground(color_background);
        canvas.clearRect(0, 0, width, height);
        if (graph.edges()>5000) {
            //log.trace("Too many edges ({}), switch to low quality line", graph.edges());
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            canvas.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            canvas.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        }
        // 3. нарисовать линии
        //log.trace("Draw lines...");
        canvas.setColor(color_line);
        for (int i=0;i<graphLayers.size();i++) {
            //log.trace("Draw lines of layer {}/{}...", i, graphLayers.size());
            List<IVertex> layer=graphLayers.get(i);
            for (int j=0;j<layer.size();j++) {
                IVertex v1=layer.get(j);
                Point p1=coord.get(v1);
                // java 1.8: v1.getAdjacencies().forEach((edge)->{
                for (Object edge:v1.getAdjacencies()) {
                    IVertex v2=((IEdge)edge).getTarget();
                    Point p2=coord.get(v2);
                    if (line_weight) {
                        double w=((IEdge)edge).getWeight();//todo use W or color/alpha
                        w=mathW(w);
                        double l=1.0-((w-minW)/(maxW-minW)); // приведение в диапазон/нормализация
                        Color color=colorMap(l);
                        canvas.setColor(color);
                    }
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y);
                };
            }
        }
        
        // 4. нарисовать поверх линий точки
        //log.trace("Draw vertex points...");
        canvas.setColor(color_point);
        for (Point p:coord.values()) {
            image.setRGB(p.x, p.y, color_point.getRGB());
            //canvas.draw(p);
        }
        if (markPoint!=null && !markPoint.isEmpty()) {
            for (IVertex v:markPoint) {
                Point p=coord.get(v);
                if (p==null) {
                    //log.warn("Mark vertex is not in graph: {}", v);
                    continue;
                }
                final int size=4;
                canvas.drawOval(p.x-size/2, p.y-size/2, size,size);
            }
        }
        
        //log.trace("Image done.");
        return image;
    }
}
