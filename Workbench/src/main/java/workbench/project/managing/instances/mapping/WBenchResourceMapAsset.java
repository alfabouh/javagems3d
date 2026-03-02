package workbench.project.managing.instances.mapping;

import javagems3d.system.external.gaming.def.IAsset;
import workbench.project.map.WBenchMapProject;

public class WBenchResourceMapAsset implements IAsset {
    private final WBenchMapProject wBenchMapProject;

    public WBenchResourceMapAsset(WBenchMapProject wBenchMapProject) {
        this.wBenchMapProject = wBenchMapProject;
    }

    public WBenchMapProject getMapProject() {
        return this.wBenchMapProject;
    }

    @Override
    public String getName() {
        return this.getMapProject().getMapName();
    }
}
