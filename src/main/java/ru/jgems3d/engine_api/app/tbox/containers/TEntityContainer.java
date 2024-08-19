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

package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

/**
 * This class represents the properties of an object inside the ToolBox system (its render and attributes)
 */
public final class TEntityContainer {
    private final Class<? extends AbstractObjectData> abstractObjectDataClass;
    private final JGemsPath pathToTBoxShader;
    private final JGemsPath pathToTBoxModel;
    private final AttributesContainer attributesContainer;
    private final ObjectCategory objectCategory;

    public TEntityContainer(@NotNull Class<? extends AbstractObjectData> abstractObjectDataClass, @NotNull JGemsPath pathToTBoxModel, @NotNull AttributesContainer attributesContainer, @NotNull ObjectCategory objectCategory) {
        this(abstractObjectDataClass, null, pathToTBoxModel, attributesContainer, objectCategory);
    }

    public TEntityContainer(@NotNull Class<? extends AbstractObjectData> abstractObjectDataClass, @Nullable JGemsPath pathToTBoxShader, @NotNull JGemsPath pathToTBoxModel, @NotNull AttributesContainer attributesContainer, @NotNull ObjectCategory objectCategory) {
        this.abstractObjectDataClass = abstractObjectDataClass;
        this.pathToTBoxModel = pathToTBoxModel;
        this.pathToTBoxShader = pathToTBoxShader;
        this.attributesContainer = attributesContainer;
        this.objectCategory = objectCategory;
    }

    /**
     * A class based on which an object is created that represents an object in theTBox scene
     */
    public Class<? extends AbstractObjectData> getAbstractObjectDataClass() {
        return this.abstractObjectDataClass;
    }

    /**
     * Path to object's model
     */
    public JGemsPath getPathToTBoxModel() {
        return this.pathToTBoxModel;
    }

    /**
     * Object's category. You can make your own categories. In the program, objects are sorted by categories
     */
    public ObjectCategory getObjectCategory() {
        return this.objectCategory;
    }

    /**
     * Path to object's TBox shader
     */
    public JGemsPath getPathToTBoxShader() {
        return this.pathToTBoxShader;
    }

    /**
     * Object's attributes. If you want to get the attribute value, you can do it like this
     * <pre>
     *     {@code
     *           Vector3f pos = attributesContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
     *     }
     * </pre>
     */
    public AttributesContainer getAttributeContainer() {
        return this.attributesContainer;
    }
}
