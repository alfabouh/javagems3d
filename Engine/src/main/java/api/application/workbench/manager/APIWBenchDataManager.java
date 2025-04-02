package api.application.workbench.manager;

import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.collections.Triple;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, ResourceEntity> resourceEntityMap;
    private final Map<String, ResourceProp> resourcePropMap;
    private final Map<String, ResourceMarker> resourceMarker;

    private final Set<Triple<String, String, JGemsPath>> skyBoxesPath;

    public APIWBenchDataManager() {
        this.resourceEntityMap = new HashMap<>();
        this.resourcePropMap = new HashMap<>();
        this.resourceMarker = new HashMap<>();
        this.skyBoxesPath = new HashSet<>();
    }

    @Override
    public void addResourceEntity(@Nullable String group, @NotNull ResourceEntity resourceEntity) {
        this.getResourceEntityMap().put(resourceEntity.getId(), resourceEntity);
        resourceEntity.setGroupId(group);
    }

    @Override
    public void addResourceProp(@Nullable String group, @NotNull ResourceProp resourceProp) {
        this.getResourcePropMap().put(resourceProp.getId(), resourceProp);
        resourceProp.setGroupId(group);
    }

    @Override
    public void addResourceMarker(@Nullable String group, @NotNull ResourceMarker resourceMarker) {
        this.getResourceMarker().put(resourceMarker.getId(), resourceMarker);
        resourceMarker.setGroupId(group);
    }

    @Override
    public void addResourceScript(@Nullable String group, @NotNull ResourceEntity resourceEntity) {

    }

    @Override
    public void addResourceSkyCubeMap(@NotNull String name, @NotNull String extension, @NotNull JGemsPath pathToCubeMapDirectory) {
        this.getSkyBoxesPath().add(new Triple<>(name, extension, pathToCubeMapDirectory));
    }

    public Set<Triple<String, String, JGemsPath>> getSkyBoxesPath() {
        return this.skyBoxesPath;
    }

    public Map<String, ResourceEntity> getResourceEntityMap() {
        return this.resourceEntityMap;
    }

    public Map<String, ResourceProp> getResourcePropMap() {
        return this.resourcePropMap;
    }

    public Map<String, ResourceMarker> getResourceMarker() {
        return this.resourceMarker;
    }
}
