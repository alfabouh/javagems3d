/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.resources.assets.initialization.base;

import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.manager.ShaderManager;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import javagems3d.system.service.files.source.JGemsStringSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public abstract class ShadersInitializer<T extends ShaderManager> {
    private final ShaderLibrariesManager shaderLibrary;
    private final ShaderStaticConstants shaderStaticConstants;

    public ShadersInitializer() {
        this.shaderLibrary = new ShaderLibrariesManager();
        this.shaderStaticConstants = new ShaderStaticConstants();
    }

    protected abstract void initObjects(ResourceCache resourceCache);
    protected abstract T createShaderObject(@NotNull JGemsPathSource shaderPath, ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary);
    protected abstract void initStaticConstants(ShaderStaticConstants shaderStaticConstants);
    protected abstract void initShaderLibraries(ShaderLibrariesManager shaderLibrary);

    @SuppressWarnings("unchecked")
    public T createShaderManager(ResourceCache resourceCache, JGemsPathSource shaderPath) {
        if (resourceCache.checkObjectInCache(shaderPath, ShadersInitializer.class)) {
            Log.get().warn("Shader " + shaderPath + " already exists");
            return (T) resourceCache.getCachedObject(shaderPath);
        }
        Log.get().info("Creating shader " + shaderPath + "...");
        T shaderManager = this.createShaderObject(shaderPath, this.getShaderStaticConstants(), this.getShaderLibrariesManager());
        resourceCache.registerInCache(shaderPath, shaderManager);
        return shaderManager;
    }

    public void startShaders(ResourceCache resourceCache) {
        Log.get().info("Compiling shaders");
        for (ShaderManager shaderManager : resourceCache.getAllCachedObjectsCollection(ShaderManager.class)) {
            shaderManager.startProgram();
        }
    }

    public UniformBufferObject createUBO(String id, int binding, int bSize) {
        return new UniformBufferObject(id, binding, bSize);
    }

    public void clearShaders(ResourceCache resourceCache) {
        resourceCache.clearClassTypesInCache(ShaderManager.class);
    }

    public void destroyShaderPrograms(ResourceCache resourceCache) {
        Log.get().info("Destroying shaders");
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
        Log.get().trace("Initialized " + this.getShaderStaticConstants().getCnstMap().size() + " shader static constants");
    }
}
