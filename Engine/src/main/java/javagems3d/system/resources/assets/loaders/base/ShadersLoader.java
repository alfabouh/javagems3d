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

package javagems3d.system.resources.assets.loaders.base;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;

public abstract class ShadersLoader<T extends ShaderManager> {
    protected abstract void initObjects(ResourceCache resourceCache);

    protected abstract T createShaderObject(JGemsPath shaderPath);

    @SuppressWarnings("unchecked")
    public T createShaderManager(ResourceCache resourceCache, JGemsPath shaderPath) {
        if (resourceCache.checkObjectInCache(shaderPath)) {
            JGemsHelper.getLogger().warn("Shader " + shaderPath + " already exists!");
            return (T) resourceCache.getCachedObject(shaderPath);
        }
        JGemsHelper.getLogger().log("Creating shader " + shaderPath + "...");
        T shaderManager = this.createShaderObject(shaderPath);
        resourceCache.addObjectInBuffer(shaderPath, shaderManager);
        return shaderManager;
    }

    public void startShaders(ResourceCache resourceCache) {
        JGemsHelper.getLogger().log("Compiling shaders!");
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.startProgram();
        }
    }

    public UniformBufferObject createUBO(String id, int binding, int bSize) {
        return new UniformBufferObject(id, binding, bSize);
    }

    public void cleanShaders(ResourceCache resourceCache) {
        resourceCache.cleanGroupInCache(ShaderManager.class);
    }

    public void destroyShaderPrograms(ResourceCache resourceCache) {
        JGemsHelper.getLogger().log("Destroying shaders!");
        resourceCache.getAllCachedObjectsCollection(ShaderManager.class).forEach(ShaderManager::destroyProgram);
    }

    public void initShaders(ResourceCache resourceCache) {
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.getShaderContainer().initAll();
        }
    }

    public void createShaders(ResourceCache resourceCache) {
        this.initObjects(resourceCache);
        this.initShaders(resourceCache);
        this.startShaders(resourceCache);
    }

    public void reloadShaders(ResourceCache resourceCache) {
        this.destroyShaderPrograms(resourceCache);
        this.initShaders(resourceCache);
        this.startShaders(resourceCache);
    }
}
