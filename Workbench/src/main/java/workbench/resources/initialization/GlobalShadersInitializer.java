package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import workbench.resources.shaders.WBenchShaderManager;

public final class GlobalShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager imgui;
    public WBenchShaderManager debug;

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/imgui"));
        this.debug = this.createShaderManager(resourceCache, new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "debug"));
    }

    @Override
    protected WBenchShaderManager createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        return new WBenchShaderManager(new ShadersContainer(shaderStaticConstants, shaderLibrary, shaderPath));
    }
}