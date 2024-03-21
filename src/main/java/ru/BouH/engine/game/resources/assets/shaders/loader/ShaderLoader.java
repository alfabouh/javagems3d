package ru.BouH.engine.game.resources.assets.shaders.loader;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.assets.shaders.Shader;
import ru.BouH.engine.game.resources.assets.shaders.ShaderGroup;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.game.resources.assets.shaders.UniformBufferObject;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.render.environment.light.LightManager;

import java.util.ArrayList;
import java.util.List;

public class ShaderLoader {
    public static final List<ShaderManager> allShaders = new ArrayList<>();
    public static final List<UniformBufferObject> allUniformBuffers = new ArrayList<>();

    public final UniformBufferObject SunLight;
    public final UniformBufferObject PointLights;
    public final UniformBufferObject Misc;
    public final UniformBufferObject Fog;

    public final ShaderManager gameUbo;
    public final ShaderManager gui_text;
    public final ShaderManager gui_image;
    public final ShaderManager gui_button;
    public final ShaderManager post_blur;
    public final ShaderManager post_render_1;
    public final ShaderManager skybox;
    public final ShaderManager world;
    public final ShaderManager simple;
    public final ShaderManager depth_sun;
    public final ShaderManager liquid;
    public final ShaderManager depth_plight;
    public final ShaderManager debug;
    public final ShaderManager world_selected;
    public final ShaderManager inventory_zippo;

    public ShaderLoader() {
        this.SunLight = this.createUBO("SunLight", 0, 32);
        this.PointLights = this.createUBO("PointLights", 1, 32 * LightManager.MAX_POINT_LIGHTS);
        this.Misc = this.createUBO("Misc", 2, 4);
        this.Fog = this.createUBO("Fog", 3, 16);

        this.debug = this.createShaderManager("debug", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_text = this.createShaderManager("gui_text", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_button = this.createShaderManager("gui_button", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.gui_image = this.createShaderManager("gui_image", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_blur = this.createShaderManager("post_blur", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.post_render_1 = this.createShaderManager("post_render_1", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.Misc);
        this.skybox = this.createShaderManager("skybox", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight);
        this.world = this.createShaderManager("world", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights).addUBO(this.Fog);
        this.liquid = this.createShaderManager("liquid", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT).addUBO(this.SunLight).addUBO(this.Misc).addUBO(this.PointLights).addUBO(this.Fog);

        this.inventory_zippo = this.createShaderManager("inventory_zippo", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.simple = this.createShaderManager("simple", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_sun = this.createShaderManager("depth_sun", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_selected = this.createShaderManager("world_selected", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.depth_plight = this.createShaderManager("depth_plight", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT | Shader.ShaderType.GEOMETRIC_BIT);

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

    public void loadAllShaders() {
        for (ShaderManager shaderManager : ShaderLoader.allShaders) {
            if (shaderManager.getShaderGroup().getFragmentShader() != null) {
                shaderManager.getShaderGroup().initAll();
            }
        }
    }
}
