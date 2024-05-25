package ru.alfabouh.engine.game.resources.assets.shaders.loader;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.assets.shaders.Shader;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.game.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.engine.render.environment.light.LightManager;

import java.util.ArrayList;
import java.util.List;

public class ShaderLoader {
    public static final List<ShaderManager> allShaders = new ArrayList<>();
    public static final List<UniformBufferObject> allUniformBuffers = new ArrayList<>();

    public UniformBufferObject SunLight;
    public UniformBufferObject PointLights;
    public UniformBufferObject Misc;
    public UniformBufferObject Fog;

    public ShaderManager world_enemy;
    public ShaderManager world_pickable;
    public ShaderManager menu;
    public ShaderManager gameUbo;
    public ShaderManager gui_text;
    public ShaderManager gui_noised;
    public ShaderManager gui_image;
    public ShaderManager gui_button;
    public ShaderManager gui_image_selectable;
    public ShaderManager blur5;
    public ShaderManager blur9;
    public ShaderManager blur13;
    public ShaderManager hdr;
    public ShaderManager fxaa;
    public ShaderManager post_psx;
    public ShaderManager menu_psx;
    public ShaderManager skybox;
    public ShaderManager world_gbuffer;
    public ShaderManager world_liquid_gbuffer;
    public ShaderManager world_deferred;
    public ShaderManager simple;
    public ShaderManager depth_sun;
    public ShaderManager depth_plight;
    public ShaderManager debug;
    public ShaderManager world_selected_gbuffer;
    public ShaderManager inventory_zippo;
    public ShaderManager inventory_common_item;
    public ShaderManager imgui;

    public ShaderLoader() {
        this.init();
    }

    private void init() {
        this.SunLight = this.createUBO("SunLight", 0, 32);
        this.PointLights = this.createUBO("PointLights", 1, 32 * LightManager.MAX_POINT_LIGHTS + 4);
        this.Misc = this.createUBO("Misc", 2, 4);
        this.Fog = this.createUBO("Fog", 3, 16);

        this.world_enemy = this.createShaderManager("world_enemy", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_pickable = this.createShaderManager("world_pickable", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.debug = this.createShaderManager("debug", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_text = this.createShaderManager("gui_text", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_noised = this.createShaderManager("gui_noised", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.gui_button = this.createShaderManager("gui_button", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_image = this.createShaderManager("gui_image", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_image_selectable = this.createShaderManager("gui_image_selectable", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.blur5 = this.createShaderManager("blur5", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.blur9 = this.createShaderManager("blur9", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.blur13 = this.createShaderManager("blur13", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.imgui = this.createShaderManager("imgui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.fxaa = this.createShaderManager("fxaa", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.hdr = this.createShaderManager("hdr", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_psx = this.createShaderManager("post_psx", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);

        this.skybox = this.createShaderManager("skybox", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight);

        this.world_gbuffer = this.createShaderManager("world_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).setUseForGBuffer(true);
        this.world_deferred = this.createShaderManager("world_deferred", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.PointLights).addUBO(this.Fog);
        this.world_liquid_gbuffer = this.createShaderManager("world_liquid_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);

        this.menu = this.createShaderManager("menu", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.menu_psx = this.createShaderManager("menu_psx", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.inventory_zippo = this.createShaderManager("inventory_zippo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.inventory_common_item = this.createShaderManager("inventory_common_item", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.simple = this.createShaderManager("simple", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_sun = this.createShaderManager("depth_sun", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_selected_gbuffer = this.createShaderManager("world_selected_gbuffer", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_plight = this.createShaderManager("depth_plight", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);

        this.gameUbo = this.createShaderManager("gameubo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights).addUBO(this.Fog);
    }

    public UniformBufferObject createUBO(String id, int binding, int bsize) {
        UniformBufferObject uniformBufferObject = new UniformBufferObject(id, binding, bsize);
        ShaderLoader.allUniformBuffers.add(uniformBufferObject);
        return uniformBufferObject;
    }

    public ShaderManager createShaderManager(String shader, int types) {
        Game.getGame().getLogManager().log("Creating shader " + shader);
        ShaderManager shaderManager = new ShaderManager(new ShaderGroup(shader, types));
        ShaderLoader.allShaders.add(shaderManager);
        return shaderManager;
    }

    public void startShaders() {
        Game.getGame().getLogManager().log("Compiling shaders!");
        for (ShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.startProgram();
        }
    }

    public void destroyShaders() {
        Game.getGame().getLogManager().log("Destroying shaders!");
        for (ShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.destroyProgram();
        }
    }

    public void loadShaders() {
        for (ShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.getShaderGroup().initAll();
        }
    }

    public void reloadShaders() {
        this.destroyShaders();
        this.loadShaders();
        this.startShaders();
    }
}
