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

package javagems3d.graphics.opengl.rendering.items;

import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.properties.ModelRenderData;

public interface IModeledSceneObject extends IRenderObject, ICulled, ILightsKeeper {
    Model<Format3D> getModel();

    ModelRenderData getMeshRenderData();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    boolean isVisible();
}
