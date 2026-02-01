package api.application.workbench.manager;

import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.AbstractObjectsFolder;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsEntityData, ApiResourceEntity> entityApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsPropData, ApiResourceProp> propApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchMarkerData, JGemsMarkerData, ApiResourceMarker> markerApiResourceObjectsFolder;
    private final Map<String, Pair<String, JGemsPath>> skyBoxesMap;

    public APIWBenchDataManager() {
        this.entityApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.propApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.markerApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(AbstractObjectsFolder.DEF_PATH);;
        this.skyBoxesMap = new HashMap<>();
    }

    @Override
    public void addResourceEntity(@Nullable String path, @NotNull ApiResourceEntity resourceEntity) {
        this.getEntities().putObjectInside(path == null ? "/" : path, resourceEntity, ApiResourceObjectsFolder::new);
    }

    @Override
    public void addResourceProp(@Nullable String path, @NotNull ApiResourceProp resourceProp) {
        this.getProps().putObjectInside(path == null ? "/": path, resourceProp, ApiResourceObjectsFolder::new);
    }

    @Override
    public void addResourceMarker(@Nullable String path, @NotNull ApiResourceMarker resourceMarker) {
        this.getMarkers().putObjectInside(path == null ? "/" : path, resourceMarker, ApiResourceObjectsFolder::new);
    }

    @Override
    public void addResourceSkyCubeMap(@NotNull String name, @NotNull String extension, @NotNull JGemsPath pathToCubeMapDirectory) {
        this.getSkyBoxesMap().put(name, new Pair<>(extension, pathToCubeMapDirectory));
    }

    public Map<String, Pair<String, JGemsPath>> getSkyBoxesMap() {
        return this.skyBoxesMap;
    }

    public ApiResourceObjectsFolder<WBenchObjectData, JGemsEntityData, ApiResourceEntity> getEntities() {
        return this.entityApiResourceObjectsFolder;
    }

    public ApiResourceObjectsFolder<WBenchObjectData, JGemsPropData, ApiResourceProp> getProps() {
        return this.propApiResourceObjectsFolder;
    }

    public ApiResourceObjectsFolder<WBenchMarkerData, JGemsMarkerData, ApiResourceMarker> getMarkers() {
        return this.markerApiResourceObjectsFolder;
    }
}
