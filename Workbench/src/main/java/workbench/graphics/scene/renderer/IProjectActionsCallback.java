package workbench.graphics.scene.renderer;

import org.jetbrains.annotations.NotNull;
import workbench.project.map.WBenchMapProject;
import workbench.resources.WBenchResourceManager;

public interface IProjectActionsCallback {
    void onOpeningProject(WBenchResourceManager resourceManager, @NotNull WBenchMapProject wBenchMapProject);
    void onClosingProject(WBenchResourceManager resourceManager, @NotNull WBenchMapProject wBenchMapProject);
}
