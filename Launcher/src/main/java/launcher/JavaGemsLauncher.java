package launcher;

import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;
import javagems3d.system.service.args.InputArgs;

public abstract class JavaGemsLauncher {
    public static void launchEngine(InputArgs inputArgs) {
        new EngineRun().run(inputArgs);
    }

    public static void launchWorkbench(InputArgs inputArgs) {
        new WorkbenchRun().run(inputArgs);
    }

    public static void launch(String[] args) {
        InputArgs inputArgs = new InputArgs(args);
        if (inputArgs.hasValue("workbench")) {
            JavaGemsLauncher.launchWorkbench(inputArgs);
            return;
        }
        JavaGemsLauncher.launchEngine(inputArgs);
    }
}
