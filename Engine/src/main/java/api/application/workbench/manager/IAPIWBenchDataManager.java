package api.application.workbench.manager;

import api.application.workbench.resources.APIResource;
import api.application.workbench.resources.ApiResourceEntity;
import api.application.workbench.resources.ApiResourceMarker;
import api.application.workbench.resources.ApiResourceProp;
import api.application.workbench.resources.data.DefaultMarker;
import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.jgems.JGemsPropData;
import api.application.workbench.resources.data.wbench.WBenchMarkerData;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import javagems3d.JGems3D;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.items.TagRadioBoolean;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IAPIWBenchDataManager {
    void addResourceEntity(@Nullable String group, @NotNull ApiResourceEntity resourceEntity);

    void addResourceProp(@Nullable String group, @NotNull ApiResourceProp resourceProp);

    void addResourceMarker(@Nullable String group, @NotNull ApiResourceMarker resourceMarker);

    void addResourceSkyCubeMap(@NotNull String name, @NotNull String extension, @NotNull JGemsPath pathToCubeMapDirectory);

    default void addResourceEntity(@Nullable String group, @NotNull String id, @NotNull JGemsPath modelPath) {
        this.addResourceEntity(group, new ApiResourceEntity(id, () -> new WBenchObjectData(modelPath), () -> new JGemsEntityData(modelPath)));
    }

    default void addResourceProp(@Nullable String group, @NotNull String id, @NotNull JGemsPath modelPath) {
        this.addResourceProp(group, new ApiResourceProp(id, () -> new WBenchObjectData(modelPath), () -> new JGemsPropData(modelPath)));
    }

    default void addResourceMarker(@Nullable String group, @NotNull String id, @NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        this.addResourceMarker(group, new ApiResourceMarker(id, () -> new WBenchMarkerData(defaultMarker, color, transparent)));
    }


    default void addResourceEntity(@Nullable String group, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(group, new ApiResourceEntity(id, fabricWBench, fabricGame));
    }

    default void addResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame);
    }

    default void addResourceProp(@Nullable String group, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(group, new ApiResourceProp(id, fabricWBench, fabricGame));
    }

    default void addResourceProp(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(null, id, fabricWBench, fabricGame);
    }

    default void addResourceMarker(@Nullable String group, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(group, new ApiResourceMarker(id, fabricWBench));
    }

    default void addResourceMarker(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(null, id, fabricWBench);
    }

    default void SET_DEFAULTS() {
        final Tag<TagRadioBoolean> TAG_PHYSICS = new Tag<>(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Is Static", true), new TagRadioBoolean.Info("Is Dynamic", false)));
        final JGemsPath cube = new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf");
        this.addResourceProp("generic_prop", "cube", () -> new WBenchObjectData(cube), () -> new JGemsPropData(cube));
        this.addResourceEntity("generic_entity", "cube", () -> new WBenchObjectData(cube).addTag(TAG_PHYSICS), () -> new JGemsEntityData(cube));
        this.addResourceMarker("generic_marker", "player_spawn", () -> new WBenchMarkerData(DefaultMarker.CURSOR_CONE, new Vector3f(0.0f, 3.0f, 0.0f), false));
        this.addResourceMarker("generic_marker", "water", () -> new WBenchMarkerData(DefaultMarker.AABB_ZONE, new Vector3f(0.0f, 0.0f, 3.0f), true));
        this.addResourceSkyCubeMap("SkyDay1", "png", new JGemsPath(JGems3D.DEFAULT_PATHS.CUBE_MAPS, "skyDay"));
    }
}