package ru.alfabouh.jgems3d.toolbox.resources.shaders;

import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.loader.IShaderLoader;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import java.util.ArrayList;
import java.util.List;

public class ShaderLoader implements IShaderLoader {
    public static final List<TBoxShaderManager> allShaders = new ArrayList<>();
    public static final List<UniformBufferObject> allUniformBuffers = new ArrayList<>();


    public TBoxShaderManager world_object;
    public TBoxShaderManager imgui;

    public ShaderLoader() {
        this.init();
    }

    private void init() {
        this.world_object = this.createShaderManager("toolbox/world_object", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.imgui = this.createShaderManager("toolbox/imgui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
    }

    public UniformBufferObject createUBO(String id, int binding, int bsize) {
        UniformBufferObject uniformBufferObject = new UniformBufferObject(id, binding, bsize);
        ShaderLoader.allUniformBuffers.add(uniformBufferObject);
        return uniformBufferObject;
    }

    public TBoxShaderManager createShaderManager(String shader, int types) {
        SystemLogging.get().getLogManager().log("Creating shader " + shader);
        TBoxShaderManager shaderManager = new TBoxShaderManager(new ShaderGroup(shader, types));
        ShaderLoader.allShaders.add(shaderManager);
        return shaderManager;
    }

    public void startShaders() {
        SystemLogging.get().getLogManager().log("Compiling shaders!");
        for (TBoxShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.startProgram();
        }
    }

    public void destroyShaders() {
        SystemLogging.get().getLogManager().log("Destroying shaders!");
        for (TBoxShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.destroyProgram();
        }
    }

    public void loadShaders() {
        for (TBoxShaderManager shaderManager : ShaderLoader.allShaders) {
            shaderManager.getShaderGroup().initAll();
        }
    }

    public void reloadShaders() {
        this.destroyShaders();
        this.loadShaders();
        this.startShaders();
    }
}
