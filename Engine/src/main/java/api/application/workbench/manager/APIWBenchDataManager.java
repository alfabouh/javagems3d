package api.application.workbench.manager;

import api.application.workbench.resources.ResourceEntity;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.collections.Triple;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, ResourceEntity> resourceEntityMap;
    private final Set<Triple<String, String, JGemsPath>> skyBoxesPath;

    public APIWBenchDataManager() {
        this.resourceEntityMap = new HashMap<>();
        this.skyBoxesPath = new HashSet<>();
    }

    @Override
    public void addResourceEntity(@Nullable String group, @NotNull ResourceEntity resourceEntity) {
        this.getResourceEntityMap().put(resourceEntity.getId(), resourceEntity);
        resourceEntity.setGroupId(group);
    }

    @Override
    public void addResourceProp(@Nullable String group, @NotNull ResourceEntity resourceEntity) {

    }

    @Override
    public void addResourceSound(@Nullable String group, @NotNull ResourceEntity resourceEntity) {

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
}
