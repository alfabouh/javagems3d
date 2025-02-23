package launcher.run;

import launcher.util.InputArgs;
import workbench.WBench;

public final class WorkbenchRun implements IRun {
    @Override
    public void run(String[] args) {
        InputArgs inputArgs = new InputArgs(args);
        WBench.launch();
    }
}
