package api.application.workbench.resources.data.jgems;

import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JGemsPropData implements IJGemsObjectData {
    private final JGemsPathSource pathToModel;
    private final PropRenderData propRenderData;

    public JGemsPropData(@Nullable JGemsPathSource pathToModel, @NotNull PropRenderData propRenderData) {
        this.propRenderData = propRenderData;
        this.pathToModel = pathToModel;
    }

    public JGemsPropData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultPropIndirect);
    }

    public JGemsPropData(@NotNull PropRenderData propRenderData) {
        this(null, propRenderData);
    }

    public JGemsPathSource getPathToModel() {
        return this.pathToModel;
    }

    public PropRenderData getPropRenderData() {
        return this.propRenderData;
    }
}
