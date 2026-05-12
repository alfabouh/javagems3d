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
import api.application.workbench.resources.data.wbench.ext.WBenchObjectInstanceExtension;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.ColorMode;
import javagems3d.system.external.mapping.tags.base.ResourceType;
import javagems3d.system.external.mapping.tags.base.VectorMode;
import javagems3d.system.external.mapping.tags.items.*;
import javagems3d.system.resources.assets.initialization.TextureAssetsInitializer;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Function;

public interface IAPIWBenchDataManager {
    String GENERIC_MARKER = "generic_marker";
    String GENERIC_PROP = "generic_prop";
    String GENERIC_ENTITY = "generic_entity";
    String DEFAULT_CUBE_OBJ = "default_cube";
    String AMBIENT_SOUND = "ambient_sound";
    String PLAYER_SPAWN = "player_spawn";
    String PARTICLE_EMITTER = "particle_emitter";
    String POINT_LIGHT = "point_light";
    String BOX_WATER = "aabb_water";

    void addResourceEntity(@Nullable String path, @NotNull ApiResourceEntity resourceEntity, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator);
    void addResourceProp(@Nullable String path, @NotNull ApiResourceProp resourceProp, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator);
    void addResourceMarker(@Nullable String path, @NotNull ApiResourceMarker resourceMarker, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator);
    void addResourceSkyCubeMap(@NotNull String name, @NotNull ICubeMapProgram.CMTextures textures);
    void addEditorObjectInstanceExtension(@Nullable String path, @NotNull APIResource<?, ?> apiResource, Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> creator);

    default void addResourceEntity(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceEntity(path, new ApiResourceEntity(id, () -> new WBenchObjectData(modelPath), () -> new JGemsEntityData(modelPath)), extCreator);
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceProp(path, new ApiResourceProp(id, () -> new WBenchObjectData(modelPath), () -> new JGemsPropData(modelPath)), extCreator);
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceMarker(path, new ApiResourceMarker(id, () -> new WBenchMarkerData(defaultMarker, color, transparent)), extCreator);
    }

    default void addResourceEntity(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceEntity(path, new ApiResourceEntity(id, fabricWBench, fabricGame), extCreator);
    }

