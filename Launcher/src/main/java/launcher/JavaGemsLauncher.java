package launcher;

import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;
import launcher.util.InputArgs;

public abstract class JavaGemsLauncher {
    public static void launchEngine(String[] args) {
        new EngineRun().run(args);
    }

    public static void launchWorkbench(String[] args) {
        new WorkbenchRun().run(args);
    }

    public static void launch(String[] args) {
        InputArgs inputArgs = new InputArgs(args);
        if (inputArgs.hasValue("workbench")) {
            JavaGemsLauncher.launchWorkbench(args);
            return;
        }
        JavaGemsLauncher.launchEngine(args);
    }
}
