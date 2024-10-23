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
import toolbox.ToolBox;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;
import toolbox.render.scene.items.renderers.AABBZoneObject3DRenderer;
import toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import toolbox.resources.shaders.manager.TBoxShaderManager;

public class AABBZoneObjectData extends AbstractObjectData {
    public AABBZoneObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, ObjectCategory objectCategory) {
        super(attributesContainer, shaderManager, AABBZoneObjectData.zoneMesh(), objectCategory);
    }

    public AABBZoneObjectData(@NotNull TBoxShaderManager shaderManager, ObjectCategory objectCategory) {
        super(shaderManager, AABBZoneObjectData.zoneMesh(), objectCategory);
    }

    public static MeshGroup zoneMesh() {
        return ToolBox.get().getResourceManager().getModelResources().zone_cube;
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new AABBZoneObject3DRenderer();
    }
}
