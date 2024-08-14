package ru.jgems3d.engine.system.graph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.system.service.json.JSONGraphDeserializer;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.logger.managers.LoggingManager;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Graph implements Serializable {
    private static final long serialVersionUID = -228L;
    private Map<GraphVertex, List<GraphEdge>> graph;
    private Map<GraphChunk, List<GraphVertex>> graphChunkGroups;
    private GraphVertex start;

    public Graph() {
        this.graph = new HashMap<>();
        this.graphChunkGroups = new HashMap<>();
        this.start = null;
    }

    public static void saveInFile(Graph graph){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(graph);
        try (FileWriter fileWriter = new FileWriter("nav.mesh")) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            LoggingManager.showExceptionDialog("Couldn't save NavMesh!");
        }
    }

    public static Graph readFromFile(JGemsPath path) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Reading NavMesh...");
        try (InputStream inputStream = JGems3D.loadFileFromJar(path)) {
            try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Graph.class, new JSONGraphDeserializer());
                Gson gson = gsonBuilder.create();
                return gson.fromJson(reader, Graph.class);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public void setGraphChunkGroups(Map<GraphChunk, List<GraphVertex>> graphChunkGroups) {
        this.graphChunkGroups = graphChunkGroups;
    }

    public void setGraph(Map<GraphVertex, List<GraphEdge>> graph) {
        this.graph = graph;
    }

    public void setStart(GraphVertex start) {
        this.start = start;
    }

    public void addVertex(GraphVertex gVertex) {
        this.getGraph().putIfAbsent(gVertex, new ArrayList<>());
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

    public void addEdge(GraphVertex vertex1, GraphVertex vertex2, float weight) {
        GraphEdge edge = new GraphEdge(vertex2, weight);
        this.getGraph().get(vertex1).add(edge);
    }

    public void addEdge(GraphVertex vertex1, GraphVertex vertex2) {
        if (vertex2 == null) {
            return;
        }
        GraphEdge edge = new GraphEdge(vertex2);
        List<GraphEdge> edges = this.getGraph().get(vertex1);
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
        return this.getGraph().get(vertex);
    }

    public synchronized Map<GraphChunk, List<GraphVertex>> getGraphChunkGroups() {
        return this.graphChunkGroups;
    }

    public synchronized Map<GraphVertex, List<GraphEdge>> getGraph() {
        return this.graph;
    }

    public GraphVertex getStart() {
        return this.start;
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
