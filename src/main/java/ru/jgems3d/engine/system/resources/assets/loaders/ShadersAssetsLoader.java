package ru.jgems3d.engine.system.resources.assets.loaders;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.environment.light.LightManager;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

public class ShadersAssetsLoader extends ShadersLoader<JGemsShaderManager> {

    public UniformBufferObject SunLight;
    public UniformBufferObject PointLights;
    public UniformBufferObject Misc;
    public UniformBufferObject Fog;

    public JGemsShaderManager world_enemy;
    public JGemsShaderManager world_pickable;
    public JGemsShaderManager menu;
    public JGemsShaderManager gameUbo;
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
    public JGemsShaderManager fxaa;
    public JGemsShaderManager skybox;
    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_ssao;
    public JGemsShaderManager world_deferred;
    public JGemsShaderManager weighted_oit;
    public JGemsShaderManager world_particle;
    public JGemsShaderManager weighted_liquid_oit;
    public JGemsShaderManager simple;
    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_sun_fix;
    public JGemsShaderManager depth_plight;
    public JGemsShaderManager debug;
    public JGemsShaderManager world_selected_gbuffer;
    public JGemsShaderManager inventory_zippo;
    public JGemsShaderManager inventory_common_item;
    public JGemsShaderManager imgui;

    protected void initObjects(ResourceCache resourceCache) {
        this.SunLight = this.createUBO("SunLight", 0, 32);
        this.PointLights = this.createUBO("PointLights", 1, 32 * LightManager.MAX_POINT_LIGHTS + 4);
        this.Misc = this.createUBO("Misc", 2, 4);
        this.Fog = this.createUBO("Fog", 3, 16);

        this.world_enemy = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_enemy") , Shader.ShaderType.DEFAULT_BITS);
        this.world_pickable = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_pickable") , Shader.ShaderType.DEFAULT_BITS);
        this.debug = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "debug") , Shader.ShaderType.DEFAULT_BITS);
        this.gui_text = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui_text") , Shader.ShaderType.DEFAULT_BITS);
        this.gui_noised = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui_noised") , Shader.ShaderType.DEFAULT_BITS);

        this.gui_button = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui_button") , Shader.ShaderType.DEFAULT_BITS);
        this.gui_image = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui_image") , Shader.ShaderType.DEFAULT_BITS);
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui_image_selectable") , Shader.ShaderType.DEFAULT_BITS);

        this.blur_ssao = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "blur_ssao") , Shader.ShaderType.DEFAULT_BITS);
        this.blur5 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "blur5") , Shader.ShaderType.DEFAULT_BITS);
        this.blur9 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "blur9") , Shader.ShaderType.DEFAULT_BITS);
        this.blur13 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "blur13") , Shader.ShaderType.DEFAULT_BITS);
        this.blur_box = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "blur_box") , Shader.ShaderType.DEFAULT_BITS);

        this.imgui = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "imgui") , Shader.ShaderType.DEFAULT_BITS);

        this.fxaa = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "fxaa") , Shader.ShaderType.DEFAULT_BITS);
        this.hdr = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "hdr") , Shader.ShaderType.DEFAULT_BITS);

        this.skybox = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "skybox") , Shader.ShaderType.DEFAULT_BITS).attachUBOs(this.SunLight);

        this.world_ssao = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_ssao") , Shader.ShaderType.COMPUTE_BIT);

        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "weighted_liquid_oit") , Shader.ShaderType.DEFAULT_BITS).setShaderRenderPass(RenderPass.TRANSPARENCY);
        this.weighted_oit = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "weighted_oit") , Shader.ShaderType.DEFAULT_BITS).setShaderRenderPass(RenderPass.TRANSPARENCY);

        this.world_particle = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_particle") , Shader.ShaderType.DEFAULT_BITS);

        this.world_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_gbuffer") , Shader.ShaderType.DEFAULT_BITS).setShaderRenderPass(RenderPass.DEFERRED);
        this.world_deferred = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_deferred") , Shader.ShaderType.DEFAULT_BITS).attachUBOs(this.SunLight, this.PointLights, this.Fog);

        this.menu = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "menu") , Shader.ShaderType.DEFAULT_BITS);

        this.inventory_zippo = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "inventory_zippo") , Shader.ShaderType.DEFAULT_BITS);
        this.inventory_common_item = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "inventory_common_item") , Shader.ShaderType.DEFAULT_BITS);

        this.simple = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "simple") , Shader.ShaderType.DEFAULT_BITS);
        this.depth_sun = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "depth_sun") , Shader.ShaderType.DEFAULT_BITS);
        this.depth_sun_fix = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "depth_sun_fix") , Shader.ShaderType.DEFAULT_BITS);

        this.world_selected_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world_selected_gbuffer") , Shader.ShaderType.DEFAULT_BITS);
        this.depth_plight = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "depth_plight") , Shader.ShaderType.DEFAULT_BITS);

        this.gameUbo = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gameubo") , Shader.ShaderType.DEFAULT_BITS).attachUBOs(this.SunLight, this.Misc, this.PointLights, this.Fog);
    }

    @Override
    protected JGemsShaderManager createShaderObject(JGPath shaderPath, int types) {
        return new JGemsShaderManager(new ShaderContainer(shaderPath, types));
    }
}