package launcher.run;

import javagems3d.JGems3D;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import org.jetbrains.annotations.NotNull;

public final class EngineRun implements IRun {
    @Override
    public void run(@NotNull JGemsLaunchArgsRegistry argsRegistry) {
        JGems3D.launch(argsRegistry);
    }
}
