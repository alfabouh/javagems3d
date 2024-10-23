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
import toolbox.resources.shaders.manager.TBoxShaderManager;

public abstract class AbstractObjectData {
    private final TBoxShaderManager shaderManager;
    private final MeshGroup meshGroup;
    private final ObjectCategory objectCategory;
    private final AttributesContainer attributesContainer;

    public AbstractObjectData(@NotNull AttributesContainer attributesContainer, @NotNull TBoxShaderManager shaderManager, @NotNull MeshGroup meshGroup, ObjectCategory objectCategory) {
        this.attributesContainer = attributesContainer;
        this.shaderManager = shaderManager;
        this.meshGroup = meshGroup;
        this.objectCategory = objectCategory;
    }

    public AbstractObjectData(@NotNull TBoxShaderManager shaderManager, @NotNull MeshGroup meshGroup, ObjectCategory objectCategory) {
        this(new AttributesContainer(), shaderManager, meshGroup, objectCategory);
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

    public MeshGroup meshDataGroup() {
        return this.meshGroup;
    }

    public ObjectCategory objectType() {
        return this.objectCategory;
    }
}
