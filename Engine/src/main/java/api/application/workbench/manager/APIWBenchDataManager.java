package api.application.workbench.manager;

import api.application.workbench.resources.ResourceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class APIWBenchDataManager implements IAPIWBenchDataManager {
    private final Map<String, ResourceEntity> resourceEntityMap;

    public APIWBenchDataManager() {
        this.resourceEntityMap = new HashMap<>();
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

    public Map<String, ResourceEntity> getResourceEntityMap() {
        return this.resourceEntityMap;
    }
}
