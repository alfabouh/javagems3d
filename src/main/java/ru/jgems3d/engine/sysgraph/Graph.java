package ru.jgems3d.engine.sysgraph;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.files.JGPath;
import ru.jgems3d.exceptions.JGemsException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Graph implements Serializable {
    private final Map<GVertex, List<GEdge>> graph;
    private GVertex start;

    public Graph() {
        this.graph = new HashMap<>();
        this.start = null;
    }

    public static void saveInFile(Graph graph, String name) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(name + ".nmesh");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(graph);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new JGemsException(e);
        }
    }

    public static Graph readFromFile(JGPath path) {
        InputStream inputStream = JGems.loadFileJarSilently(path);
        if (inputStream == null) {
            JGemsHelper.getLogger().warn("Couldn't find file " + path);
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Graph graph1 = (Graph) objectInputStream.readObject();
            objectInputStream.close();
            inputStream.close();
            return graph1;
        } catch (IOException | ClassNotFoundException e) {
            throw new JGemsException(e);
        }
    }

    public GVertex getStart() {
        return this.start;
    }

    public void addVertex(GVertex gVertex) {
        this.getGraphContainer().putIfAbsent(gVertex, new ArrayList<>());
        if (this.getStart() == null) {
            this.start = gVertex;
        }
    }

    public void addEdge(GVertex vertex1, GVertex vertex2, float w) {
        GEdge edge = new GEdge(vertex2, w);
        this.getGraphContainer().get(vertex1).add(edge);
    }

    public void addEdge(GVertex vertex1, GVertex vertex2) {
        if (vertex2 == null) {
            return;
        }
        GEdge edge = new GEdge(vertex2);
        List<GEdge> edges = this.getGraphContainer().get(vertex1);
        if (edges != null) {
            for (GEdge edge1 : edges) {
                if (edge1.getTarget().equals(vertex2)) {
                    return;
                }
            }
            edges.add(edge);
        }
    }

    public List<GEdge> getNeighbors(GVertex vertex) {
        return this.getGraphContainer().get(vertex);
    }

    public Map<GVertex, List<GEdge>> getGraphContainer() {
        return this.graph;
    }

    @SuppressWarnings("all")
    public GVertex getRandomVertex() {
        Random random = new Random();
        Set<GVertex> set = this.graph.keySet();
        return set.stream().skip(ThreadLocalRandom.current().nextInt(set.size())).findAny().get();
    }

    @SuppressWarnings("all")
    public GVertex getClosestVertex(Vector3f pos) {
        GVertex minVer = this.getGraphContainer().keySet().stream().findFirst().get();
        for (GVertex vertex : this.getGraphContainer().keySet()) {
            if (vertex.distanceTo(pos) < minVer.distanceTo(pos)) {
                minVer = vertex;
            }
        }
        return minVer;
    }

    public static class GEdge implements Serializable {
        private final GVertex target;
        private final float weight;

        public GEdge(GVertex vertex) {
            this(vertex, 1.0f);
        }

        public GEdge(GVertex vertex, float weight) {
            this.target = vertex;
            this.weight = weight;
        }

        public float getWeight() {
            return this.weight;
        }

        public GVertex getTarget() {
            return this.target;
        }
    }

    public static class GVertex implements Serializable {
        private final float x;
        private final float y;
        private final float z;
        private float g;
        private float h;
        private float f;
        private GVertex parent;

        public GVertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.g = 0;
            this.f = 0;
        }

        public double distanceTo(GVertex vertex) {
            return new Vector3f(this.getX(), this.getY(), this.getZ()).distance(vertex.getX(), vertex.getY(), vertex.getZ());
        }

        public double distanceTo(Vector3f vector3f) {
            return new Vector3f(this.getX(), this.getY(), this.getZ()).distance(vector3f);
        }

        public float getG() {
            return this.g;
        }

        public void setG(float g) {
            this.g = g;
        }

        public float getH() {
            return this.h;
        }

        public void setH(float h) {
            this.h = h;
        }

        public double getF() {
            return this.f;
        }

        public void setF(float f) {
            this.f = f;
        }

        public GVertex getParent() {
            return this.parent;
        }

        public void setParent(GVertex parent) {
            this.parent = parent;
        }

        public synchronized float getX() {
            return this.x;
        }

        public synchronized float getY() {
            return this.y;
        }

        public synchronized float getZ() {
            return this.z;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Double.hashCode(this.getX());
            result = prime * result + Double.hashCode(this.getY());
            result = prime * result + Double.hashCode(this.getZ());
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GVertex)) {
                return false;
            }
            GVertex vertex = (GVertex) o;
            return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(vertex.x) && Double.doubleToLongBits(this.y) == Double.doubleToLongBits(vertex.y) && Double.doubleToLongBits(this.z) == Double.doubleToLongBits(vertex.z);
        }

        public String toString() {
            return this.getX() + " " + this.getY() + " " + this.getZ();
        }
    }
}
