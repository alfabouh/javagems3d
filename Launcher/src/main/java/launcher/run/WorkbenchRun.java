package launcher.run;

import launcher.util.ArgsDecomposer;
import workbench.WBench;

public final class WorkbenchRun implements IRun {
    @Override
    public void run(String[] args) {
        ArgsDecomposer argsDecomposer = new ArgsDecomposer(args);
        WBench.launch();
    }
}
