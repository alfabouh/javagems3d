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
import api.application.workbench.resources.data.wbench.ext.WBenchObjectInstanceExtension;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension>> objectInstancesExtensions;

    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsEntityData, ApiResourceEntity> entityApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchObjectData, JGemsPropData, ApiResourceProp> propApiResourceObjectsFolder;
    private final ApiResourceObjectsFolder<WBenchMarkerData, JGemsMarkerData, ApiResourceMarker> markerApiResourceObjectsFolder;
    private final Map<String, ICubeMapProgram.CMTextures> skyBoxesMap;

    public APIWBenchDataManager() {
        this.entityApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.propApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.markerApiResourceObjectsFolder = new ApiResourceObjectsFolder<>(VirtualObjectsFolder.DEF_PATH);
        this.skyBoxesMap = new HashMap<>();
        this.objectInstancesExtensions = new HashMap<>();
    }

    @Override
    public void addResourceEntity(@Nullable String path, @NotNull ApiResourceEntity resourceEntity, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.getEntities().putObjectInside(path == null ? "/" : path, resourceEntity, ApiResourceObjectsFolder::new);
        if (extCreator != null) {
            this.addEditorObjectInstanceExtension(path, resourceEntity, extCreator);
        }
    }

    @Override
    public void addResourceProp(@Nullable String path, @NotNull ApiResourceProp resourceProp, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.getProps().putObjectInside(path == null ? "/": path, resourceProp, ApiResourceObjectsFolder::new);
        if (extCreator != null) {
            this.addEditorObjectInstanceExtension(path, resourceProp, extCreator);
        }
    }

    @Override
    public void addResourceMarker(@Nullable String path, @NotNull ApiResourceMarker resourceMarker, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.getMarkers().putObjectInside(path == null ? "/" : path, resourceMarker, ApiResourceObjectsFolder::new);
        if (extCreator != null) {
            this.addEditorObjectInstanceExtension(path, resourceMarker, extCreator);
        }
    }

    @Override
    public void addResourceSkyCubeMap(@NotNull String name, @NotNull ICubeMapProgram.CMTextures textures) {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        this.getSkyBoxesMap().put(name, textures);
    }

    @Override
    public void addEditorObjectInstanceExtension(@Nullable String path, @NotNull APIResource<?, ?> apiResource, Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.getObjectInstancesExtensions().put("/" + (path == null ? "/" : path) + "/" + apiResource.name(), extCreator);
    }

    public Map<String, Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension>> getObjectInstancesExtensions() {
        return this.objectInstancesExtensions;
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
