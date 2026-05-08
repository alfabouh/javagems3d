package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import workbench.resources.shaders.WBenchShaderManager;

public final class WBenchLocalShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager scene_gluing;
    public WBenchShaderManager skybox;
    public WBenchShaderManager simple_skybox_face;
    public WBenchShaderManager background;
    public WBenchShaderManager background_indirect;
    public WBenchShaderManager world_gbuffer;
    public WBenchShaderManager world_gbuffer_indirect;
    public WBenchShaderManager world_deferred;
    public WBenchShaderManager weighted_oit_simple;
    public WBenchShaderManager weighted_oit;
    public WBenchShaderManager weighted_oit_indirect;
    public WBenchShaderManager weighted_liquid_oit;
    public WBenchShaderManager simple;
    public WBenchShaderManager simple_gbuffer;
    public WBenchShaderManager depth_sun;
    public WBenchShaderManager depth_sun_indirect;
    public WBenchShaderManager depth_plight;
    public WBenchShaderManager depth_plight_indirect;
    public WBenchShaderManager gui_image;
    public WBenchShaderManager preview;
    public WBenchShaderManager simple_flat;
    public WBenchShaderManager blur5;
    public WBenchShaderManager hdr;
    public WBenchShaderManager world_ssao;
    public JGemsShaderManager blur_ssao;

    public ShaderStorageBufferObject MainSceneIndirectBufferData;
    public ShaderStorageBufferObject ParticleSceneIndirectBufferData;
    public ShaderStorageBufferObject ParticleScenePropertiesData;
    //public ShaderStorageBufferObject ShadowSceneIndirectBufferData;
    //public ShaderStorageBufferObject ShadowScenePointLightIndirectBufferData;

    public ShaderStorageBufferObject BindlessTexturesData;
    public ShaderStorageBufferObject MaterialsData;
    public ShaderStorageBufferObject MainScenePropertiesData;
    //public ShaderStorageBufferObject ShadowScenePropertiesData;
    //public ShaderStorageBufferObject ShadowScenePointLightPropertiesData;

    public ShaderStorageBufferObject TimerData;
    public ShaderStorageBufferObject SunLightData;
    public ShaderStorageBufferObject PointLightsData;
    public ShaderStorageBufferObject FogData;

    public WBenchLocalShadersInitializer() {
        super();
    }

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
        shaderStaticConstants.createConstant("MAX_PARTICLE_INDIRECT_RENDERING_DATA", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_PROPERTIES));
        shaderStaticConstants.createConstant("MAX_BINDLESS_TEXTURES", String.valueOf(JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES));
        shaderStaticConstants.createConstant("MAX_INDIRECT_RENDERING_MATERIALS", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_MATERIALS));
        shaderStaticConstants.createConstant("MAX_INDIRECT_RENDERING_PROPERIES", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_PROPERTIES));
        shaderStaticConstants.createConstant("MAX_INDIRECT_RENDERING_MESH_DATASETS", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_DATASETS));
        shaderStaticConstants.createConstant("ANIM_MAX_WEIGHTS", String.valueOf(JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS));
        shaderStaticConstants.createConstant("MAX_POINT_LIGHTS", String.valueOf(JGemsConfig.SYSTEM.MAX_POINT_LIGHTS));
        shaderStaticConstants.createConstant("MAX_POINT_LIGHTS_SHADOWS", String.valueOf(JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS));
        shaderStaticConstants.createConstant("SUN_SHADOW_CASCADES", String.valueOf(JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES));
        shaderStaticConstants.createConstant("DIFFUSE_CODE", String.valueOf(JGemsHelper.Render.DIFFUSE_CODE));
        shaderStaticConstants.createConstant("NORMALS_CODE", String.valueOf(JGemsHelper.Render.NORMALS_CODE));
        shaderStaticConstants.createConstant("EMISSION_CODE", String.valueOf(JGemsHelper.Render.EMISSION_CODE));
        shaderStaticConstants.createConstant("METALLIC_ROUGHNESS_CODE", String.valueOf(JGemsHelper.Render.METALLIC_ROUGHNESS_CODE));
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/shadows", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/shadows_simple", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/animations", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/cubemap_reflections", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/fog", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/lighting", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/oit", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/lighting_simple", ISource.Source.INSIDE_JAR));
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.TimerData = new ShaderStorageBufferObject(0, Float.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.TimerData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.MainSceneIndirectBufferData = new ShaderStorageBufferObject(1, (JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_DATASETS * Float.BYTES) + 4 * (JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_DATASETS * Integer.BYTES) + (JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBOStorage(this.MainSceneIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.BindlessTexturesData = new ShaderStorageBufferObject(2, Long.BYTES * JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES);
        ShaderStorageBufferProgram.createSSBOStorage(this.BindlessTexturesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.MaterialsData = new ShaderStorageBufferObject(3, Integer.BYTES * JGemsConfig.SYSTEM.INDIRECT_SCENE_OBJ_RENDERING_MATERIALS_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_MATERIALS);
        ShaderStorageBufferProgram.createSSBOStorage(this.MaterialsData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.MainScenePropertiesData = new ShaderStorageBufferObject(4, Integer.BYTES * JGemsConfig.SYSTEM.INDIRECT_SCENE_OBJ_RENDERING_PROPERTIES_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_SCENE_OBJ_RENDERING_MESH_PROPERTIES);
        ShaderStorageBufferProgram.createSSBOStorage(this.MainScenePropertiesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.SunLightData = new ShaderStorageBufferObject(5, Float.BYTES * JGemsConfig.SYSTEM.SUN_LIGHT_BUFFER_PACK_SIZE);
        ShaderStorageBufferProgram.createSSBOStorage(this.SunLightData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.PointLightsData = new ShaderStorageBufferObject(6, 4 * JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE + Integer.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.PointLightsData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.FogData = new ShaderStorageBufferObject(7, Float.BYTES * JGemsConfig.SYSTEM.FOG_BUFFER_PACK_SIZE);
        ShaderStorageBufferProgram.createSSBOStorage(this.FogData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.ParticleSceneIndirectBufferData = new ShaderStorageBufferObject(30,
                (JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBOStorage(this.ParticleSceneIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.ParticleScenePropertiesData = new ShaderStorageBufferObject(31, Integer.BYTES * JGemsConfig.SYSTEM.INDIRECT_PARTICLES_RENDERING_PROPERTIES_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_PARTICLES_RENDERING_MESH_PROPERTIES);
        ShaderStorageBufferProgram.createSSBOStorage(this.ParticleScenePropertiesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        //this.ShadowSceneIndirectBufferData = new ShaderStorageBufferObject(10, this.MainSceneIndirectBufferData.getBufferSize());
        //ShaderStorageBufferProgram.createSSBOStorage(this.ShadowSceneIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);
//
        //this.ShadowScenePointLightIndirectBufferData = new ShaderStorageBufferObject(11, this.MainSceneIndirectBufferData.getBufferSize());
        //ShaderStorageBufferProgram.createSSBOStorage(this.ShadowScenePointLightIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);
//
        //this.ShadowScenePropertiesData = new ShaderStorageBufferObject(14, this.MainScenePropertiesData.getBufferSize());
        //ShaderStorageBufferProgram.createSSBOStorage(this.ShadowScenePropertiesData, GL46.GL_DYNAMIC_STORAGE_BIT);
//
        //this.ShadowScenePointLightPropertiesData = new ShaderStorageBufferObject(15, this.MainScenePropertiesData.getBufferSize());
        //ShaderStorageBufferProgram.createSSBOStorage(this.ShadowScenePointLightPropertiesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.gui_image = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_image"), ISource.Source.INSIDE_JAR));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/scene_gluing"), ISource.Source.INSIDE_JAR));
        this.blur_ssao = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur_ssao"), ISource.Source.INSIDE_JAR));
        this.skybox = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/skybox"), ISource.Source.INSIDE_JAR));
        this.background = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background"), ISource.Source.INSIDE_JAR));
        this.background_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background_indirect"), ISource.Source.INSIDE_JAR));
        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_liquid_oit"), ISource.Source.INSIDE_JAR));
        this.weighted_oit = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit"), ISource.Source.INSIDE_JAR));
        this.weighted_oit_simple = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit_simple"), ISource.Source.INSIDE_JAR));
        this.weighted_oit_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit_indirect"), ISource.Source.INSIDE_JAR));
        this.world_gbuffer = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer"), ISource.Source.INSIDE_JAR));
        this.world_gbuffer_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer_indirect"), ISource.Source.INSIDE_JAR));
        this.world_deferred = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_deferred"), ISource.Source.INSIDE_JAR));
        this.simple_gbuffer = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple_gbuffer"), ISource.Source.INSIDE_JAR));
        this.simple = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple"), ISource.Source.INSIDE_JAR));
        this.depth_sun = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun"), ISource.Source.INSIDE_JAR));
        this.depth_sun_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun_indirect"), ISource.Source.INSIDE_JAR));
        this.depth_plight = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_plight"), ISource.Source.INSIDE_JAR));
        this.depth_plight_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_plight_indirect"), ISource.Source.INSIDE_JAR));
        this.blur5 = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur5"), ISource.Source.INSIDE_JAR));
        this.hdr = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/hdr"), ISource.Source.INSIDE_JAR));
        this.simple_skybox_face = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath("/assets/wbench/shaders/world/simple_skybox_face"), ISource.Source.INSIDE_JAR));
        this.preview = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath("/assets/wbench/shaders/world/preview"), ISource.Source.INSIDE_JAR));
        this.simple_flat = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath("/assets/wbench/shaders/world/simple_flat"), ISource.Source.INSIDE_JAR));
        this.world_ssao = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "computing/screen_ssao"), ISource.Source.INSIDE_JAR));
    }

    @Override
    protected WBenchShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
        return new WBenchShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
    }
}