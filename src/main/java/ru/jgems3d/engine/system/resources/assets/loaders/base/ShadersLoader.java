package ru.jgems3d.engine.system.resources.assets.loaders.base;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.file.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

public abstract class ShadersLoader <T extends ShaderManager> {
    protected abstract void initObjects(ResourceCache resourceCache);
    protected abstract T createShaderObject(JGemsPath shaderPath);

    @SuppressWarnings("unchecked")
    public T createShaderManager(ResourceCache resourceCache, JGemsPath shaderPath) {
        if (resourceCache.checkObjectInCache(shaderPath)) {
            JGemsHelper.getLogger().warn("Shader " + shaderPath + " already exists!");
            return (T) resourceCache.getCachedObject(shaderPath);
        }
        JGemsHelper.getLogger().log("Creating shader " + shaderPath + "...");
        T shaderManager = (T) this.createShaderObject(shaderPath);
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
