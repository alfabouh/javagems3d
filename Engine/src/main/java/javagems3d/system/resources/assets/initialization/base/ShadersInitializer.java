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

package javagems3d.system.resources.assets.initialization.base;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;

public abstract class ShadersInitializer<T extends ShaderManager> {
    private final ShaderLibrariesManager shaderLibrary;
    private final ShaderStaticConstants shaderStaticConstants;

    public ShadersInitializer() {
        this.shaderLibrary = new ShaderLibrariesManager();
        this.shaderStaticConstants = new ShaderStaticConstants();
    }

    protected abstract void initObjects(ResourceCache resourceCache);
    protected abstract T createShaderObject(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath);
    protected abstract void initStaticConstants(ShaderStaticConstants shaderStaticConstants);
    protected abstract void initShaderLibraries(ShaderLibrariesManager shaderLibrary);

    @SuppressWarnings("unchecked")
    public T createShaderManager(ResourceCache resourceCache, JGemsPath shaderPath) {
        if (resourceCache.checkObjectInCache(shaderPath)) {
            JGemsHelper.getLogger().warn("Shader " + shaderPath + " already exists");
            return (T) resourceCache.getCachedObject(shaderPath);
        }
        JGemsHelper.getLogger().info("Creating shader " + shaderPath + "...");
        T shaderManager = this.createShaderObject(this.getShaderStaticConstants(), this.getShaderLibrariesManager(), shaderPath);
        resourceCache.addObjectInBuffer(shaderPath, shaderManager);
        return shaderManager;
    }

    public void startShaders(ResourceCache resourceCache) {
        JGemsHelper.getLogger().info("Compiling shaders");
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.startProgram();
        }
    }

    public UniformBufferObject createUBO(String id, int binding, int bSize) {
        return new UniformBufferObject(id, binding, bSize);
    }

    public void clearShaders(ResourceCache resourceCache) {
        resourceCache.clearGroupInCache(ShaderManager.class);
    }

    public void destroyShaderPrograms(ResourceCache resourceCache) {
        JGemsHelper.getLogger().info("Destroying shaders");
        resourceCache.getAllCachedObjectsCollection(ShaderManager.class).forEach(ShaderManager::destroyProgram);
    }

    public void initShaders(ResourceCache resourceCache) {
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.getShadersContainer().initAll();
        }
    }

    public ShaderStaticConstants getShaderStaticConstants() {
        return this.shaderStaticConstants;
    }

    public ShaderLibrariesManager getShaderLibrariesManager() {
        return this.shaderLibrary;
    }

    public void createShaders(ResourceCache resourceCache) {
        this.getShaderLibrariesManager().clear();
        this.initConstants();
        this.initShaderLibraries(this.getShaderLibrariesManager());
        this.initObjects(resourceCache);
        this.initShaders(resourceCache);
        this.startShaders(resourceCache);
    }

    public void reloadShaders(ResourceCache resourceCache) {
        this.getShaderLibrariesManager().reload();
        this.initConstants();
        this.initShaderLibraries(this.getShaderLibrariesManager());
        this.destroyShaderPrograms(resourceCache);
        this.initShaders(resourceCache);
        this.startShaders(resourceCache);
    }

    private void initConstants() {
        this.getShaderStaticConstants().clear();
        this.initStaticConstants(this.getShaderStaticConstants());
        JGemsHelper.getLogger().trace("Initialized " + this.getShaderStaticConstants().getCnstMap().size() + " shader static constants");
    }
}
