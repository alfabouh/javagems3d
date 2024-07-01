package ru.alfabouh.jgems3d.mapsys.toolbox.table.object;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes.AttributeContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.MarkerObject3DRenderer;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class MarkerObjectData extends AbstractObjectData {
    public MarkerObjectData(@NotNull AttributeContainer attributeContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectType objectType) {
        super(attributeContainer, shaderManager, meshDataGroup, objectType);
    }

    public MarkerObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectType objectType) {
        super(shaderManager, meshDataGroup, objectType);
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new MarkerObject3DRenderer();
    }
}
