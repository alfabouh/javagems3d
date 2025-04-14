package api.application.workbench.resources.data.jgems;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JGemsEntityData implements IJGemsObjectData {
    private final JGemsPath pathToModel;
    private final EntityRenderData entityRenderData;

    public JGemsEntityData(@Nullable JGemsPath pathToModel, @NotNull EntityRenderData entityRenderData) {
        this.entityRenderData = entityRenderData;
        this.pathToModel = pathToModel;
    }

    public JGemsEntityData(@Nullable JGemsPath pathToModel) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    public JGemsEntityData() {
        this(null, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    public JGemsPath getPathToModel() {
        return pathToModel;
    }

    public EntityRenderData getEntityRenderData() {
        return entityRenderData;
    }
}
