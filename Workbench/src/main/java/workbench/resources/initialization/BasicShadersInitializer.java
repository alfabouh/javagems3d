package workbench.resources.initialization;

import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import workbench.resources.shaders.WBenchShaderManager;

public final class BasicShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager imgui;

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
      //  shaderStaticConstants.putConstant("MAX_BINDLESS_TEXTURES", String.valueOf(JGemsGlobalConfiguration.MAX_BINDLESS_TEXTURES));
      //  shaderStaticConstants.putConstant("MAX_INDIRECT_RENDERING_MESH_DATASETS", String.valueOf(JGemsGlobalConfiguration.MAX_INDIRECT_RENDERING_MESH_DATASETS));
      //  shaderStaticConstants.putConstant("ANIM_MAX_WEIGHTS", String.valueOf(JGemsGlobalConfiguration.ANIM_MAX_WEIGHTS));
      //  shaderStaticConstants.putConstant("MAX_POINT_LIGHTS", String.valueOf(JGemsGlobalConfiguration.MAX_POINT_LIGHTS));
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
        shaderLibrary.initLibrary(new JGemsPath("/assets/jgems/shaders/libs/shadows"));
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.imgui = this.createShaderManager(resourceCache, new JGemsPath("/assets/wbench/shaders/imgui"));
    }

    @Override
    protected WBenchShaderManager createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        return new WBenchShaderManager(new ShadersContainer(shaderStaticConstants, shaderLibrary, shaderPath));
    }
}