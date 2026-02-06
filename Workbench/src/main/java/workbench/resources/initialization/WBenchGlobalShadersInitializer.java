package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import org.jetbrains.annotations.NotNull;
import workbench.resources.shaders.WBenchShaderManager;

public final class WBenchGlobalShadersInitializer extends ShadersInitializer<WBenchShaderManager> {
    public WBenchShaderManager imgui;
    public WBenchShaderManager debug;

    public WBenchGlobalShadersInitializer() {
    }

    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {
    }

    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {
    }

    protected void initObjects(ResourceCache resourceCache) {
        this.imgui = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "gui/imgui"), ISource.Source.INSIDE_JAR));
        this.debug = this.createShaderManager(resourceCache, new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SHADERS, "debug"), ISource.Source.INSIDE_JAR));
    }

    @Override
    protected WBenchShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
        return new WBenchShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
    }
}