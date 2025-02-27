package toolbox.resources.shaders;

import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import toolbox.resources.shaders.manager.TBoxShaderManager;

public final class ShaderResources extends ShadersInitializer<TBoxShaderManager> {
    public TBoxShaderManager world_transparent_color;
    public TBoxShaderManager world_isometric_object;
    public TBoxShaderManager world_lines;
    public TBoxShaderManager world_xyz;
    public TBoxShaderManager world_object;
    public TBoxShaderManager world_object_nolight;
    public TBoxShaderManager imgui;
    public TBoxShaderManager scene_gluing;

    protected void initObjects(ResourceCache resourceCache) {
        this.world_transparent_color = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_transparent_color"));
        this.world_isometric_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_isometric_object"));
        this.world_object = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_object"));
        this.world_object_nolight = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_object_nolight"));
        this.world_lines = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_lines"));
        this.world_xyz = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/world_xyz"));
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/imgui"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGemsPath("/assets/toolbox/shaders/scene_gluing"));
    }

    @Override
    protected TBoxShaderManager createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        return new TBoxShaderManager(new ShadersContainer(null, null, shaderPath));
    }

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {

    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {

    }
}