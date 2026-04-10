package javagems3d.system.resources.assets.initialization;

import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
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

public final class GlobalShadersInitializer extends ShadersInitializer<JGemsShaderManager> {
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
    public JGemsShaderManager weighted_liquid_oit;
    public JGemsShaderManager simple;
    public JGemsShaderManager simple_gbuffer;
    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_sun_indirect;
    public JGemsShaderManager depth_plight;
    public JGemsShaderManager debug;
    public JGemsShaderManager imgui;
    public JGemsShaderManager alpha_scanning;

    public ShaderStorageBufferObject MainSceneIndirectBufferData;
    public ShaderStorageBufferObject ShadowSceneIndirectBufferData;
    public ShaderStorageBufferObject BindlessTexturesData;
    public ShaderStorageBufferObject MaterialsData;
    public ShaderStorageBufferObject PropertiesData;
    public ShaderStorageBufferObject TimerData;
    public ShaderStorageBufferObject SunLightData;
    public ShaderStorageBufferObject PointLightsData;
    public ShaderStorageBufferObject FogData;
    public ShaderStorageBufferObject TextureScan;
    public ShaderStorageBufferObject ModelVertexesData;
    public ShaderStorageBufferObject AABBResult;

    private static int ssboID;

    public GlobalShadersInitializer() {
    }

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
        shaderStaticConstants.createConstant("MAX_BINDLESS_TEXTURES", String.valueOf(JGemsConfig.SYSTEM.MAX_BINDLESS_TEXTURES));
        shaderStaticConstants.createConstant("MAX_INDIRECT_RENDERING_MESH_DATASETS", String.valueOf(JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS));
        shaderStaticConstants.createConstant("MAX_VERTEXES_IN_MODEL", String.valueOf(JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL));
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
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/animations", ISource.Source.INSIDE_JAR));
        shaderLibrary.createLibrary(new JGemsPathSource("/assets/shaders/libs/lighting", ISource.Source.INSIDE_JAR));
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.TimerData = new ShaderStorageBufferObject(0, Float.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.TimerData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.MainSceneIndirectBufferData = new ShaderStorageBufferObject(1, (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Float.BYTES) + 4 * (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Integer.BYTES) + (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBOStorage(this.MainSceneIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);

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

        this.TextureScan = new ShaderStorageBufferObject(8, Integer.BYTES);
        ShaderStorageBufferProgram.createSSBOStorage(this.TextureScan, GL46.GL_DYNAMIC_STORAGE_BIT | GL46.GL_MAP_READ_BIT | GL46.GL_MAP_PERSISTENT_BIT | GL46.GL_MAP_COHERENT_BIT);
        ShaderStorageBufferProgram.mapBuffer(this.TextureScan, GL46.GL_MAP_READ_BIT | GL46.GL_MAP_PERSISTENT_BIT | GL46.GL_MAP_COHERENT_BIT);

        this.ShadowSceneIndirectBufferData = new ShaderStorageBufferObject(10, (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Float.BYTES) + 4 * (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * Integer.BYTES) + (JGemsConfig.SYSTEM.MAX_INDIRECT_RENDERING_MESH_DATASETS * 16 * Float.BYTES));
        ShaderStorageBufferProgram.createSSBOStorage(this.ShadowSceneIndirectBufferData, GL46.GL_DYNAMIC_STORAGE_BIT);

        this.alpha_scanning = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "computing/alpha_scanning"), ISource.Source.INSIDE_JAR));
        this.debug = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "debug"), ISource.Source.INSIDE_JAR));
        this.gui_text = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_text"), ISource.Source.INSIDE_JAR));
        this.gui_noised = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_noised"), ISource.Source.INSIDE_JAR));
        this.gui_button = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_button"), ISource.Source.INSIDE_JAR));
        this.gui_image = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_image"), ISource.Source.INSIDE_JAR));
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/gui_image_selectable"), ISource.Source.INSIDE_JAR));
        this.blur_ssao = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur_ssao"), ISource.Source.INSIDE_JAR));
        this.blur5 = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur5"), ISource.Source.INSIDE_JAR));
        this.blur9 = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur9"), ISource.Source.INSIDE_JAR));
        this.blur13 = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur13"), ISource.Source.INSIDE_JAR));
        this.blur_box = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/blur_box"), ISource.Source.INSIDE_JAR));
        this.imgui = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/imgui"), ISource.Source.INSIDE_JAR));
        this.fxaa = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/fxaa"), ISource.Source.INSIDE_JAR));
        this.hdr = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/hdr"), ISource.Source.INSIDE_JAR));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "post/scene_gluing"), ISource.Source.INSIDE_JAR));
        this.skybox = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/skybox"), ISource.Source.INSIDE_JAR));
        this.background = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background"), ISource.Source.INSIDE_JAR));
        this.background_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/background_indirect"), ISource.Source.INSIDE_JAR));
        this.world_ssao = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "computing/screen_ssao"), ISource.Source.INSIDE_JAR));
        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_liquid_oit"), ISource.Source.INSIDE_JAR));
        this.weighted_oit = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit"), ISource.Source.INSIDE_JAR));
        this.weighted_oit_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "oit/weighted_oit_indirect"), ISource.Source.INSIDE_JAR));
        this.world_gbuffer = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer"), ISource.Source.INSIDE_JAR));
        this.world_gbuffer_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_gbuffer_indirect"), ISource.Source.INSIDE_JAR));
        this.world_deferred = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/world_deferred"), ISource.Source.INSIDE_JAR));
        this.menu = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/menu"), ISource.Source.INSIDE_JAR));
        this.simple_gbuffer = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple_gbuffer"), ISource.Source.INSIDE_JAR));
        this.simple = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "world/simple"), ISource.Source.INSIDE_JAR));
        this.depth_sun = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun"), ISource.Source.INSIDE_JAR));
        this.depth_sun_indirect = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_sun_indirect"), ISource.Source.INSIDE_JAR));
        this.depth_plight = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "shadows/depth_plight"), ISource.Source.INSIDE_JAR));
    }

    @Override
    protected JGemsShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
        return new JGemsShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
    }
}