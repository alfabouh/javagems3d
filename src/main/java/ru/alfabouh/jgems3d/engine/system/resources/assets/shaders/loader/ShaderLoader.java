package ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.loader;

import ru.alfabouh.jgems3d.engine.render.opengl.environment.light.LightManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

import java.util.ArrayList;
import java.util.List;

public class ShaderLoader implements IShaderLoader {
    public static final List<JGemsShaderManager> allShaders = new ArrayList<>();
    public static final List<UniformBufferObject> allUniformBuffers = new ArrayList<>();

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
    public JGemsShaderManager hdr;
    public JGemsShaderManager fxaa;
    public JGemsShaderManager post_psx;
    public JGemsShaderManager menu_psx;
    public JGemsShaderManager skybox;
    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_liquid_gbuffer;
    public JGemsShaderManager world_deferred;
    public JGemsShaderManager simple;
    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_plight;
    public JGemsShaderManager debug;
    public JGemsShaderManager world_selected_gbuffer;
    public JGemsShaderManager inventory_zippo;
    public JGemsShaderManager inventory_common_item;
    public JGemsShaderManager imgui;

    public ShaderLoader() {
        this.init();
    }

    private void init() {
        this.SunLight = this.createUBO("SunLight", 0, 32);
        this.PointLights = this.createUBO("PointLights", 1, 32 * LightManager.MAX_POINT_LIGHTS + 4);
        this.Misc = this.createUBO("Misc", 2, 4);
        this.Fog = this.createUBO("Fog", 3, 16);

        this.world_enemy = this.createShaderManager("jgems/shaders/world_enemy", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_pickable = this.createShaderManager("jgems/shaders/world_pickable", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.debug = this.createShaderManager("jgems/shaders/debug", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_text = this.createShaderManager("jgems/shaders/gui_text", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_noised = this.createShaderManager("jgems/shaders/gui_noised", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.gui_button = this.createShaderManager("jgems/shaders/gui_button", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_image = this.createShaderManager("jgems/shaders/gui_image", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_image_selectable = this.createShaderManager("jgems/shaders/gui_image_selectable", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.blur5 = this.createShaderManager("jgems/shaders/blur5", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.blur9 = this.createShaderManager("jgems/shaders/blur9", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.blur13 = this.createShaderManager("jgems/shaders/blur13", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.imgui = this.createShaderManager("jgems/shaders/imgui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.fxaa = this.createShaderManager("jgems/shaders/fxaa", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.hdr = this.createShaderManager("jgems/shaders/hdr", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_psx = this.createShaderManager("jgems/shaders/post_psx", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);

        this.skybox = this.createShaderManager("jgems/shaders/skybox", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight);

        this.world_gbuffer = this.createShaderManager("jgems/shaders/world_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).setUseForGBuffer(true);
        this.world_deferred = this.createShaderManager("jgems/shaders/world_deferred", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.PointLights).addUBO(this.Fog);
        this.world_liquid_gbuffer = this.createShaderManager("jgems/shaders/world_liquid_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);

        this.menu = this.createShaderManager("jgems/shaders/menu", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.menu_psx = this.createShaderManager("jgems/shaders/menu_psx", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.inventory_zippo = this.createShaderManager("jgems/shaders/inventory_zippo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.inventory_common_item = this.createShaderManager("jgems/shaders/inventory_common_item", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.simple = this.createShaderManager("jgems/shaders/simple", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_sun = this.createShaderManager("jgems/shaders/depth_sun", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_selected_gbuffer = this.createShaderManager("jgems/shaders/world_selected_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_plight = this.createShaderManager("jgems/shaders/depth_plight", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.gameUbo = this.createShaderManager("jgems/shaders/gameubo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights).addUBO(this.Fog);
    }

    public UniformBufferObject createUBO(String id, int binding, int bsize) {
        UniformBufferObject uniformBufferObject = new UniformBufferObject(id, binding, bsize);
        ShaderLoader.allUniformBuffers.add(uniformBufferObject);
        return uniformBufferObject;
    }

    public JGemsShaderManager createShaderManager(String shader, int types) {
        SystemLogging.get().getLogManager().log("Creating shader " + shader);
        JGemsShaderManager shaderManager = new JGemsShaderManager(new ShaderGroup(shader, types));
        ShaderLoader.allShaders.add(shaderManager);
        return shaderManager;
    }

    public void startShaders() {
        SystemLogging.get().getLogManager().log("Compiling shaders!");
        for (JGemsShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.startProgram();
        }
    }

    public void destroyShaders() {
        SystemLogging.get().getLogManager().log("Destroying shaders!");
        for (JGemsShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.destroyProgram();
        }
    }

    public void loadShaders() {
        for (JGemsShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.getShaderGroup().initAll();
        }
    }

    public void reloadShaders() {
        this.destroyShaders();
        this.loadShaders();
        this.startShaders();
    }
}
