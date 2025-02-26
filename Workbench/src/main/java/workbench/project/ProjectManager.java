package workbench.project;

import org.jetbrains.annotations.NotNull;
import workbench.graphics.scene.world.WBenchWorld;

public final class ProjectManager {
    private Project currentProject;
    private WBenchWorld world;

    public ProjectManager() {
        this.currentProject = null;
        this.world = null;
    }

    public WBenchWorld getWorld() {
        return this.world;
    }

    public void setWorld(@NotNull WBenchWorld world) {
        this.world = world;
    }

    public boolean createProject(String path, String name) {
        return false;
    }

    public boolean openProject(String path) {
        return false;
    }
}