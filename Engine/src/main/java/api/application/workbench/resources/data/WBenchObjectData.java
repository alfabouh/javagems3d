package api.application.workbench.resources.data;

import javagems3d.graphics.objects.rendering.configuration.RenderProperties;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

public final class WBenchObjectData {
    private final JGemsPath pathToModel;
    private final RenderProperties renderProperties;

    public WBenchObjectData(@NotNull JGemsPath pathToModel, @NotNull RenderProperties renderProperties) {
        this.pathToModel = pathToModel;
        this.renderProperties = renderProperties;
    }

    public WBenchObjectData(@NotNull JGemsPath pathToModel) {
        this(pathToModel, RenderProperties.get());
    }

    public JGemsPath getPathToModel() {
        return this.pathToModel;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }
}
