package ru.jgems3d.toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.jgems3d.toolbox.render.scene.items.renderers.MarkerObject3DRenderer;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class MarkerObjectData extends AbstractObjectData {
    public MarkerObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        super(attributesContainer, shaderManager, meshDataGroup, objectCategory);
    }

    public MarkerObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        super(shaderManager, meshDataGroup, objectCategory);
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new MarkerObject3DRenderer();
    }
}
