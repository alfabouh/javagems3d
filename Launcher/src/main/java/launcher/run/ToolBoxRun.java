package launcher.run;

import javagems3d.JGems3D;
import launcher.util.ArgsDecomposer;
import toolbox.ToolBox;

public final class ToolBoxRun implements IRun {
    @Override
    public void run(String[] args) {
        ArgsDecomposer argsDecomposer = new ArgsDecomposer(args);
        ToolBox.launch();
    }
}
