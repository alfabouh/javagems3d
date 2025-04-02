package api.application.workbench.resources.data.jgems;

import api.application.workbench.manager.IAPIWBenchDataManager;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JGemsEntityData {
    private final JGemsPath pathToModel;
    private final EntityRenderData entityRenderData;
    private final WorldItemFabric worldItemFabric;

    public JGemsEntityData(@Nullable JGemsPath pathToModel, @NotNull EntityRenderData entityRenderData, @NotNull WorldItemFabric worldItemFabric) {
        this.entityRenderData = entityRenderData;
        this.worldItemFabric = worldItemFabric;
        this.pathToModel = pathToModel;
    }

    public JGemsEntityData(@Nullable JGemsPath pathToModel, @NotNull WorldItemFabric worldItemFabric) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, worldItemFabric);
    }

    public JGemsEntityData(@Nullable JGemsPath pathToModel, boolean isDynamic) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, isDynamic ? IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_DYNAMIC : IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_STATIC);
    }

    public JGemsEntityData(@NotNull WorldItemFabric worldItemFabric) {
        this(null, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, worldItemFabric);
    }

    public JGemsEntityData(boolean isDynamic) {
        this(null, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, isDynamic ? IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_DYNAMIC : IAPIWBenchDataManager.DEFAULT_FABRIC_FOR_STATIC);
    }

    public JGemsPath getPathToModel() {
        return this.pathToModel;
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
