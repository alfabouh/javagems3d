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

package ru.jgems3d.toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.render.scene.items.renderers.AABBZoneObject3DRenderer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.jgems3d.toolbox.render.scene.items.renderers.MarkerObject3DRenderer;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class AABBZoneObjectData extends AbstractObjectData {
    public AABBZoneObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, ObjectCategory objectCategory) {
        super(attributesContainer, shaderManager, AABBZoneObjectData.zoneMesh(), objectCategory);
    }

    public AABBZoneObjectData(@NotNull TBoxShaderManager shaderManager, ObjectCategory objectCategory) {
        super(shaderManager, AABBZoneObjectData.zoneMesh(), objectCategory);
    }

    public static MeshDataGroup zoneMesh() {
        return ToolBox.get().getResourceManager().getModelResources().zone_cube;
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new AABBZoneObject3DRenderer();
    }
}
