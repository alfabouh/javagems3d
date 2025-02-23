package launcher.run;

import javagems3d.JGems3D;
import launcher.util.InputArgs;

public final class EngineRun implements IRun {
    @Override
    public void run(String[] args) {
        InputArgs inputArgs = new InputArgs(args);
        if (inputArgs.getBoolValue("debug")) {
            JGems3D.DEBUG_MODE = true;
        }
        JGems3D.launch();
    }
}
