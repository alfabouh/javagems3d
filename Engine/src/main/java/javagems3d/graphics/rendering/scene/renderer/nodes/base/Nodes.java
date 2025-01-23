package javagems3d.graphics.rendering.scene.renderer.nodes.base;

public enum Nodes {
    DEFERRED_RENDER_PASS("d-pass", 0),
    FORWARD_RENDER_PASS("f-pass", 1),
    TRANSPARENCY_RENDER_PASS("transparency-pass", 2),
    UI_RENDER_PASS("ui-pass", 3),
    GLUING_RENDER_PASS("gluing-pass", 4),
    POST_EFFECTS_RENDER_PASS("post-fx-pass", 5);

    private final String name;
    private final int id;

    Nodes(String name, int id) {
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
