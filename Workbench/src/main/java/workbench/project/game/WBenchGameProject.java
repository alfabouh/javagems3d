package workbench.project.game;

import javagems3d.JGems3D;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WBenchGameProject extends GameProjectData {
    private transient JGemsPath currentProjectPath;
    public List<String> maps;

    public WBenchGameProject(@NotNull String version, @NotNull String projectTitle) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_INFO, projectTitle, version, "");
        this.currentProjectPath = null;
        this.maps = new ArrayList<>();
    }

    public void setGameDataFile(String gameDataFile) {
        this.gameDataFile = gameDataFile;
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public void refreshMapsList(@NotNull List<String> maps) {
        this.maps.clear();
        this.maps.addAll(maps);
    }

    public void removeMap(String map) {
        this.getMaps().remove(map);
    }

    public void addNewMap(String map) {
        this.getMaps().add(map);
    }

    public List<String> getMaps() {
        return this.maps;
    }

    public JGemsPath getCurrentProjectPath() {
        return this.currentProjectPath;
    }

    public String toString() {
        return this.getGameTitle() + " - " + this.getVersion();
    }

    //TODO
    public void checkVersion() {
        //final String version = this.getVersion();
        //if (!JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SUPPORTED_VERSIONS.contains(version)) {
        //    throw new JGemsIOException("Map's version " + version + " is not supported in current JGems3D version: " + JGemsCore.ENG_VER);
        //}
    }
}