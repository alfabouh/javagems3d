package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base;

public class RenderGroup {
    private final String id;

    public RenderGroup(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
