package api.application.workbench.manager;

import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsMarkerData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsEntityData, ApiResourceEntity> entityApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsPropData, ApiResourceProp> propApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchMarkerData, JGemsMarkerData, ApiResourceMarker> markerApiResourceObjectsFolder;
    private final Map<String, ICubeMapProgram.CMTextures> skyBoxesMap;

    public APIWBenchDataManager() {
        this.entityApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.propApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.markerApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
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
    public void addResourceSkyCubeMap(@NotNull String name, @NotNull ICubeMapProgram.CMTextures textures) {
        this.getSkyBoxesMap().put(name, textures);
    }

    public Map<String, ICubeMapProgram.CMTextures> getSkyBoxesMap() {
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
