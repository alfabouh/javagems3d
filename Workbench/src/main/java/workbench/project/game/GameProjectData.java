package workbench.project.game;

import org.jetbrains.annotations.NotNull;

public class GameProjectData {
    protected String gameInfo;
    protected String gameTitle;
    protected String version;

    public GameProjectData(@NotNull String gameInfo, @NotNull String gameTitle, @NotNull String version) {
        this.gameInfo = gameInfo;
        this.gameTitle = gameTitle;
        this.version = version;
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

}
