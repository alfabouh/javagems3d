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
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public abstract class AbstractObjectData {
    private final TBoxShaderManager shaderManager;
    private final MeshDataGroup meshDataGroup;
    private final ObjectCategory objectCategory;
    private final AttributesContainer attributesContainer;

    public AbstractObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        this.attributesContainer = attributesContainer;
        this.shaderManager = shaderManager;
        this.meshDataGroup = meshDataGroup;
        this.objectCategory = objectCategory;
    }

    public AbstractObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshDataGroup meshDataGroup, ObjectCategory objectCategory) {
        this(new AttributesContainer(), shaderManager, meshDataGroup, objectCategory);
    }

    public TBoxShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public abstract ITBoxObjectRenderer getObjectRenderer();

    public AttributesContainer getDefaultAttributeContainer() {
        return this.attributesContainer;
    }

    public AttributesContainer copyAttributeContainer() {
        return new AttributesContainer(this.getDefaultAttributeContainer());
    }

    public MeshDataGroup meshDataGroup() {
        return this.meshDataGroup;
    }

    public ObjectCategory objectType() {
        return this.objectCategory;
    }
}
