package workbench.graphics.scene.renderer;

import org.jetbrains.annotations.NotNull;
import workbench.project.WBenchProject;
import workbench.resources.WBenchResourceManager;

public interface IProjectActionsCallback {
    void onOpeningProject(WBenchResourceManager resourceManager, @NotNull WBenchProject WBenchProject);
    void onClosingProject(WBenchResourceManager resourceManager, @NotNull WBenchProject WBenchProject);
}
