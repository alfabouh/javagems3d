package workbench.project.game.settings;

public final class GameProjectSettings {
    public MapProjectAutoSaveMode mapProjectAutoSaveMode;
    public int saveEachStep;
    public float savePerSecond;
    public String compileCorePath;
    public String compileLauncherPath;
    public String compileWorkbenchPath;

    public GameProjectSettings(String absPath) {
        this.mapProjectAutoSaveMode = MapProjectAutoSaveMode.TIMER;
        this.saveEachStep = 5;
        this.savePerSecond = 30.0f;
        this.compileCorePath = absPath + "/jgems3d-core.jar";
        this.compileLauncherPath = absPath + "/jgems3d-launcher.jar";
        this.compileWorkbenchPath = absPath + "/jgems3d-workbench.jar";
    }

    public enum MapProjectAutoSaveMode {
        TIMER,
        STEPS
    }
}
