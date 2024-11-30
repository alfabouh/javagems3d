package javagems3d.graphics.rendering.scene.renderer.nodes.groups;

public enum NodesGroup {
    DEFERRED_R_PASS_GROUP("d-pass", 0),
    FORWARD_R_PASS_GROUP("f-pass", 1),
    TRANSPARENCY_PASS("transparency-pass", 2),
    UI_PASS("ui-pass", 3),
    GLUING_PASS_GROUP("gluing-pass", 4),
    POST_EFFECTS_PASS_GROUP("post-fx-pass", 5);

    private final String name;
    private final int id;

    NodesGroup(String name, int id) {
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
