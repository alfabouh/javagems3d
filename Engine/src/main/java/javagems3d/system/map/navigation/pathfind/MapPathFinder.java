/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.map.navigation.pathfind;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.graph.Graph;
import javagems3d.system.graph.GraphEdge;
import javagems3d.system.graph.GraphVertex;

import java.util.*;

public class MapPathFinder {
    private final Graph graph;
    private final GraphVertex start;
    private final GraphVertex end;

    public MapPathFinder(@NotNull Graph graph, GraphVertex start, GraphVertex end) {
        this.graph = graph;
        this.start = start;
        this.end = end;
    }

    public List<GraphVertex> findPath() {
        PriorityQueue<GraphVertex> openList = new PriorityQueue<>(Comparator.comparingDouble(GraphVertex::getF));
        HashSet<GraphVertex> closedList = new HashSet<>();

        this.start.setParent(null);
        this.start.setG(0);
        this.start.setH(this.heuristic(this.start, this.end));
        this.start.setF(start.getG() + start.getH());

        openList.add(this.start);

        while (!openList.isEmpty()) {
            GraphVertex current = openList.poll();

            if (current.equals(this.end)) {
                return this.buildPath(current);
            }

            closedList.add(current);

            for (GraphEdge edge : this.graph.getNeighbors(current)) {
                GraphVertex neighbor = edge.getTarget();

                if (closedList.contains(neighbor)) {
                    continue;
                }

                float tentativeG = current.getG() + edge.getWeight();

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

    private List<GraphVertex> buildPath(GraphVertex vertex) {
        List<GraphVertex> path = new ArrayList<>();

        while (vertex != null) {
            path.add(vertex);
            vertex = vertex.getParent();
        }

        Collections.reverse(path);
        if (path.isEmpty()) {
            return null;
        }
        return path;
    }

    private float heuristic(GraphVertex v1, GraphVertex v2) {
        int dx = (int) Math.abs(v1.getPosition().x - v2.getPosition().x);
        int dy = (int) Math.abs(v1.getPosition().y - v2.getPosition().y);
        int dz = (int) Math.abs(v1.getPosition().z - v2.getPosition().z);
        return dx + dy + dz;
    }
}
