package ru.jgems3d.toolbox.resources.shaders;

import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.shaders.Shader;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public class ShaderResources extends ShadersLoader<TBoxShaderManager> {
    public TBoxShaderManager world_isometric_object;
    public TBoxShaderManager world_lines;
    public TBoxShaderManager world_xyz;
    public TBoxShaderManager world_object;
    public TBoxShaderManager imgui;

    protected void initObjects(ResourceCache resourceCache) {
        this.world_isometric_object = this.createShaderManager(resourceCache, new JGPath("/assets/toolbox/shaders/world_isometric_object"), Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_object = this.createShaderManager(resourceCache, new JGPath("/assets/toolbox/shaders/world_object"), Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_lines = this.createShaderManager(resourceCache, new JGPath("/assets/toolbox/shaders/world_lines"), Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.world_xyz = this.createShaderManager(resourceCache, new JGPath("/assets/toolbox/shaders/world_xyz"), Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
        this.imgui = this.createShaderManager(resourceCache, new JGPath("/assets/toolbox/shaders/imgui"), Shader.ShaderType.FRAGMENT_BIT | Shader.ShaderType.VERTEX_BIT);
    }

    @Override
    public TBoxShaderManager createShaderObject(JGPath shaderPath, int types) {
        return new TBoxShaderManager(new ShaderContainer(shaderPath, types));
    }
}