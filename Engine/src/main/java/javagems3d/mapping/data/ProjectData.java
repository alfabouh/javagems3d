package javagems3d.mapping.data;

import javagems3d.JGems3D;
import javagems3d.mapping.JGemsMapping;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ProjectData {
    protected String information;
    protected String projectName;
    protected String version;
    protected String mapDataFile;
    protected List<String> scriptFiles;

    public ProjectData(String information, String projectName, String version, String mapDataFile) {
        this.information = information;
        this.projectName = projectName;
        this.version = version;
        this.scriptFiles = new ArrayList<>();
        this.mapDataFile = mapDataFile;
    }

    public String getInformation() {
        return this.information;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMapDataFile() {
        return this.mapDataFile;
    }

    public List<String> getScriptFiles() {
        return this.scriptFiles;
    }

    public void checkVersion() {
        final String version = this.getVersion();
        if (!JGemsMapping.SUPPORTED_VERSIONS.contains(version)) {
            throw new JGemsIOException("Project's version " + version + " is not supported in current JGems3D version: " + JGemsCore.ENG_VER);
        }
    }
}
