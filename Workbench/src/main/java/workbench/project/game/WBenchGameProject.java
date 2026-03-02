package workbench.project.game;

import javagems3d.JGems3D;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

public class WBenchGameProject extends GameProjectData {
    private transient JGemsPath currentProjectPath;

    public WBenchGameProject(@NotNull String version, @NotNull String projectTitle) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_INFO, projectTitle, version);
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath1) {
        this.currentProjectPath = currentProjectPath1;
    }

    public JGemsPath getCurrentProjectAbsolutePath() {
        return this.currentProjectPath.getAbsolutePathDirectory();
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