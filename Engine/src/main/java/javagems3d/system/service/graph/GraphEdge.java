package javagems3d.system.service.graph;

import java.io.Serializable;

public record GraphEdge(GraphVertex target, float weight) implements Serializable {
    private static final long serialVersionUID = -228L;

    public GraphEdge(GraphVertex vertex) {
        this(vertex, 1.0f);
    }

}
