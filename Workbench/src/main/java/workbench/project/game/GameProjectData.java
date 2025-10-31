package workbench.project.game;

import org.jetbrains.annotations.NotNull;

public class GameProjectData {
    protected String gameInfo;
    protected String gameTitle;
    protected String version;
    protected String gameDataFile;

    public GameProjectData(@NotNull String gameInfo, @NotNull String gameTitle, @NotNull String version, @NotNull String gameDataFile) {
        this.gameInfo = gameInfo;
        this.gameTitle = gameTitle;
        this.version = version;
        this.gameDataFile = gameDataFile;
    }

    public String getGameInfo() {
        return this.gameInfo;
    }

    public String getGameTitle() {
        return this.gameTitle;
    }

    public String getVersion() {
        return this.version;
    }

    public String getGameDataFile() {
        return this.gameDataFile;
    }
}
