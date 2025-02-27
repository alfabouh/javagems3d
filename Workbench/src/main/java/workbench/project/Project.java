package workbench.project;

import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

public class Project {
    private final String projectName;
    private final String version;

    public Project(@NotNull String version, @NotNull String projectName) {
        this.version = version;
        this.projectName = projectName;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getVersion() {
        return this.version;
    }

    public String toString() {
        return this.getProjectName() + "&" + this.getVersion();
    }
}
