package workbench.project;

import javagems3d.mapping.JGemsMapping;
import javagems3d.mapping.data.ProjectData;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class WBenchProject extends ProjectData implements Serializable {
    private static final long serialVersionUID = -2138L;
    private transient JGemsPath currentProjectPath;

    public WBenchProject(@NotNull String version, @NotNull String projectName) {
        super(JGemsMapping.DATA_INFO, projectName, version, "");
        this.currentProjectPath = null;
    }

    public void setMapDataFile(String mapDataFile) {
        this.mapDataFile = mapDataFile;
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public JGemsPath getCurrentProjectPath() {
        return this.currentProjectPath;
    }

    public String toString() {
        return this.getProjectName() + " - " + this.getVersion();
    }
}