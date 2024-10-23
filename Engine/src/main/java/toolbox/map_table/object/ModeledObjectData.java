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

package toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;
import toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import toolbox.render.scene.items.renderers.ModeledObject3DRenderer;
import toolbox.resources.shaders.manager.TBoxShaderManager;

public class ModeledObjectData extends AbstractObjectData {
    public ModeledObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshGroup meshGroup, ObjectCategory objectCategory) {
        super(attributesContainer, shaderManager, meshGroup, objectCategory);
    }

    public ModeledObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshGroup meshGroup, ObjectCategory objectCategory) {
        super(shaderManager, meshGroup, objectCategory);
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new ModeledObject3DRenderer();
    }
}
