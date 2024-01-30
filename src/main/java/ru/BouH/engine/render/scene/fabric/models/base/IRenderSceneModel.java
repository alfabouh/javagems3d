package ru.BouH.engine.render.scene.fabric.models.base;

import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;

public interface IRenderSceneModel {
    void onRender(double partialTicks);
    ModelRenderConstraints getModelRenderConstraints();
    Model<Format3D> toRender();
}
