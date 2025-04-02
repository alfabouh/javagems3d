package workbench.resources.initialization;

import api.application.workbench.manager.IAPIWBenchDataManager;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import org.lwjgl.opengl.GL46;
import workbench.resources.shaders.WBenchShaderManager;

public final class LocalShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager scene_gluing;
    public WBenchShaderManager skybox;
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
    public WBenchShaderManager gui_image;
    public WBenchShaderManager preview;
    public WBenchShaderManager simple_flat;
    public WBenchShaderManager blur5;

    public ShaderStorageBufferObject IndirectBufferData;
    public ShaderStorageBufferObject BindlessTexturesData;
    public ShaderStorageBufferObject MaterialsData;
    public ShaderStorageBufferObject PropertiesData;
    public ShaderStorageBufferObject TimerData;
    public ShaderStorageBufferObject SunLightData;
    public ShaderStorageBufferObject PointLightsData;
    public ShaderStorageBufferObject FogData;

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
        shaderStaticConstants.putConstant("MAX_BINDLESS_TEXTURES", String.valueOf(JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES));
        shaderStaticConstants.putConstant("MAX_INDIRECT_RENDERING_MESH_DATASETS", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS));
        shaderStaticConstants.putConstant("ANIM_MAX_WEIGHTS", String.valueOf(JGemsConfig.SYSTEM.ANIM_MAX_WEIGHTS));
        shaderStaticConstants.putConstant("MAX_POINT_LIGHTS", String.valueOf(JGemsConfig.SYSTEM.MAX_POINT_LIGHTS));
        shaderStaticConstants.putConstant("MAX_POINT_LIGHTS_SHADOWS", String.valueOf(JGemsConfig.SYSTEM.MAX_POINT_LIGHTS_SHADOWS));
        shaderStaticConstants.putConstant("SUN_SHADOW_CASCADES", String.valueOf(JGemsConfig.SYSTEM.SUN_SHADOW_CASCADES));

        shaderStaticConstants.putConstant("DIFFUSE_CODE", String.valueOf(JGemsRenderingHelper.DIFFUSE_CODE));
        shaderStaticConstants.putConstant("NORMALS_CODE", String.valueOf(JGemsRenderingHelper.NORMALS_CODE));
        shaderStaticConstants.putConstant("EMISSION_CODE", String.valueOf(JGemsRenderingHelper.EMISSION_CODE));
        shaderStaticConstants.putConstant("METALLIC_ROUGHNESS_CODE", String.valueOf(JGemsRenderingHelper.METALLIC_ROUGHNESS_CODE));
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/shadows"));
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/animations"));
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/lighting"));
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.TimerData = new ShaderStorageBufferObject(0, Float.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.TimerData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.IndirectBufferData = new ShaderStorageBufferObject(1, (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Float.BYTES) + 4 * (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Integer.BYTES) + (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBOStorage(this.IndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.BindlessTexturesData = new ShaderStorageBufferObject(2, Long.BYTES * JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES);
        ShaderStorageBufferProgram.createSSBOStorage(this.BindlessTexturesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.MaterialsData = new ShaderStorageBufferObject(3, Integer.BYTES * JGemsConfig.SYSTEM.INDIRECT_RENDERING_MATERIALS_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_MATERIALS);
        ShaderStorageBufferProgram.createSSBOStorage(this.MaterialsData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.PropertiesData = new ShaderStorageBufferObject(4, Integer.BYTES * JGemsConfig.SYSTEM.INDIRECT_RENDERING_PROPERTIES_PACK_SIZE * JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_PROPERTIES);
        ShaderStorageBufferProgram.createSSBOStorage(this.PropertiesData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.SunLightData = new ShaderStorageBufferObject(5, Float.BYTES * JGemsConfig.SYSTEM.SUN_LIGHT_BUFFER_PACK_SIZE);
        ShaderStorageBufferProgram.createSSBOStorage(this.SunLightData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.PointLightsData = new ShaderStorageBufferObject(6, 4 * JGemsConfig.SYSTEM.POINT_LIGHT_BUFFER_PACK_SIZE + Integer.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.PointLightsData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.FogData = new ShaderStorageBufferObject(7, Float.BYTES * JGemsConfig.SYSTEM.FOG_BUFFER_PACK_SIZE);
        ShaderStorageBufferProgram.createSSBOStorage(this.FogData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.gui_image = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_image"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/scene_gluing"));
        this.skybox = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/skybox"));
        this.background = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background"));
        this.background_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background_indirect"));
        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_liquid_oit"));
        this.weighted_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit"));
        this.weighted_oit_simple = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit_simple"));
        this.weighted_oit_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit_indirect"));
        this.world_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer"));
        this.world_gbuffer_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer_indirect"));
        this.world_deferred = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_deferred"));
        this.simple_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple_gbuffer"));
        this.simple = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple"));
        this.depth_sun = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun"));
        this.depth_sun_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun_indirect"));
        this.depth_plight = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_plight"));
        this.blur5 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur5"));

        this.preview = this.createShaderManager(resourceCache, new JGemsPath("/assets/wbench/shaders/world/preview"));
        this.simple_flat = this.createShaderManager(resourceCache, new JGemsPath("/assets/wbench/shaders/world/simple_flat"));
    }

    @Override
    protected WBenchShaderManager createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        return new WBenchShaderManager(new ShadersContainer(shaderStaticConstants, shaderLibrary, shaderPath));
    }
}