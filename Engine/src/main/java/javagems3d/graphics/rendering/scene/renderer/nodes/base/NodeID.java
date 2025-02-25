package javagems3d.graphics.rendering.scene.renderer.nodes.base;

public final class NodeID {
    private final String name;
    private final int id;

    public NodeID(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}
