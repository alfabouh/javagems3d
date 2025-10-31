package javagems3d.mapping.data;

import javagems3d.JGems3D;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.service.exceptions.JGemsIOException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class MapProjectData {
    protected String information;
    protected String projectName;
    protected String version;
    protected String mapDataFile;
    protected List<String> scriptFiles;

    public MapProjectData(@NotNull String information, @NotNull String projectName, @NotNull String version, @NotNull String mapDataFile) {
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

    //TODO
    public void checkVersion() {
        //final String version = this.getVersion();
        //if (!JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SUPPORTED_VERSIONS.contains(version)) {
        //    throw new JGemsIOException("Map's version " + version + " is not supported in current JGems3D version: " + JGemsCore.ENG_VER);
        //}
    }
}
