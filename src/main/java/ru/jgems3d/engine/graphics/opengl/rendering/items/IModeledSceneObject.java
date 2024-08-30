/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.rendering.items;

import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;

public interface IModeledSceneObject extends IRenderObject, ICulled, ILightsKeeper {
    Model<Format3D> getModel();

    MeshRenderData getMeshRenderData();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    boolean isVisible();
}
