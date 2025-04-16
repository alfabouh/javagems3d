package api.application.workbench.manager;

import api.application.workbench.resources.Resource;
import api.application.workbench.resources.ResourceEntity;
import api.application.workbench.resources.ResourceMarker;
import api.application.workbench.resources.ResourceProp;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IAPIWBenchDataManager {
    void addResourceEntity(@Nullable String group, @NotNull ResourceEntity resourceEntity);
    void addResourceProp(@Nullable String group, @NotNull ResourceProp resourceProp);
    void addResourceMarker(@Nullable String group, @NotNull ResourceMarker resourceMarker);
    void addResourceSkyCubeMap(@NotNull String name, @NotNull String extension, @NotNull JGemsPath pathToCubeMapDirectory);

    default void addResourceEntity(@Nullable String group, @NotNull String id, @NotNull JGemsPath modelPath) {
        this.addResourceEntity(group, new ResourceEntity(id, () -> new WBenchObjectData(modelPath), () -> new JGemsEntityData(modelPath)));
    }

    default void addResourceProp(@Nullable String group, @NotNull String id, @NotNull JGemsPath modelPath) {
        this.addResourceProp(group, new ResourceProp(id, () -> new WBenchObjectData(modelPath), () -> new JGemsPropData(modelPath)));
    }

    default void addResourceMarker(@Nullable String group, @NotNull String id, @NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        this.addResourceMarker(group, new ResourceMarker(id, () -> new WBenchMarkerData(defaultMarker, color, transparent)));
    }


    default void addResourceEntity(@Nullable String group, @NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(group, new ResourceEntity(id, fabricWBench, fabricGame));
    }

    default void addResourceEntity(@NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame);
    }

    default void addResourceProp(@Nullable String group, @NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(group, new ResourceProp(id, fabricWBench, fabricGame));
    }

    default void addResourceProp(@NotNull String id, @NotNull Resource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull Resource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(null, id, fabricWBench, fabricGame);
    }

    default void addResourceMarker(@Nullable String group, @NotNull String id, @NotNull Resource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(group, new ResourceMarker(id, fabricWBench));
    }

    default void addResourceMarker(@NotNull String id, @NotNull Resource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(null, id, fabricWBench);
    }
}