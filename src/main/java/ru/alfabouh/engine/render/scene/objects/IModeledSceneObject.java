package ru.alfabouh.engine.render.scene.objects;

import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.render.frustum.ICullable;
import ru.alfabouh.engine.render.scene.fabric.render.data.ModelRenderParams;

public interface IModeledSceneObject extends IRenderObject, ICullable {
    Model<Format3D> getModel3D();
    ModelRenderParams getModelRenderParams();
    boolean isVisible();
}
