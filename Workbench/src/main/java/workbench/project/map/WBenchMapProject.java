package workbench.project.map;

import javagems3d.JGems3D;
import javagems3d.system.external.mapping.data.MapProjectData;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;

public class WBenchMapProject extends MapProjectData {
    public WBenchMapProject(@NotNull String version, @NotNull String projectName, @NotNull JGemsPath absolutePath) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_INFO, projectName, version, absolutePath);
    }
}