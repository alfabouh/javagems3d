/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 */

package ru.jgems3d.engine.system.map.loaders.tbox.placers;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;

/**
 * This class represents the rendering information for an object inside the engine itself
 */
public final class TDefaultRenderContainer {
    private final MeshRenderAttributes meshRenderAttributes;
    private final JGemsPath pathToRenderModel;
    private final JGemsPath pathToRenderShader;
    private final Class<? extends AbstractSceneEntity> sceneEntityClass;
    private final IRenderObjectFabric renderFabric;

    public TDefaultRenderContainer(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @NotNull JGemsPath pathToRenderShader, @NotNull JGemsPath pathToRenderModel, @NotNull MeshRenderAttributes meshRenderAttributes) {
        this.renderFabric = renderFabric;
        this.sceneEntityClass = sceneEntityClass;
        this.pathToRenderShader = pathToRenderShader;
        this.pathToRenderModel = pathToRenderModel;
        this.meshRenderAttributes = meshRenderAttributes;
    }

    /**
     * Path to object's JGems shader
     */
    public @NotNull JGemsPath getPathToJGemsShader() {
        return this.pathToRenderShader;
    }

    /**
     * These are the attributes that affect the rendering of the mesh in the JGems pipeline
     */
    public @NotNull MeshRenderAttributes getMeshRenderAttributes() {
        return this.meshRenderAttributes;
    }

    /**
     * Path to model
     */
    public @NotNull JGemsPath getPathToRenderModel() {
        return this.pathToRenderModel;
    }

    /**
     * A render factory is a special class that is responsible for rendering a specific IRenderObject object
     * @see ru.jgems3d.engine.graphics.opengl.rendering.items.IRenderObject
     */
    public @NotNull IRenderObjectFabric getRenderFabric() {
        return this.renderFabric;
    }

    /**
     * This class represents an object in the world of the scene. It is connected to an object from the physical world. It can be null if you don't need this object.
     */
    public @NotNull Class<? extends AbstractSceneEntity> getSceneEntityClass() {
        return this.sceneEntityClass;
    }
}