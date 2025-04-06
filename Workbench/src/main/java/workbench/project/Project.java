package workbench.project;

import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = -2138L;

    private transient JGemsPath currentProjectPath;
    private final String projectName;
    private final String version;
    private String mapDataFile;
    private final List<String> scriptFiles;

    public Project(@NotNull String version, @NotNull String projectName) {
        this.version = version;
        this.projectName = projectName;
        this.currentProjectPath = null;
        this.mapDataFile = "";
        this.scriptFiles = new ArrayList<>();
    }

    public void setMapDataFile(String mapDataFile) {
        this.mapDataFile = mapDataFile;
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public List<String> getScriptFiles() {
        return this.scriptFiles;
    }

    public String getMapDataFile() {
        return this.mapDataFile;
    }

    public JGemsPath getCurrentProjectPath() {
        return this.currentProjectPath;
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