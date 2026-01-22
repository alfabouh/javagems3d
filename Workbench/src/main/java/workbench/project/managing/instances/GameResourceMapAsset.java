package workbench.project.managing.instances;

import workbench.project.map.WBenchMapProject;

public class GameResourceMapAsset implements IAsset {
    private final WBenchMapProject wBenchMapProject;

    public GameResourceMapAsset(WBenchMapProject wBenchMapProject) {
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
