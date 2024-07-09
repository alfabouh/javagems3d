package ru.alfabouh.jgems3d.toolbox.map_table.object;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public abstract class AbstractObjectData {
    private final TBoxShaderManager shaderManager;
    private final MeshDataGroup meshDataGroup;
    private final ObjectType objectType;
    private final AttributeContainer attributeContainer;

    public AbstractObjectData(@NotNull AttributeContainer attributeContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectType objectType) {
        this.attributeContainer = attributeContainer;
        this.shaderManager = shaderManager;
        this.meshDataGroup = meshDataGroup;
        this.objectType = objectType;
    }

    public AbstractObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectType objectType) {
        this(new AttributeContainer(), shaderManager, meshDataGroup, objectType);
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

    public ObjectType objectType() {
        return this.objectType;
    }
}
