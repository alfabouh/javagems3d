package ru.jgems3d.toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public abstract class AbstractObjectData {
    private final TBoxShaderManager shaderManager;
    private final MeshDataGroup meshDataGroup;
    private final ObjectCategory objectCategory;
    private final AttributeContainer attributeContainer;

    public AbstractObjectData(@NotNull AttributeContainer attributeContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        this.attributeContainer = attributeContainer;
        this.shaderManager = shaderManager;
        this.meshDataGroup = meshDataGroup;
        this.objectCategory = objectCategory;
    }

    public AbstractObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        this(new AttributeContainer(), shaderManager, meshDataGroup, objectCategory);
    }

    public TBoxShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public abstract ITBoxObjectRenderer getObjectRenderer();

    public AttributeContainer getDefaultAttributeContainer() {
        return this.attributeContainer;
    }

    public AttributeContainer copyAttributeContainer() {
        return new AttributeContainer(this.getDefaultAttributeContainer());
    }

    public MeshDataGroup meshDataGroup() {
        return this.meshDataGroup;
    }

    public ObjectCategory objectType() {
        return this.objectCategory;
    }
}
