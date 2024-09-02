package launcher.run;

import javagems3d.JGems3D;
import launcher.util.ArgsDecomposer;

public final class EngineRun implements IRun {
    @Override
    public void run(String[] args) {
        ArgsDecomposer argsDecomposer = new ArgsDecomposer(args);
        if (argsDecomposer.getBoolValue("debug")) {
            JGems3D.DEBUG_MODE = true;
        }
        JGems3D.launch();
    }
}
