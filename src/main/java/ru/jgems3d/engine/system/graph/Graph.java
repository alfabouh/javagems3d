package ru.jgems3d.engine.system.graph;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.exceptions.JGemsNullException;
import ru.jgems3d.engine.system.service.misc.JGPath;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Graph implements Serializable {
    private static final long serialVersionUID = -228L;
    private final Map<GraphVertex, List<GraphEdge>> graph;
    private final Map<GraphChunk, List<GraphVertex>> graphChunkGroups;
    private GraphVertex start;

    public Graph() {
        this.graph = new HashMap<>();
        this.graphChunkGroups = new HashMap<>();
        this.start = null;
    }

    public static void saveInFile(Graph graph, String name) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(name + ".nav");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(graph);
        fileOutputStream.close();
    }

    public static Graph readFromFile(JGPath path) throws IOException, ClassNotFoundException {
        try (InputStream inputStream = JGems3D.loadFileFromJar(path)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                return (Graph) objectInputStream.readObject();
            }
        }
    }

    public GraphVertex getStart() {
        return this.start;
    }

    public void addVertex(GraphVertex gVertex) {
        this.getGraphContainer().putIfAbsent(gVertex, new ArrayList<>());
        GraphChunk graphChunk = GraphChunk.getChunkIJByCoordinates(gVertex.getPosition());
        if (this.getGraphChunkGroups().containsKey(graphChunk)) {
            this.getGraphChunkGroups().get(graphChunk).add(gVertex);
        } else {
            this.getGraphChunkGroups().put(graphChunk, new ArrayList<GraphVertex>() {{ add(gVertex); }});
        }
        if (this.getStart() == null) {
            this.start = gVertex;
        }
    }

    public void addEdge(GraphVertex vertex1, GraphVertex vertex2, float w) {
        GraphEdge edge = new GraphEdge(vertex2, w);
        this.getGraphContainer().get(vertex1).add(edge);
    }

    public void addEdge(GraphVertex vertex1, GraphVertex vertex2) {
        if (vertex2 == null) {
            return;
        }
        GraphEdge edge = new GraphEdge(vertex2);
        List<GraphEdge> edges = this.getGraphContainer().get(vertex1);
        if (edges != null) {
            for (GraphEdge edge1 : edges) {
                if (edge1.getTarget().equals(vertex2)) {
                    return;
                }
            }
            edges.add(edge);
        }
    }

    public List<GraphEdge> getNeighbors(GraphVertex vertex) {
        return this.getGraphContainer().get(vertex);
    }

    public Map<GraphChunk, List<GraphVertex>> getGraphChunkGroups() {
        return this.graphChunkGroups;
    }

    public Map<GraphVertex, List<GraphEdge>> getGraphContainer() {
        return this.graph;
    }

    @SuppressWarnings("all")
    public GraphVertex getRandomVertex() {
        Random random = new Random();
        Set<GraphVertex> set = this.graph.keySet();
        return set.stream().skip(ThreadLocalRandom.current().nextInt(set.size())).findAny().get();
    }

    public GraphChunk getVertexChunk(GraphVertex vertex) {
        return GraphChunk.getChunkIJByCoordinates(vertex.getPosition());
    }

    public List<GraphVertex> getVerticesInChunk(GraphChunk graphChunk) {
        return this.getGraphChunkGroups().get(graphChunk);
    }

    public List<GraphVertex> getVerticesInChunk(Vector3f worldPos) {
        return this.getGraphChunkGroups().get(GraphChunk.getChunkIJByCoordinates(worldPos));
    }

    @SuppressWarnings("all")
    public GraphVertex getClosestVertex(Vector3f pos) {
        if (this.getGraphChunkGroups().isEmpty()) {
            return null;
        }
        GraphChunk currChunk = GraphChunk.getChunkIJByCoordinates(pos);
        if (!this.getGraphChunkGroups().containsKey(currChunk)) {
            float minDist = Float.MAX_VALUE;
            currChunk = null;
            for (GraphChunk graphChunk : this.getGraphChunkGroups().keySet()) {
                float dist = pos.distance(GraphChunk.getChunkPos(graphChunk, pos.y));
                if (currChunk == null || dist < minDist) {
                    minDist = dist;
                    currChunk = graphChunk;
                }
            }
        }
        List<GraphVertex> vertexList = this.getVerticesInChunk(currChunk);
        GraphVertex minVer = vertexList.get(0);
        for (GraphVertex vertex : vertexList) {
            if (vertex.distanceTo(pos) < minVer.distanceTo(pos)) {
                minVer = vertex;
            }
        }
        return minVer;
    }
}