    default void addResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame, extCreator);
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceProp(path, new ApiResourceProp(id, fabricWBench, fabricGame), extCreator);
    }

    default void addResourceProp(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceProp(null, id, fabricWBench, fabricGame, extCreator);
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceMarker(path, new ApiResourceMarker(id, fabricWBench), extCreator);
    }

    default void addResourceMarker(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench, @Nullable Function<WBenchObjectInstanceExtension.ContextData, WBenchObjectInstanceExtension> extCreator) {
        this.addResourceMarker(null, id, fabricWBench, extCreator);
    }

    default void addResourceEntity(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath) {
        this.addResourceEntity(path, new ApiResourceEntity(id, () -> new WBenchObjectData(modelPath), () -> new JGemsEntityData(modelPath)), null);
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @Nullable JGemsPathSource modelPath) {
        this.addResourceProp(path, new ApiResourceProp(id, () -> new WBenchObjectData(modelPath), () -> new JGemsPropData(modelPath)), null);
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        this.addResourceMarker(path, new ApiResourceMarker(id, () -> new WBenchMarkerData(defaultMarker, color, transparent)), null);
    }

    default void addResourceEntity(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(path, new ApiResourceEntity(id, fabricWBench, fabricGame), null);
    }

    default void addResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        this.addResourceEntity(null, id, fabricWBench, fabricGame, null);
    }

    default void addResourceProp(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(path, new ApiResourceProp(id, fabricWBench, fabricGame), null);
    }

    default void addResourceProp(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsPropData> fabricGame) {
        this.addResourceProp(null, id, fabricWBench, fabricGame, null);
    }

    default void addResourceMarker(@Nullable String path, @NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(path, new ApiResourceMarker(id, fabricWBench), null);
    }

    default void addResourceMarker(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchMarkerData> fabricWBench) {
        this.addResourceMarker(null, id, fabricWBench, null);
    }

    default void SET_DEFAULTS() {
        final Tag<TagRadioBoolean> TAG_PHYSICS = new Tag<>(TagID.DEFAULT.PHYSICS_STATE, new TagRadioBoolean(new TagRadioBoolean.Info("Is Static", true), new TagRadioBoolean.Info("Is Dynamic", false)));
        final JGemsPathSource cube = new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "cube/cube.gltf"), ISource.Source.INSIDE_JAR);
        this.addResourceProp(IAPIWBenchDataManager.GENERIC_PROP, IAPIWBenchDataManager.DEFAULT_CUBE_OBJ, () -> new WBenchObjectData(cube), () -> new JGemsPropData(cube));
        this.addResourceEntity(IAPIWBenchDataManager.GENERIC_ENTITY, IAPIWBenchDataManager.DEFAULT_CUBE_OBJ, () -> new WBenchObjectData(cube).addTag(TAG_PHYSICS), () -> new JGemsEntityData(cube));
        this.addResourceMarker(IAPIWBenchDataManager.GENERIC_MARKER, IAPIWBenchDataManager.PLAYER_SPAWN, () -> new WBenchMarkerData(DefaultMarker.CURSOR_CONE, new Vector3f(0.0f, 3.0f, 0.0f), false));
        {
            this.addResourceMarker(IAPIWBenchDataManager.GENERIC_MARKER, IAPIWBenchDataManager.AMBIENT_SOUND, () -> {
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
            });
        }
        {
            this.addResourceMarker(IAPIWBenchDataManager.GENERIC_MARKER, IAPIWBenchDataManager.PARTICLE_EMITTER, () -> {
                final TagGameResourcesList textureResource = new TagGameResourcesList("", ResourceType.TEXTURE);
                final TagFloat particleRespawnTime = new TagFloat(2.5f, -1.0f, 128.0f);
                final TagFloat particleRespawnTimeRandomOffsetRange = new TagFloat(0.1f, 0.0f, 128.0f);
                final TagFloat particleLifeTime = new TagFloat(5.0f, -1.0f, 128.0f);
                final TagFloat particleLifeTimeRandomOffsetRange = new TagFloat(0.1f, 0.0f, 128.0f);
                final TagVector particleRandomSpawnPosOffsetRange = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), -128.0f, 128.0f);
                final TagFloat particleRandomSpawnScalingOffsetRange = new TagFloat(0.0f, -16.0f, 16.0f);
                final TagVector particleBasicVelocity = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), -128.0f, 128.0f);
                final TagVector particleBasicVelocityRandomOffsetRange = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), -128.0f, 128.0f);
                final TagVector particleBasicAcceleration = new TagVector(VectorMode.VEC3F, new Vector4f(1.0f), 0.0f, 128.0f);
                final TagVector particleGravity = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), -128.0f, 128.0f);
                final TagVector particleBaseScale = new TagVector(VectorMode.VEC2F, new Vector4f(1.0f), 0.0f, 128.0f);
                final TagVector particleColorMask = new TagVector(VectorMode.VEC3F, new Vector4f(1.0f), 0.0f, 1.0f);
                final TagFloat particleBlendingTransparency = new TagFloat(0.5f, 0.0f, 1.0f);
                final TagFloat particleAlphaDiscard = new TagFloat(0.1f, -0.0f, 1.0f);
                final TagVector particleEmissiveColor = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f), 0.0f, 1.0f);
                final TagFloat particleEmissiveFactorStrength = new TagFloat(0.0f, 0.0f, 16.0f);
                final TagInt cellsX = new TagInt(1, 1, 32);
                final TagInt cellsY = new TagInt(1, 1, 32);
                final TagInt maxSprites = new TagInt(1, 1, 256);
                final TagCheckBoolean particleFadeOut = new TagCheckBoolean(true);
                final TagCheckBoolean particleAnimationLooped = new TagCheckBoolean(true);
                final TagFloat particleAnimationSpeed = new TagFloat(0.1f, 0.0f, 64.0f);
                final TagVector particleSpawnPosOffset = new TagVector(VectorMode.VEC3F, new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), -128.0f, 128.0f);
                final TagCheckBoolean particleNormalizeY = new TagCheckBoolean(false);

                final Tag<TagCheckBoolean> tag_particleNormalizeY = new Tag<>(TagID.DEFAULT.PARTICLE_NORMALIZE_Y, particleNormalizeY);
                final Tag<TagVector> tag_spawnPosOffset = new Tag<>(TagID.DEFAULT.PARTICLE_SPAWN_POS_OFFSET, particleSpawnPosOffset);
                final Tag<TagGameResourcesList> tag_texture = new Tag<>(TagID.DEFAULT.TEXTURE_PATH, textureResource);
                final Tag<TagFloat> tag_respawnTime = new Tag<>(TagID.DEFAULT.PARTICLE_RESPAWN_TIME, particleRespawnTime);
                final Tag<TagFloat> tag_respawnTimeRandom = new Tag<>(TagID.DEFAULT.PARTICLE_RESPAWN_TIME_RANDOM_OFFSET_RANGE, particleRespawnTimeRandomOffsetRange);
                final Tag<TagFloat> tag_lifeTime = new Tag<>(TagID.DEFAULT.PARTICLE_LIFE_TIME, particleLifeTime);
                final Tag<TagFloat> tag_lifeTimeRandom = new Tag<>(TagID.DEFAULT.PARTICLE_LIFE_TIME_RANDOM_OFFSET_RANGE, particleLifeTimeRandomOffsetRange);
                final Tag<TagVector> tag_spawnPosRandom = new Tag<>(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_POS_OFFSET_RANGE, particleRandomSpawnPosOffsetRange);
                final Tag<TagFloat> tag_spawnScaleRandom = new Tag<>(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_SCALING_OFFSET_RANGE, particleRandomSpawnScalingOffsetRange);
                final Tag<TagVector> tag_velocity = new Tag<>(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY, particleBasicVelocity);
                final Tag<TagVector> tag_velocityRandom = new Tag<>(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY_RANDOM_OFFSET_RANGE, particleBasicVelocityRandomOffsetRange);
                final Tag<TagVector> tag_acceleration = new Tag<>(TagID.DEFAULT.PARTICLE_BASIC_ACCELERATION, particleBasicAcceleration);
                final Tag<TagVector> tag_gravity = new Tag<>(TagID.DEFAULT.PARTICLE_GRAVITY, particleGravity);
                final Tag<TagVector> tag_baseScale = new Tag<>(TagID.DEFAULT.PARTICLE_BASE_SCALE, particleBaseScale);
                final Tag<TagVector> tag_colorMask = new Tag<>(TagID.DEFAULT.PARTICLE_COLOR_MASK, particleColorMask);
                final Tag<TagFloat> tag_transparency = new Tag<>(TagID.DEFAULT.PARTICLE_BLENDING_TRANSPARENCY, particleBlendingTransparency);
                final Tag<TagFloat> tag_alphaDiscard = new Tag<>(TagID.DEFAULT.PARTICLE_ALPHA_DISCARD, particleAlphaDiscard);
                final Tag<TagVector> tag_emissiveColor = new Tag<>(TagID.DEFAULT.PARTICLE_EMISSIVE_COLOR, particleEmissiveColor);
                final Tag<TagFloat> tag_emissiveStrength = new Tag<>(TagID.DEFAULT.PARTICLE_EMISSIVE_FACTOR_STRENGTH, particleEmissiveFactorStrength);
                final Tag<TagCheckBoolean> tag_fadeOut = new Tag<>(TagID.DEFAULT.PARTICLE_FADE_OUT, particleFadeOut);
                final Tag<TagInt> tag_cellsX = new Tag<>(TagID.DEFAULT.CELLS_X, cellsX);
                final Tag<TagInt> tag_cellsY = new Tag<>(TagID.DEFAULT.CELLS_Y, cellsY);
                final Tag<TagInt> tag_maxSprites = new Tag<>(TagID.DEFAULT.MAX_SPRITES, maxSprites);
                final Tag<TagCheckBoolean> tag_animationLooped = new Tag<>(TagID.DEFAULT.PARTICLE_ANIMATION_LOOPED, particleAnimationLooped);
                final Tag<TagFloat> tag_animationSpeed = new Tag<>(TagID.DEFAULT.PARTICLE_ANIMATION_SPEED, particleAnimationSpeed);
                final WBenchMarkerData wBenchMarkerData = new WBenchMarkerData(DefaultMarker.POINT, new Vector3f(1.0f, 1.0f, 1.0f), false);

                wBenchMarkerData.addTags(
                        tag_texture,
                        tag_respawnTime,
                        tag_respawnTimeRandom,
                        tag_lifeTime,
                        tag_lifeTimeRandom,
                        tag_spawnPosRandom,
                        tag_spawnPosOffset,
                        tag_velocity,
                        tag_velocityRandom,
                        tag_acceleration,
                        tag_gravity,
                        tag_baseScale,
                        tag_spawnScaleRandom,
                        tag_colorMask,
                        tag_transparency,
                        tag_alphaDiscard,
                        tag_emissiveColor,
                        tag_emissiveStrength,
                        tag_fadeOut,
                        tag_cellsX,
                        tag_cellsY,
                        tag_maxSprites,
                        tag_animationLooped,
                        tag_animationSpeed,
                        tag_particleNormalizeY
                );
                return wBenchMarkerData;
            }, ParticleEmitterObjectExtension::new);
        }
        {
            this.addResourceMarker(IAPIWBenchDataManager.GENERIC_MARKER, IAPIWBenchDataManager.POINT_LIGHT, () -> {
                final Tag<TagColor> colorTag = Tag.create(TagID.DEFAULT.COLOR3, new TagColor(ColorMode.COLOR3, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)));
                final Tag<TagFloat> brightnessTag = Tag.create(TagID.DEFAULT.BRIGHTNESS, new TagFloat(1.0f, 0.0f, 24.0f));
                final Tag<TagCheckBoolean> enableShadowMap = Tag.create(TagID.DEFAULT.SHADOW_MAP, new TagCheckBoolean(false));
                final Tag<TagVector> offset = Tag.create(new TagID(TagID.DEFAULT.FLOAT3, "Offset"), new TagVector(VectorMode.VEC3F, new Vector4f(0.0f), -32.0f, 32.0f));

                final WBenchMarkerData wBenchMarkerData = new WBenchMarkerData(DefaultMarker.POINT, new Vector3f(1.0f, 1.0f, 1.0f), false);
                wBenchMarkerData.addTags(colorTag);
                wBenchMarkerData.addTags(brightnessTag);
                wBenchMarkerData.addTags(offset);
                wBenchMarkerData.addTags(enableShadowMap);

                return wBenchMarkerData;
            }, PointLightObjectExtension::new);
        }
        this.addResourceMarker(IAPIWBenchDataManager.GENERIC_MARKER, IAPIWBenchDataManager.BOX_WATER, () -> new WBenchMarkerData(DefaultMarker.AABB_ZONE, new Vector3f(0.0f, 0.0f, 3.0f), true));
        this.addResourceSkyCubeMap("SkyDay1", TextureAssetsInitializer.DEF_CUBE_MAP_TEXTURES);
    }

    class PointLightObjectExtension extends WBenchObjectInstanceExtension {
        private PointLight pointLight;

        public PointLightObjectExtension(@NotNull ContextData dataPack) {
            super(dataPack);
        }

        @Override
        public Vector3f textInMenuColor() {
            return new Vector3f(1.0f, 0.0f, 1.0f);
        }

        @Override
        public int orderInItemsList() {
            return 2;
        }

        @Override
        public boolean shouldMarkerBeFullLighted() {
            return true;
        }

        @Override
        public void onSpawnExt(IRenderWorld world) {
            this.pointLight = new PointLight(this.getContextData().sceneProp().getPosition(), new Vector3f(1.0f)).on();
            this.pointLight.setActionOnDetach(ILightAttachable.ActionOnDetach.DESTROY);
            world.addLight(this.pointLight, this.getContextData().sceneProp());
        }

        @Override
        public void onDestroyExt(IRenderWorld world) {
            if (this.pointLight != null) {
                world.removeLight(this.pointLight);
            }
        }

        @Override
        public void onUpdateExt(IRenderWorld world) {
            if (this.pointLight != null) {
                this.pointLight.setLightColor(this.returnNewMarkerColor());
                this.pointLight.setBrightness(this.getBrightness(this.getContextData().tagsContainer().get()));
                this.pointLight.setOffset(this.getLightOffset(this.getContextData().tagsContainer().get()));
                this.pointLight.setEnableShadowMap(this.enableShadows(this.getContextData().tagsContainer().get()));
            }
        }

        @Override
        public @Nullable Vector3f returnNewMarkerColor() {
            Vector4f tagColor = this.getContextData().tagsContainer().get().getTag(TagID.DEFAULT.COLOR3).<TagColor>getTagItemUnsafeCast().getColorVector();
            return new Vector3f(tagColor.x, tagColor.y, tagColor.z);
        }

        public float getBrightness(TagsContainer tagsContainer) {
            return tagsContainer.getTag(TagID.DEFAULT.BRIGHTNESS).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public Vector3f getLightOffset(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.FLOAT3)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.FLOAT3).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public boolean enableShadows(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.SHADOW_MAP)) {
                return false;
            }
            return tagsContainer.getTag(TagID.DEFAULT.SHADOW_MAP).<TagCheckBoolean>getTagItemUnsafeCast().isFlag();
        }

       //@Override
       //public void onCopiedTagsContainerExt(TagsContainer tagsContainer) {
       //    tagsContainer.replaceTag(TagID.DEFAULT.OBJECT_LIST, new TagObjectsList());
       //}
    }

    class ParticleEmitterObjectExtension extends WBenchObjectInstanceExtension {
        private ParticleEmitter particleEmitter;

        public ParticleEmitterObjectExtension(@NotNull ContextData dataPack) {
            super(dataPack);
        }

        @Override
        public Vector3f textInMenuColor() {
            return new Vector3f(1.0f, 0.5f, 0.75f);
        }

        @Override
        public int orderInItemsList() {
            return 3;
        }

        @Override
        public boolean shouldMarkerBeFullLighted() {
            return false;
        }
        
        @Override
        public void onTagsContainerAnyTagModified(TagsContainer tagsContainer, SceneProp sceneObject) {
            this.refreshEmitter(tagsContainer, this.getContextData().sceneProp());
        }

        private void refreshEmitter(TagsContainer tagsContainer, SceneProp sceneProp) {
            final IParticlesManager particlesManager = sceneProp.getWorld().getEnvironment().getParticlesScene().getParticlesManager();
            if (this.particleEmitter != null) {
                this.particleEmitter.setDead();
            }
            if (this.texture2DProgram(tagsContainer) == null) {
                return;
            }
            this.particleEmitter = particlesManager.spawnParticleFXEmitter(
                    particlesManager.createDefaultWorldParticleEmitter(
                            this.getContextData().sceneProp().getPosition(),
                            this.texture2DProgram(tagsContainer),
                            new ParticleFXSpriteProperties(
                                    new Vector2i(this.getCellsX(tagsContainer), this.getCellsY(tagsContainer)),
                                    this.getMaxSprites(tagsContainer),
                                    this.isParticleAnimationLooped(tagsContainer),
                                    this.getParticleAnimationSpeed(tagsContainer),
                                    this.isParticleFadeOutEnabled(tagsContainer),
                                    this.isParticleNormalizedY(tagsContainer)),
                            sceneProp.getWorld().getEnvironment(), -1.0f)
            );
            this.particleEmitter.getEmitterProperties()
                    .setSpawnPosOffset(this.getParticleSpawnPosOffset(tagsContainer))
                    .setParticleRandomSpawnPosOffsetRange(this.getParticleRandomSpawnPosOffsetRange(tagsContainer))
                    .setParticleRandomSpawnScalingOffsetRange(this.getParticleRandomSpawnScalingOffsetRange(tagsContainer))

                    .setParticleLifeTime(this.getParticleLifeTime(tagsContainer))
                    .setParticleLifeTimeRandomOffsetRange(this.getParticleLifeTimeRandomOffsetRange(tagsContainer))

                    .setParticleRespawnTime(this.getParticleRespawnTime(tagsContainer))
                    .setParticleRespawnTimeRandomOffsetRange(this.getParticleRespawnTimeRandomOffsetRange(tagsContainer))

                    .setParticleAlphaDiscard(this.getParticleAlphaDiscard(tagsContainer))
                    .setParticleBlendingTransparency(this.getParticleBlendingTransparency(tagsContainer))

                    .setParticleBaseScale(this.getParticleBaseScale(tagsContainer))
                    .setParticleColorMask(this.getParticleColorMask(tagsContainer))

                    .setParticleBasicVelocity(this.getParticleBasicVelocity(tagsContainer))
                    .setParticleBasicVelocityRandomOffsetRange(this.getParticleBasicVelocityRandomOffsetRange(tagsContainer))
                    .setParticleBasicAcceleration(this.getParticleBasicAcceleration(tagsContainer))

                    .setParticleGravity(this.getParticleGravity(tagsContainer))

                    .setParticleEmissiveColor(this.getParticleEmissiveColor(tagsContainer))
                    .setParticleEmissiveFactorStrength(this.getParticleEmissiveFactorStrength(tagsContainer));
        }

        @Override
        public void onApplySnapshot(TagsContainer tagsContainer, SceneProp sceneObject) {
            this.refreshEmitter(tagsContainer, sceneObject);
        }

        @Override
        public void onSpawnExt(IRenderWorld world) {
            this.refreshEmitter(this.getContextData().tagsContainer().get(), this.getContextData().sceneProp());
        }

        @Override
        public void onDestroyExt(IRenderWorld world) {
            if (this.particleEmitter != null) {
                this.particleEmitter.setDead();
            }
        }

        @Override
        public void onUpdateExt(IRenderWorld world) {
            if (this.particleEmitter != null) {
                this.particleEmitter.setEmitterPosition(this.getContextData().sceneProp().getPosition());
            }
        }

        @Override
        public @Nullable Vector3f returnNewMarkerColor() {
            return new Vector3f(1.0f);
        }

        public Vector3f getParticleSpawnPosOffset(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_SPAWN_POS_OFFSET)) {
                return new Vector3f(0.0f);
            }

            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_SPAWN_POS_OFFSET).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public ImageTexture texture2DProgram(TagsContainer tagsContainer) {
            GameResourceTextureAsset gameResourceTextureAsset = this.getContextData().utilityFunctions().extractTextureFromLocalCache().apply(this.getParticleTexture(tagsContainer));
            if (gameResourceTextureAsset != null && gameResourceTextureAsset.texture2DProgram() instanceof ImageTexture imageTexture) {
                return imageTexture;
            }
            return null;
        }

        public String getParticleTexture(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.TEXTURE_PATH)) {
                return "";
            }
            return tagsContainer.getTag(TagID.DEFAULT.TEXTURE_PATH).<TagGameResourcesList>getTagItemUnsafeCast().getValue();
        }

        public float getParticleRespawnTime(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_RESPAWN_TIME)) {
                return 0.1f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_RESPAWN_TIME).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public float getParticleRespawnTimeRandomOffsetRange(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_RESPAWN_TIME_RANDOM_OFFSET_RANGE)) {
                return 0.1f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_RESPAWN_TIME_RANDOM_OFFSET_RANGE).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public float getParticleLifeTime(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_LIFE_TIME)) {
                return 0.1f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_LIFE_TIME).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public float getParticleLifeTimeRandomOffsetRange(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_LIFE_TIME_RANDOM_OFFSET_RANGE)) {
                return 0.1f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_LIFE_TIME_RANDOM_OFFSET_RANGE).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public Vector3f getParticleRandomSpawnPosOffsetRange(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_POS_OFFSET_RANGE)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_POS_OFFSET_RANGE).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public float getParticleRandomSpawnScalingOffsetRange(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_SCALING_OFFSET_RANGE) || !(tagsContainer.getTag(TagID.DEFAULT.PARTICLE_RANDOM_SPAWN_SCALING_OFFSET_RANGE).getTagItem() instanceof TagFloat tagFloat)) {
                return 0.0f;
            }
            return tagFloat.getValue();
        }

        public Vector3f getParticleBasicVelocity(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public Vector3f getParticleBasicVelocityRandomOffsetRange(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY_RANDOM_OFFSET_RANGE)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_BASIC_VELOCITY_RANDOM_OFFSET_RANGE).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public Vector3f getParticleBasicAcceleration(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_BASIC_ACCELERATION)) {
                return new Vector3f(1.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_BASIC_ACCELERATION).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public Vector3f getParticleGravity(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_GRAVITY)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_GRAVITY).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public boolean isParticleNormalizedY(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_NORMALIZE_Y)) {
                return true;
            }

            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_NORMALIZE_Y).<TagCheckBoolean>getTagItemUnsafeCast().isFlag();
        }

        public boolean isParticleAnimationLooped(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_ANIMATION_LOOPED)) {
                return true;
            }

            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_ANIMATION_LOOPED).<TagCheckBoolean>getTagItemUnsafeCast().isFlag();
        }

        public float getParticleAnimationSpeed(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_ANIMATION_SPEED)) {
                return 1.0f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_ANIMATION_SPEED).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public Vector2f getParticleBaseScale(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_BASE_SCALE)) {
                return new Vector2f(1.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_BASE_SCALE).<TagVector>getTagItemUnsafeCast().getValues().xy(new Vector2f());
        }

        public Vector3f getParticleColorMask(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_COLOR_MASK)) {
                return new Vector3f(1.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_COLOR_MASK).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public float getParticleBlendingTransparency(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_BLENDING_TRANSPARENCY)) {
                return 0.5f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_BLENDING_TRANSPARENCY).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public float getParticleAlphaDiscard(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_ALPHA_DISCARD)) {
                return 0.1f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_ALPHA_DISCARD).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public Vector3f getParticleEmissiveColor(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_EMISSIVE_COLOR)) {
                return new Vector3f(0.0f);
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_EMISSIVE_COLOR).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
        }

        public float getParticleEmissiveFactorStrength(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_EMISSIVE_FACTOR_STRENGTH)) {
                return 0.0f;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_EMISSIVE_FACTOR_STRENGTH).<TagFloat>getTagItemUnsafeCast().getValue();
        }

        public int getCellsX(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.CELLS_X)) {
                return 1;
            }
            return tagsContainer.getTag(TagID.DEFAULT.CELLS_X).<TagInt>getTagItemUnsafeCast().getValue();
        }

        public int getCellsY(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.CELLS_Y)) {
                return 1;
            }
            return tagsContainer.getTag(TagID.DEFAULT.CELLS_Y).<TagInt>getTagItemUnsafeCast().getValue();
        }

        public int getMaxSprites(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.MAX_SPRITES)) {
                return 1;
            }
            return tagsContainer.getTag(TagID.DEFAULT.MAX_SPRITES).<TagInt>getTagItemUnsafeCast().getValue();
        }

        public boolean isParticleFadeOutEnabled(TagsContainer tagsContainer) {
            if (!tagsContainer.hasTag(TagID.DEFAULT.PARTICLE_FADE_OUT)) {
                return true;
            }
            return tagsContainer.getTag(TagID.DEFAULT.PARTICLE_FADE_OUT).<TagCheckBoolean>getTagItemUnsafeCast().isFlag();
        }
    }
}