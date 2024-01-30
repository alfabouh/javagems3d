package ru.BouH.engine.render.scene;

import ru.BouH.engine.render.scene.world.SceneWorld;

import java.util.List;

public interface IScene {
    SceneWorld getSceneWorld();

    List<SceneRenderBase> getRenderQueueContainer();
}
