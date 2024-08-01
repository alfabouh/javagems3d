package ru.jgems3d.toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ModeledObject3DRenderer;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class ModeledObjectData extends AbstractObjectData {
    public ModeledObjectData(@NotNull AttributeContainer attributeContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        super(attributeContainer, shaderManager, meshDataGroup, objectCategory);
    }

    public ModeledObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        super(shaderManager, meshDataGroup, objectCategory);
    }

    @Override
    public ITBoxObjectRenderer getObjectRenderer() {
        return new ModeledObject3DRenderer();
    }
}