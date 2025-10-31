package api.application.workbench.manager;

import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, TemplatesTable<ApiResourceEntity>> resourceEntityMap;
    private final Map<String, TemplatesTable<ApiResourceProp>> resourcePropMap;
    private final Map<String, TemplatesTable<ApiResourceMarker>> resourceMarker;
    private final Map<String, Pair<String, JGemsPath>> skyBoxesMap;

    public APIWBenchDataManager() {
        this.resourceEntityMap = new HashMap<>();
        this.resourcePropMap = new HashMap<>();
        this.resourceMarker = new HashMap<>();
        this.skyBoxesMap = new HashMap<>();
    }

    @Override
    public void addResourceEntity(@Nullable String group, @NotNull ApiResourceEntity resourceEntity) {
        resourceEntity.setGroupId(group);
        JGemsUtils.putObjectInMapOrUpdate(this.getResourceEntityMap(), group, new TemplatesTable<>(resourceEntity), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, resourceEntity);
    }

    @Override
    public void addResourceProp(@Nullable String group, @NotNull ApiResourceProp resourceProp) {
        resourceProp.setGroupId(group);
        JGemsUtils.putObjectInMapOrUpdate(this.getResourcePropMap(), group, new TemplatesTable<>(resourceProp), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, resourceProp);
    }

    @Override
    public void addResourceMarker(@Nullable String group, @NotNull ApiResourceMarker resourceMarker) {
        resourceMarker.setGroupId(group);
        JGemsUtils.putObjectInMapOrUpdate(this.getResourceMarkerMap(), group, new TemplatesTable<>(resourceMarker), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, resourceMarker);
    }

    @Override
    public void addResourceSkyCubeMap(@NotNull String name, @NotNull String extension, @NotNull JGemsPath pathToCubeMapDirectory) {
        this.getSkyBoxesMap().put(name, new Pair<>(extension, pathToCubeMapDirectory));
    }

    public Map<String, Pair<String, JGemsPath>> getSkyBoxesMap() {
        return this.skyBoxesMap;
    }

    public Map<String, TemplatesTable<ApiResourceEntity>> getResourceEntityMap() {
        return this.resourceEntityMap;
    }

    public Map<String, TemplatesTable<ApiResourceProp>> getResourcePropMap() {
        return this.resourcePropMap;
    }

    public Map<String, TemplatesTable<ApiResourceMarker>> getResourceMarkerMap() {
        return this.resourceMarker;
    }

    public static class TemplatesTable<T extends APIResource<?, ?>> {
        private final Map<String, T> templateMap;

        @SuppressWarnings("all")
        public TemplatesTable(APIResource<?, ?> apiResource) {
            this();
            this.getTemplateMap().put(apiResource.getNameId(), (T) apiResource);
        }

        public TemplatesTable() {
            this.templateMap = new HashMap<>();
        }

        @SuppressWarnings("all")
        public void add(APIResource<?, ?> apiResource) {
            this.getTemplateMap().put(apiResource.getNameId(), (T) apiResource);
        }

        public T find(String id) {
            return this.getTemplateMap().get(id);
        }

        public Map<String, T> getTemplateMap() {
            return this.templateMap;
        }
    }
}
