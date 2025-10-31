package launcher;

import javagems3d.JGems3D;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import launcher.run.EngineRun;
import launcher.run.WorkbenchRun;
import org.jetbrains.annotations.NotNull;

public abstract class JavaGemsLauncher {
    public static void launchEngine(@NotNull JGemsLaunchArgsRegistry jGemsLaunchArgsRegistry) {
        new EngineRun().run(jGemsLaunchArgsRegistry);
    }

    public static void launchWorkbench(@NotNull JGemsLaunchArgsRegistry jGemsLaunchArgsRegistry) {
        new WorkbenchRun().run(jGemsLaunchArgsRegistry);
    }

    public static void launchJGemsIsolatedProcess(String[] args) {
        JGems3D.IsolatedProcessLauncher.EXEC(args);
    }

    public static void launch(String[] args) {
        JGemsLaunchArgsRegistry.INSTANCE.read(args);
        if (JGemsLaunchArgsRegistry.INSTANCE.getValue(JGemsLaunchArgsRegistry.JGemsLaunchArgs.WORKBENCH) == Boolean.TRUE) {
            JavaGemsLauncher.launchWorkbench(JGemsLaunchArgsRegistry.INSTANCE);
            return;
        }
        JavaGemsLauncher.launchEngine(JGemsLaunchArgsRegistry.INSTANCE);
    }
}
