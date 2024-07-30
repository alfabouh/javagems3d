package ru.jgems3d.engine.graphics.opengl.rendering.items;

import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;

public interface IModeledSceneObjectKeeper extends IRenderObject, ICulled, ILightsKeeper {
    Model<Format3D> getModel();

    MeshRenderData getMeshRenderData();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    boolean isVisible();
}
