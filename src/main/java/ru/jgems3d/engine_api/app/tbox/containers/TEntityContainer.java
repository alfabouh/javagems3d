package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

public final class TEntityContainer {
    private final Class<? extends AbstractObjectData> abstractObjectDataClass;
    private final JGPath pathToTBoxShader;
    private final JGPath pathToTBoxModel;
    private final AttributeContainer attributeContainer;
    private final ObjectCategory objectCategory;

    public TEntityContainer(@NotNull Class<? extends AbstractObjectData> abstractObjectDataClass, @NotNull JGPath pathToTBoxModel, @NotNull AttributeContainer attributeContainer, @NotNull ObjectCategory objectCategory) {
        this(abstractObjectDataClass, null, pathToTBoxModel, attributeContainer, objectCategory);
    }

    public TEntityContainer(@NotNull Class<? extends AbstractObjectData> abstractObjectDataClass, @Nullable JGPath pathToTBoxShader, @NotNull JGPath pathToTBoxModel, @NotNull AttributeContainer attributeContainer, @NotNull ObjectCategory objectCategory) {
        this.abstractObjectDataClass = abstractObjectDataClass;
        this.pathToTBoxModel = pathToTBoxModel;
        this.pathToTBoxShader = pathToTBoxShader;
        this.attributeContainer = attributeContainer;
        this.objectCategory = objectCategory;
    }

    public Class<? extends AbstractObjectData> getAbstractObjectDataClass() {
        return this.abstractObjectDataClass;
    }

    public JGPath getPathToTBoxModel() {
        return this.pathToTBoxModel;
    }

    public ObjectCategory getObjectCategory() {
        return this.objectCategory;
    }

    public JGPath getPathToTBoxShader() {
        return this.pathToTBoxShader;
    }

    public AttributeContainer getAttributeContainer() {
        return this.attributeContainer;
    }
}
