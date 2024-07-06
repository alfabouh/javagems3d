package ru.alfabouh.jgems3d.toolbox.resources.shaders;

import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.ShaderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import java.util.ArrayList;
import java.util.List;

public class ShaderResources extends ShadersLoader {
    public TBoxShaderManager world_isometric_object;
    public TBoxShaderManager world_lines;
    public TBoxShaderManager world_xyz;
    public TBoxShaderManager world_object;
    public TBoxShaderManager world_marker;
    public TBoxShaderManager imgui;

    protected void initObjects(ResourceCache resourceCache) {
        this.world_isometric_object = this.createShaderManager(resourceCache, "toolbox/shaders/world_isometric_object", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_object = this.createShaderManager(resourceCache, "toolbox/shaders/world_object", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_lines = this.createShaderManager(resourceCache, "toolbox/shaders/world_lines", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_xyz = this.createShaderManager(resourceCache, "toolbox/shaders/world_xyz", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_marker = this.createShaderManager(resourceCache, "toolbox/shaders/world_marker", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.imgui = this.createShaderManager(resourceCache, "toolbox/shaders/imgui", Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
    }

    @Override
    public ShaderManager createShaderObject(String shader, int types) {
        return new TBoxShaderManager(new ShaderGroup(shader, types));
    }
}