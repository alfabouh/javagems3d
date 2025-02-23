/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package toolbox.resources;

import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.SystemLogging;
import toolbox.ToolBox;
import toolbox.resources.models.ModelResources;
import toolbox.resources.samples.ImageTexture;
import toolbox.resources.shaders.ShaderResources;
import toolbox.resources.shaders.manager.TBoxShaderManager;
import toolbox.resources.utils.SimpleModelLoader;

public class TBoxResourceManager {
    private final ShaderResources shaderAssets;
    private final ModelResources modelResources;
    private final ResourceCache resourceCache;

    public TBoxResourceManager() {
        this.resourceCache = new ResourceCache("Global");
        this.shaderAssets = new ShaderResources();
        this.modelResources = new ModelResources();
    }

    public static void createShaders() {
        TBoxResourceManager.shaderResources().createShaders(ToolBox.get().getResourceManager().getCache());
    }

    public static ShaderResources shaderResources() {
        return ToolBox.get().getResourceManager().getShaderAssets();
    }

    public TBoxShaderManager createShaderManager(JGemsPath shaderPath) {
        if (ToolBox.get().getResourceManager().getCache().checkObjectInCache(shaderPath)) {
            Log.get().warn("Shader " + shaderPath + " already exists");
            return (TBoxShaderManager) this.getCache().getCachedObject(shaderPath);
        }
        Log.get().trace("Creating shader " + shaderPath + "...");
        TBoxShaderManager shaderManager = new TBoxShaderManager(new ShadersContainer(shaderPath));
        this.getCache().addObjectInBuffer(shaderPath, shaderManager);
        return shaderManager;
    }

    public ImageTexture createTexture(String fullPath) {
        return ImageTexture.createTexture(this.getCache(), fullPath);
    }

    public MeshGroup createModel(JGemsPath modelPath) {
        return SimpleModelLoader.createMesh(this, modelPath);
    }

    public ICached getResource(String key) {
        return this.getCache().getCachedObject(key);
    }

    public void loadResources() {
        SystemLogging.get().getLogManager().trace("Loading resources...");
        this.getModelResources().init(this);
    }

    public void destroy() {
        this.getCache().clearCache();
    }

    public ShaderResources getShaderAssets() {
        return this.shaderAssets;
    }

    public ModelResources getModelResources() {
        return this.modelResources;
    }

    public ResourceCache getCache() {
        return this.resourceCache;
    }
}
