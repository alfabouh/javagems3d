package ru.alfabouh.engine.graph.pathfind;

import ru.alfabouh.engine.graph.Graph;

import java.util.*;

public class AStar {
    private final Graph graph;
    private final Graph.GVertex start;
    private final Graph.GVertex end;

    public AStar(Graph graph, Graph.GVertex start, Graph.GVertex end) {
        this.graph = graph;
        this.start = start;
        this.end = end;
    }

    public List<Graph.GVertex> findPath() {
        PriorityQueue<Graph.GVertex> openList = new PriorityQueue<>(Comparator.comparingDouble(Graph.GVertex::getF));
        HashSet<Graph.GVertex> closedList = new HashSet<>();

        this.start.setParent(null);
        this.start.setG(0);
        this.start.setH(this.heuristic(this.start, this.end));
        this.start.setF(start.getG() + start.getH());

        openList.add(this.start);

        while (!openList.isEmpty()) {
            Graph.GVertex current = openList.poll();

            if (current.equals(this.end)) {
                return this.buildPath(current);
            }

            closedList.add(current);

            for (Graph.GEdge edge : graph.getNeighbors(current)) {
                Graph.GVertex neighbor = edge.getTarget();

                if (closedList.contains(neighbor)) {
                    continue;
                }

                double tentativeG = current.getG() + edge.getWeight();

                if (!openList.contains(neighbor) || tentativeG < neighbor.getG()) {
                    neighbor.setParent(current);
                    neighbor.setG(tentativeG);
                    neighbor.setH(this.heuristic(neighbor, this.end));
                    neighbor.setF(neighbor.getG() + neighbor.getH());

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private List<Graph.GVertex> buildPath(Graph.GVertex vertex) {
        List<Graph.GVertex> path = new ArrayList<>();

        while (vertex != null) {
            path.add(vertex);
            vertex = vertex.getParent();
        }

        Collections.reverse(path);
        return path;
    }

    private double heuristic(Graph.GVertex v1, Graph.GVertex v2) {
        int dx = (int) Math.abs(v1.getX() - v2.getX());
        int dy = (int) Math.abs(v1.getZ() - v2.getZ());
        return dx + dy;
    }
}
