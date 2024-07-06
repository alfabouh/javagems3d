package ru.alfabouh.jgems3d.toolbox.resources;

import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ICached;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.ToolBox;
import ru.alfabouh.jgems3d.toolbox.resources.models.ModelResources;
import ru.alfabouh.jgems3d.toolbox.resources.samples.TextureSample;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.ShaderResources;
import ru.alfabouh.jgems3d.toolbox.resources.utils.SimpleModelLoader;

public class TBoxResourceManager {
    public static ShaderResources shaderAssets;
    private final ModelResources modelResources;
    private final ResourceCache resourceCache;

    public TBoxResourceManager() {
        this.resourceCache = new ResourceCache("Global");
        TBoxResourceManager.shaderAssets = new ShaderResources();
        this.modelResources = new ModelResources();
    }

    public static TextureSample createTextureInJar(String fullPath) {
        return TextureSample.createTexture(true, ToolBox.get().getScreen().getResourceManager().getCache(), fullPath);
    }

    public static TextureSample createTextureOutSideJar(String fullPath) {
        return TextureSample.createTexture(false, ToolBox.get().getScreen().getResourceManager().getCache(), fullPath);
    }

    public static MeshDataGroup createModel(String modelPath) {
        return SimpleModelLoader.createMesh(ToolBox.get().getScreen().getResourceManager().getCache(), modelPath);
    }

    public static ICached getResource(String key) {
        return ToolBox.get().getResourceManager().getCache().getCachedObject(key);
    }

    public static void createShaders() {
        TBoxResourceManager.shaderAssets.createShaders(ToolBox.get().getResourceManager().getCache());
    }

    public void loadResources() {
        SystemLogging.get().getLogManager().log("Loading resources...");
        this.getModelResources().init();
    }

    public void destroy() {
        this.getCache().cleanCache();
    }

    public ModelResources getModelResources() {
        return this.modelResources;
    }

    public ResourceCache getCache() {
        return this.resourceCache;
    }
}
