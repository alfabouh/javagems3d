package launcher;

import launcher.run.EngineRun;

public abstract class JGemsLauncher {
    public static void launchEngine(String[] args) {
        new EngineRun().run(args);
    }
}
