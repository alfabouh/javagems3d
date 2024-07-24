package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items;

import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;

public interface IModeledSceneObject extends IRenderObject, ICulled, IKeepLights {
    Model<Format3D> getModel();

    ModelRenderParams getModelRenderParams();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    boolean isVisible();
}
