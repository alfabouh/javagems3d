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
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.base.ResourceType;
import javagems3d.system.external.mapping.tags.items.TagFloat;
import javagems3d.system.external.mapping.tags.items.TagGameResourcesList;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;
import javagems3d.system.resources.assets.initialization.TextureAssetsInitializer;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IAPIWBenchDataManager {
    void addResourceEntity(@Nullable String path, @NotNull ApiResourceEntity resourceEntity);
    void addResourceProp(@Nullable String path, @NotNull ApiResourceProp resourceProp);
    void addResourceMarker(@Nullable String path, @NotNull ApiResourceMarker resourceMarker);
    void addResourceSkyCubeMap(@NotNull String name, @NotNull ICubeMapProgram.CMTextures textures);

    default void addResourceEntity(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath) {
        this.addResourceEntity(path, new ApiResourceEntity(id, () -> new WBenchObjectData(modelPath), () -> new JGemsEntityData(modelPath)));
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath) {
        this.addResourceProp(path, new ApiResourceProp(id, () -> new WBenchObjectData(modelPath), () -> new JGemsPropData(modelPath)));
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        this.addResourceMarker(path, new ApiResourceMarker(id, () -> new WBenchMarkerData(defaultMarker, color, transparent)));
    }


    default void addResourceEntity(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(path, new ApiResourceEntity(id, fabricWBench, fabricGame));
    }

    default void addResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame);
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(path, new ApiResourceProp(id, fabricWBench, fabricGame));
    }

    default void addResourceProp(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(null, id, fabricWBench, fabricGame);
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(path, new ApiResourceMarker(id, fabricWBench));
    }

    default void addResourceMarker(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(null, id, fabricWBench);
    }

    default void SET_DEFAULTS() {
        final Tag<TagRadioBoolean> TAG_PHYSICS = new Tag<>(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Is Static", true), new TagRadioBoolean.Info("Is Dynamic", false)));
        final JGemsPathSource cube = new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf"), ISource.Source.INSIDE_JAR);
        this.addResourceProp("generic_prop", "cube", () -> new WBenchObjectData(cube), () -> new JGemsPropData(cube));
        this.addResourceEntity("generic_entity", "cube", () -> new WBenchObjectData(cube).addTag(TAG_PHYSICS), () -> new JGemsEntityData(cube));
        this.addResourceMarker("generic_marker", "player_spawn", () -> new WBenchMarkerData(DefaultMarker.CURSOR_CONE, new Vector3f(0.0f, 3.0f, 0.0f), false));
        {
            this.addResourceMarker("generic_marker", "ambient_sound", () -> {{
                final TagFloat volume = new TagFloat(0.5f, 0.0f, 128.0f);
                final TagFloat pitch = new TagFloat(1.0f, 0.0f, 3.0f);
                final TagFloat distance = new TagFloat(16.0f, -1.0f, 128.0f);
                final TagGameResourcesList soundResource = new TagGameResourcesList("", ResourceType.SOUND);
                final Tag<TagFloat> tag_volume = new Tag<>(TagID.DEFAULT.SOUND_VOLUME, volume);
                final Tag<TagFloat> tag_pitch = new Tag<>(TagID.DEFAULT.SOUND_PITCH, pitch);
                final Tag<TagFloat> tag_distance = new Tag<>(TagID.DEFAULT.SOUND_DISTANCE, distance);
                final Tag<TagGameResourcesList> tag_sound = new Tag<>(TagID.DEFAULT.SOUND_PATH, soundResource);
                final WBenchMarkerData wBenchMarkerData = new WBenchMarkerData(DefaultMarker.POINT, new Vector3f(3.0f, 0.0f, 0.0f), false);
                wBenchMarkerData.addTags(tag_volume, tag_pitch, tag_distance, tag_sound);
                return wBenchMarkerData;
            }});
        }
        this.addResourceMarker("generic_marker", "water", () -> new WBenchMarkerData(DefaultMarker.AABB_ZONE, new Vector3f(0.0f, 0.0f, 3.0f), true));
        this.addResourceSkyCubeMap("SkyDay1", TextureAssetsInitializer.DEF_CUBE_MAP_TEXTURES);
    }
}