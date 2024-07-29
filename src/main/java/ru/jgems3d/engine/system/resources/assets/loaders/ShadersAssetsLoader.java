package ru.jgems3d.engine.system.resources.assets.loaders;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.graphics.opengl.environment.light.LightManager;
import ru.jgems3d.engine.system.files.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
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
    public JGemsShaderManager hdr;
    public JGemsShaderManager fxaa;
    public JGemsShaderManager skybox;
    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_ssao;
    public JGemsShaderManager world_liquid_gbuffer;
    public JGemsShaderManager world_deferred;
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

        this.world_enemy = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_enemy") , Shader.ShaderType.DEFAULT);
        this.world_pickable = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_pickable") , Shader.ShaderType.DEFAULT);
        this.debug = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "debug") , Shader.ShaderType.DEFAULT);
        this.gui_text = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gui_text") , Shader.ShaderType.DEFAULT);
        this.gui_noised = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gui_noised") , Shader.ShaderType.DEFAULT);

        this.gui_button = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gui_button") , Shader.ShaderType.DEFAULT);
        this.gui_image = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gui_image") , Shader.ShaderType.DEFAULT);
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gui_image_selectable") , Shader.ShaderType.DEFAULT);

        this.blur5 = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "blur5") , Shader.ShaderType.DEFAULT);
        this.blur9 = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "blur9") , Shader.ShaderType.DEFAULT);
        this.blur13 = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "blur13") , Shader.ShaderType.DEFAULT);
        this.blur_box = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "blur_box") , Shader.ShaderType.DEFAULT);

        this.imgui = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "imgui") , Shader.ShaderType.DEFAULT);

        this.fxaa = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "fxaa") , Shader.ShaderType.DEFAULT);
        this.hdr = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "hdr") , Shader.ShaderType.DEFAULT);

        this.skybox = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "skybox") , Shader.ShaderType.DEFAULT).attachUBOs(this.SunLight);

        this.world_ssao = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_ssao") , Shader.ShaderType.COMPUTE_BIT);

        this.world_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_gbuffer") , Shader.ShaderType.DEFAULT).setUseForGBuffer(true);
        this.world_deferred = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_deferred") , Shader.ShaderType.DEFAULT).attachUBOs(this.SunLight, this.PointLights, this.Fog);
        this.world_liquid_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_liquid_gbuffer") , Shader.ShaderType.DEFAULT).attachUBOs(this.Misc);

        this.menu = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "menu") , Shader.ShaderType.DEFAULT);

        this.inventory_zippo = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "inventory_zippo") , Shader.ShaderType.DEFAULT);
        this.inventory_common_item = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "inventory_common_item") , Shader.ShaderType.DEFAULT);

        this.simple = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "simple") , Shader.ShaderType.DEFAULT);
        this.depth_sun = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "depth_sun") , Shader.ShaderType.DEFAULT);
        this.depth_sun_fix = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "depth_sun_fix") , Shader.ShaderType.DEFAULT);

        this.world_selected_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "world_selected_gbuffer") , Shader.ShaderType.DEFAULT);
        this.depth_plight = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "depth_plight") , Shader.ShaderType.DEFAULT);

        this.gameUbo = this.createShaderManager(resourceCache, new JGPath(JGems.Paths.SHADERS, "gameubo") , Shader.ShaderType.DEFAULT).attachUBOs(this.SunLight, this.Misc, this.PointLights, this.Fog);
    }

    @Override
    protected JGemsShaderManager createShaderObject(JGPath shaderPath, int types) {
        return new JGemsShaderManager(new ShaderContainer(shaderPath, types));
    }
}