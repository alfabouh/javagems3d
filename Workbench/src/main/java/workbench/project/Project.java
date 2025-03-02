package workbench.project;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Project implements Serializable {
    private static final long serialVersionUID = -2138L;

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
        return this.getProjectName() + " - " + this.getVersion();
    }
}
