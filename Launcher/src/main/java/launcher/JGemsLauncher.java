package launcher;

import javagems3d.JGems3D;
import launcher.run.EngineRun;
import launcher.run.ToolBoxRun;

public abstract class JGemsLauncher {
    public static void launchEngine(String[] args) {
        new EngineRun().run(args);
    }

    public static void launchToolbox(String[] args) {
        new ToolBoxRun().run(args);
    }
}
