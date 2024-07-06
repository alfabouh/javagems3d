package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base;

import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.alfabouh.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public abstract class ShadersLoader {
    public UniformBufferObject createUBO(String id, int binding, int bSize) {
        return new UniformBufferObject(id, binding, bSize);
    }

    protected abstract void initObjects(ResourceCache resourceCache);
    protected abstract ShaderManager createShaderObject(String shader, int types);

    @SuppressWarnings("unchecked")
    public <T extends ShaderManager> T createShaderManager(ResourceCache resourceCache, String shader, int types) {
        if (resourceCache.checkObjectInCache(shader)) {
            SystemLogging.get().getLogManager().warn("Shader " + shader + " already exists!");
            return (T) resourceCache.getCachedObject(shader);
        }
        SystemLogging.get().getLogManager().log("Creating shader " + shader + "...");
        T shaderManager = (T) this.createShaderObject(shader, types);
        resourceCache.addObjectInBuffer(shader, shaderManager);
        return shaderManager;
    }

    public void startShaders(ResourceCache resourceCache) {
        SystemLogging.get().getLogManager().log("Compiling shaders!");
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.startProgram();
        }
    }

    public void destroyShaders(ResourceCache resourceCache) {
        SystemLogging.get().getLogManager().log("Destroying shaders!");
        resourceCache.cleanGroupInCache(ShaderManager.class);
    }

    public void initShaders(ResourceCache resourceCache) {
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.getShaderGroup().initAll();
        }
    }

    public void createShaders(ResourceCache resourceCache) {
        this.initObjects(resourceCache);
        this.initShaders(resourceCache);
        this.startShaders(resourceCache);
    }

    public void reloadShaders(ResourceCache resourceCache) {
        this.destroyShaders(resourceCache);
        this.createShaders(resourceCache);
    }
}
