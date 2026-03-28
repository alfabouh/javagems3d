package api.scripting.coding.env.internal.util.world.render.world;

import javagems3d.graphics.world.SceneWorld;

public class JSSceneWorld {
    private final SceneWorld sceneWorld;

    public JSSceneWorld(SceneWorld sceneWorld) {
        this.sceneWorld = sceneWorld;
    }

    public SceneWorld getJavaSceneWorld() {
        return this.sceneWorld;
    }
}
