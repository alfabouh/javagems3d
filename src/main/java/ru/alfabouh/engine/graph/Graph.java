package ru.alfabouh.engine.graph;

import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;

import java.io.*;
import java.util.*;

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
            throw new GameException(e);
        }
    }

    public static Graph readFromFile(String name) {
        String path = "/assets/map/" + name + ".nmesh";
        InputStream inputStream = Game.loadFileJarSilently(path);
        if (inputStream == null) {
            Game.getGame().getLogManager().warn("Couldn't find file " + path);
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Graph graph1 = (Graph) objectInputStream.readObject();
            objectInputStream.close();
            inputStream.close();
            return graph1;
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException(e);
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

    public void addEdge(GVertex vertex1, GVertex vertex2, double w) {
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
        return set.stream().skip(Game.getGame().random.nextInt(set.size() - 1)).findFirst().get();
    }

    @SuppressWarnings("all")
    public GVertex getClosestVertex(Vector3d pos) {
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
        private final double weight;

        public GEdge(GVertex vertex) {
            this(vertex, 1.0d);
        }

        public GEdge(GVertex vertex, double weight) {
            this.target = vertex;
            this.weight = weight;
        }

        public double getWeight() {
            return this.weight;
        }

        public GVertex getTarget() {
            return this.target;
        }
    }

    public static class GVertex implements Serializable {
        private final double x;
        private final double y;
        private final double z;
        private double g;
        private double h;
        private double f;
        private GVertex parent;

        public GVertex(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.g = 0;
            this.f = 0;
        }

        public double distanceTo(GVertex vertex) {
            return new Vector3d(this.getX(), this.getY(), this.getZ()).distance(vertex.getX(), vertex.getY(), vertex.getZ());
        }

        public double distanceTo(Vector3d vector3d) {
            return new Vector3d(this.getX(), this.getY(), this.getZ()).distance(vector3d);
        }

        public double getG() {
            return this.g;
        }

        public void setG(double g) {
            this.g = g;
        }

        public double getH() {
            return this.h;
        }

        public void setH(double h) {
            this.h = h;
        }

        public double getF() {
            return this.f;
        }

        public void setF(double f) {
            this.f = f;
        }

        public GVertex getParent() {
            return this.parent;
        }

        public void setParent(GVertex parent) {
            this.parent = parent;
        }

        public synchronized double getX() {
            return this.x;
        }

        public synchronized double getY() {
            return this.y;
        }

        public synchronized double getZ() {
            return this.z;
        }

        @Override
        public int hashCode() {
            double result = this.x;
            result = 31 * result + this.y + 28 * this.z;
            return (int) result;
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
