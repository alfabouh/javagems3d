package launcher.run;

import javagems3d.JGems3D;
import javagems3d.system.service.args.InputArgs;

public final class EngineRun implements IRun {
    @Override
    public void run(InputArgs inputArgs) {
        if (inputArgs.getBoolValue("debug")) {
            JGems3D.DEBUG_MODE = true;
        }
        JGems3D.launch();
    }
}
