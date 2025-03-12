package api.application.workbench.manager;

import api.application.workbench.resources.Resource;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.data.JGemsEntityData;
import api.application.workbench.resources.data.WBenchObjectData;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IAPIWBenchDataManager {
    JGemsEntityData.WorldItemFabric DEFAULT_FABRIC_FOR_STATIC = (id, mesh, physWorld, sceneWorld) -> new JGemsStaticBody(MeshCollider.getStatic(mesh), physWorld, id).setCanBeDestroyed(false);
    JGemsEntityData.WorldItemFabric DEFAULT_FABRIC_FOR_DYNAMIC = (id, mesh, physWorld, sceneWorld) -> new JGemsDynamicBody(MeshCollider.getDynamic(mesh), physWorld, id).setCanBeDestroyed(true);

    String DEFAULT_WORKBENCH_INDIRECT_SHADER = "/assets/wbench/shaders/world/world_gbuffer_indirect";
    String DEFAULT_WORKBENCH_DIRECT_SHADER = "/assets/wbench/shaders/world/world_gbuffer";

    void addResourceEntity(@Nullable String group, @NotNull ResourceEntity resourceEntity);
    void addResourceProp(@Nullable String group, @NotNull ResourceEntity resourceEntity);
    void addResourceSound(@Nullable String group, @NotNull ResourceEntity resourceEntity);
    void addResourceScript(@Nullable String group, @NotNull ResourceEntity resourceEntity);

    default void addResourceEntity(@Nullable String group, @NotNull String id, @NotNull Resource.WFabric<WBenchObjectData> fabricWBench, @NotNull Resource.WFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(group, new ResourceEntity(id, fabricWBench, fabricGame));
    }

    default void addResourceEntity(@NotNull String id, @NotNull Resource.WFabric<WBenchObjectData> fabricWBench, @NotNull Resource.WFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame);
    }
}