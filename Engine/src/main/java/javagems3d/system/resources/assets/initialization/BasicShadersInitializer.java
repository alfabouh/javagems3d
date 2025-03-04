package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import org.lwjgl.opengl.GL46;

public final class BasicShadersInitializer extends ShadersInitializer<JGemsShaderManager> {
    public JGemsShaderManager menu;
    public JGemsShaderManager gui_text;
    public JGemsShaderManager gui_noised;
    public JGemsShaderManager gui_image;
    public JGemsShaderManager gui_button;
    public JGemsShaderManager gui_image_selectable;
    public JGemsShaderManager blur5;
    public JGemsShaderManager blur9;
    public JGemsShaderManager blur13;
    public JGemsShaderManager blur_box;
    public JGemsShaderManager blur_ssao;
    public JGemsShaderManager hdr;
    public JGemsShaderManager scene_gluing;
    public JGemsShaderManager fxaa;
    public JGemsShaderManager skybox;
    public JGemsShaderManager background;
    public JGemsShaderManager background_indirect;
    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_gbuffer_indirect;
    public JGemsShaderManager world_ssao;
    public JGemsShaderManager world_deferred;
    public JGemsShaderManager weighted_oit;
    public JGemsShaderManager weighted_oit_indirect;
    public JGemsShaderManager weighted_particle_oit;
    public JGemsShaderManager weighted_liquid_oit;
    public JGemsShaderManager simple;
    public JGemsShaderManager simple_gbuffer;
    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_sun_indirect;
    public JGemsShaderManager depth_plight;
    public JGemsShaderManager debug;
    public JGemsShaderManager imgui;

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
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/shadows"));
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/animations"));
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

        this.debug = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "debug"));
        this.gui_text = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/gui_text"));
        this.gui_noised = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/gui_noised"));
        this.gui_button = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/gui_button"));
        this.gui_image = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/gui_image"));
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/gui_image_selectable"));
        this.blur_ssao = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/blur_ssao"));
        this.blur5 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/blur5"));
        this.blur9 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/blur9"));
        this.blur13 = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/blur13"));
        this.blur_box = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/blur_box"));
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/imgui"));
        this.fxaa = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/fxaa"));
        this.hdr = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/hdr"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/scene_gluing"));
        this.skybox = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/skybox"));
        this.background = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/background"));
        this.background_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/background_indirect"));
        this.world_ssao = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "post/screen_ssao"));
        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "oit/weighted_liquid_oit"));
        this.weighted_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "oit/weighted_oit"));
        this.weighted_oit_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "oit/weighted_oit_indirect"));
        this.weighted_particle_oit = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "oit/weighted_particle_oit"));
        this.world_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/world_gbuffer"));
        this.world_gbuffer_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/world_gbuffer_indirect"));
        this.world_deferred = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/world_deferred"));
        this.menu = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "gui/menu"));
        this.simple_gbuffer = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/simple_gbuffer"));
        this.simple = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "world/simple"));
        this.depth_sun = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "shadows/depth_sun"));
        this.depth_sun_indirect = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "shadows/depth_sun_indirect"));
        this.depth_plight = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEF_PATHS.SHADERS, "shadows/depth_plight"));
    }

    @Override
    protected JGemsShaderManager createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        return new JGemsShaderManager(new ShadersContainer(shaderStaticConstants, shaderLibrary, shaderPath));
    }
}