package launcher;

import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;

public abstract class JGemsLauncher {
    public static void launchEngine(String[] args) {
        new EngineRun().run(args);
    }

    public static void launchWorkbench(String[] args) {
        new WorkbenchRun().run(args);
    }
}
