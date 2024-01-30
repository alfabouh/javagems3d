package ru.BouH.engine.render.scene.scene_render;

public class RenderGroup {
    private final String id;
    private final boolean mainSceneGroup;

    public RenderGroup(String id, boolean mainSceneGroup) {
        this.id = id;
        this.mainSceneGroup = mainSceneGroup;
    }

    public boolean isMainSceneGroup() {
        return this.mainSceneGroup;
    }

    public String getId() {
        return this.id;
    }
}
