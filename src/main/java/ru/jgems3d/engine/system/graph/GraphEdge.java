package ru.jgems3d.engine.system.graph;

import java.io.Serializable;

public class GraphEdge implements Serializable {
    private static final long serialVersionUID = -228L;
    private final GraphVertex target;
    private final float weight;

    public GraphEdge(GraphVertex vertex) {
        this(vertex, 1.0f);
    }

    public GraphEdge(GraphVertex vertex, float weight) {
        this.target = vertex;
        this.weight = weight;
    }

    public float getWeight() {
        return this.weight;
    }

    public GraphVertex getTarget() {
        return this.target;
    }
}
