package launcher.run;

import javagems3d.system.core.JGemsLaunchArgsRegistry;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;

public final class WorkbenchRun implements IRun {
    @Override
    public void run(@NotNull JGemsLaunchArgsRegistry argsRegistry) {
        WBench.launch(argsRegistry);
    }
}
