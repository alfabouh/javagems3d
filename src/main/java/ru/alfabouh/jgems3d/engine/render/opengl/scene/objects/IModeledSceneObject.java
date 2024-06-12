package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects;

import ru.alfabouh.jgems3d.engine.render.opengl.frustum.ICullable;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;

public interface IModeledSceneObject extends IRenderObject, ICullable {
    Model<Format3D> getModel3D();
    ModelRenderParams getModelRenderParams();
    boolean isVisible();
}
