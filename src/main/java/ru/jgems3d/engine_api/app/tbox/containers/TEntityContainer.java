package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.system.service.file.JGemsPath;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributesContainer;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

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

    public Class<? extends AbstractObjectData> getAbstractObjectDataClass() {
        return this.abstractObjectDataClass;
    }

    public JGemsPath getPathToTBoxModel() {
        return this.pathToTBoxModel;
    }

    public ObjectCategory getObjectCategory() {
        return this.objectCategory;
    }

    public JGemsPath getPathToTBoxShader() {
        return this.pathToTBoxShader;
    }

    public AttributesContainer getAttributeContainer() {
        return this.attributesContainer;
    }
}
