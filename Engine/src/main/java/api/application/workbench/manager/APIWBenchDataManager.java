package api.application.workbench.manager;

import api.application.workbench.resources.Resource;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import javagems3d.help.JGemsUtils;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.collections.Triple;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, TemplatesTable<ResourceEntity>> resourceEntityMap;
    private final Map<String, TemplatesTable<ResourceProp>> resourcePropMap;
    private final Map<String, TemplatesTable<ResourceMarker>> resourceMarker;
    private final Map<String, Pair<String, JGemsPath>> skyBoxesMap;

    public APIWBenchDataManager() {
        this.resourceEntityMap = new HashMap<>();
        this.resourcePropMap = new HashMap<>();
        this.resourceMarker = new HashMap<>();
        this.skyBoxesMap = new HashMap<>();
    }

    @Override
    public void addResourceEntity(@Nullable String group, @NotNull ResourceEntity resourceEntity) {
        resourceEntity.setGroupId(group);
        JGemsUtils.putObjectInMapOrUpdate(this.getResourceEntityMap(), group, new TemplatesTable<>(resourceEntity), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, resourceEntity);
    }

    @Override
    public void addResourceProp(@Nullable String group, @NotNull ResourceProp resourceProp) {
        resourceProp.setGroupId(group);
        JGemsUtils.putObjectInMapOrUpdate(this.getResourcePropMap(), group, new TemplatesTable<>(resourceProp), (ex, nw) -> {
            ex.add(nw);
            return ex;
        }, resourceProp);
    }

    @Override
    public void addResourceMarker(@Nullable String group, @NotNull ResourceMarker resourceMarker) {
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

    public Map<String, TemplatesTable<ResourceEntity>> getResourceEntityMap() {
        return this.resourceEntityMap;
    }

    public Map<String, TemplatesTable<ResourceProp>> getResourcePropMap() {
        return this.resourcePropMap;
    }

    public Map<String, TemplatesTable<ResourceMarker>> getResourceMarkerMap() {
        return this.resourceMarker;
    }

    public static class TemplatesTable<T extends Resource<?, ?>> {
        private final Map<String, T> templateMap;

        @SuppressWarnings("all")
        public TemplatesTable(Resource<?, ?> resource) {
            this();
            this.getTemplateMap().put(resource.getNameId(), (T) resource);
        }

        public TemplatesTable() {
            this.templateMap = new HashMap<>();
        }

        @SuppressWarnings("all")
        public void add(Resource<?, ?> resource) {
            this.getTemplateMap().put(resource.getNameId(), (T) resource);
        }

        public T find(String id) {
            return this.getTemplateMap().get(id);
        }

        public Map<String, T> getTemplateMap() {
            return this.templateMap;
        }
    }
}
