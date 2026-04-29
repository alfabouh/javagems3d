package api.application.workbench.resources.data.jgems;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JGemsEntityData(JGemsPathSource pathToModel, EntityRenderData entityRenderData) implements IJGemsObjectData {
    public JGemsEntityData(@Nullable JGemsPathSource pathToModel, @NotNull EntityRenderData entityRenderData) {
        this.entityRenderData = entityRenderData;
        this.pathToModel = pathToModel;
    }

    public JGemsEntityData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties) {
        this(pathToModel, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, RenderAttributes.getDefaultIndirect(renderProperties)));
    }

    public JGemsEntityData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    public JGemsEntityData() {
        this(null, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    @SuppressWarnings("all")
    public <T extends RenderProperties> T getRenderPropertiesUnsafeCast() {
        return (T) this.entityRenderData().getObjectRenderAttributes().getProperties();
    }
}
