package workbench.graphics.scene.renderer;

import org.jetbrains.annotations.NotNull;
import workbench.project.Project;
import workbench.resources.WBenchResourceManager;

public interface IProjectActrionsCallback {
    void onOpenedProject(WBenchResourceManager resourceManager, @NotNull Project project);
    void onClosingProject(WBenchResourceManager resourceManager, @NotNull Project project);
}
