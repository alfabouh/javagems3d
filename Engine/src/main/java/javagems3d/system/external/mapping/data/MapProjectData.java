package javagems3d.system.external.mapping.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class MapProjectData {
    protected String mapDescription;
    protected String mapName;
    protected String version;
    protected String mapDataFile;
    protected List<String> scriptFiles;

    public MapProjectData(@NotNull String mapDescription, @NotNull String mapName, @NotNull String version, @NotNull String mapDataFile) {
        this.mapDescription = mapDescription;
        this.mapName = mapName;
        this.version = version;
        this.scriptFiles = new ArrayList<>();
        this.mapDataFile = mapDataFile;
    }

    public void setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getMapDescription() {
        return this.mapDescription;
    }

    public String getMapName() {
        return this.mapName;
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
