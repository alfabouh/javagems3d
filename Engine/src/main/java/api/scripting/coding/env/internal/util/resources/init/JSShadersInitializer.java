package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSResourcesManager;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

@JSCodingClass(binding = "JSAssetsInitializer", description = "...")
public abstract class JSShadersInitializer extends ShadersInitializer<JGemsShaderManager> {
    @JSCodingFunctionOrMethod(description = "...") public abstract void registerShaderLibraries(JSResourcesManager resourcesManager);
    @JSCodingFunctionOrMethod(description = "...") public abstract void registerShaderConstants(JSResourcesManager resourcesManager);
    @JSCodingFunctionOrMethod(description = "...") public abstract void run(JSResourcesManager resourcesManager);


    @JSHideFromDoc
    @Override
    protected void initStaticConstants(ShaderStaticConstants shaderStaticConstants) {

    }

    @JSHideFromDoc
    @Override
    protected void initShaderLibraries(ShaderLibrariesManager shaderLibrary) {

    }

    @JSHideFromDoc
    protected void initObjects(ResourceCache resourceCache) {

    }

    @JSHideFromDoc
    @Override
    protected JGemsShaderManager createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary) {
        return new JGemsShaderManager(new ShadersContainer(shaderPath, shaderStaticConstants, shaderLibrary));
    }
}
