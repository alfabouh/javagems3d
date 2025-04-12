package launcher.run;

import javagems3d.system.service.args.InputArgs;
import workbench.WBench;

public final class WorkbenchRun implements IRun {
    @Override
    public void run(InputArgs inputArgs) {
        WBench.launch();
    }
}
