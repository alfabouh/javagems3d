package launcher.run;

import javagems3d.system.core.JGemsLaunchArgsRegistry;
import org.jetbrains.annotations.NotNull;

public interface IRun {
    void run(@NotNull JGemsLaunchArgsRegistry argsRegistry);
}
