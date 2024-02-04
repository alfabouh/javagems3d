package ru.BouH.engine.render.scene.objects;

import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.render.frustum.ICullable;
import ru.BouH.engine.render.scene.fabric.render_data.ModelRenderParams;

public interface IModeledSceneObject extends IRenderObject, ICullable {
    Model<Format3D> getModel3D();
    ModelRenderParams getModelRenderParams();
    boolean isVisible();
}
