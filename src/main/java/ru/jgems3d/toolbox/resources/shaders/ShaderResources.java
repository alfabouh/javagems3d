package ru.jgems3d.toolbox.resources.shaders;

import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
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
        this.world_isometric_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_isometric_object"));
        this.world_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_object"));
        this.world_lines = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_lines"));
        this.world_xyz = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_xyz"));
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/imgui"));
    }

    @Override
    public TBoxShaderManager createShaderObject(JGemsPath shaderPath) {
        return new TBoxShaderManager(new ShaderContainer(shaderPath));
    }
}