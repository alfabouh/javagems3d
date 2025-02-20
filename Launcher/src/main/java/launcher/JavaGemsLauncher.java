package launcher;

import javagems3d.JGems3D;
import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;
import launcher.util.ArgsDecomposer;

public abstract class JavaGemsLauncher {
    public static void launchEngine(String[] args) {
        new EngineRun().run(args);
    }

    public static void launchWorkbench(String[] args) {
        new WorkbenchRun().run(args);
    }

    public static void launch(String[] args) {
        ArgsDecomposer argsDecomposer = new ArgsDecomposer(args);
        if (argsDecomposer.getBoolValue("workbench")) {
            JavaGemsLauncher.launchWorkbench(args);
            return;
        }
        JavaGemsLauncher.launchEngine(args);
    }
}
