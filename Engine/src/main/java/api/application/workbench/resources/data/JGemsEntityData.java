package api.application.workbench.resources.data;

import api.application.workbench.manager.IAPIWBenchDataManager;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public final class JGemsEntityData {
    private final EntityRenderData entityRenderData;
    private final WorldItemFabric worldItemFabric;

    public JGemsEntityData(@NotNull EntityRenderData entityRenderData, @NotNull WorldItemFabric worldItemFabric) {
        this.entityRenderData = entityRenderData;
        this.worldItemFabric = worldItemFabric;
    }

    public JGemsEntityData(@NotNull WorldItemFabric worldItemFabric) {
        this(JGemsResourceManager.globalRenderDataAssets.defaultIndirect, worldItemFabric);
    }

    public JGemsEntityData(boolean isDynamic) {
        this(JGemsResourceManager.globalRenderDataAssets.defaultIndirect, isDynamic ? IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_DYNAMIC : IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_STATIC);
    }

    public EntityRenderData getEntityRenderData() {
        return this.entityRenderData;
    }

    public WorldItemFabric getWorldItemFabric() {
        return this.worldItemFabric;
    }

    @FunctionalInterface
    public interface WorldItemFabric {
        WorldItem create(String id, MeshStructure3D<?> objectsMesh, PhysicsWorld world, SceneWorld sceneWorld);
    }
}
