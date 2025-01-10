/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 */

package javagems3d.system.map.loaders.tbox.placers;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.fabric.IRenderFabric;
import org.jetbrains.annotations.NotNull;
import javagems3d.graphics.objects.entities.AbstractSceneEntity;
import javagems3d.system.service.path.JGemsPath;

/**
 * This class represents the rendering information for an object inside the engine itself
 */
public final class TDefaultRenderContainer {
    private final RenderAttributes objectRenderingConfiguration;
    private final JGemsPath pathToRenderModel;
    private final JGemsPath pathToRenderShader;
    private final Class<? extends AbstractSceneEntity> sceneEntityClass;
    private final IRenderFabric renderFabric;

    public TDefaultRenderContainer(@NotNull IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @NotNull JGemsPath pathToRenderShader, @NotNull JGemsPath pathToRenderModel, @NotNull RenderAttributes objectRenderingConfiguration) {
        this.renderFabric = renderFabric;
        this.sceneEntityClass = sceneEntityClass;
        this.pathToRenderShader = pathToRenderShader;
        this.pathToRenderModel = pathToRenderModel;
        this.objectRenderingConfiguration = objectRenderingConfiguration;
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
    public @NotNull RenderAttributes getObjectRenderSettings() {
        return this.objectRenderingConfiguration;
    }

    /**
     * Path to model
     */
    public @NotNull JGemsPath getPathToRenderModel() {
        return this.pathToRenderModel;
    }

    /**
     * A render factory is a special class that is responsible for rendering a specific IRenderObject object
     *
     * @see IRendered
     */
    public @NotNull IRenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    /**
     * This class represents an object in the world of the scene. It is connected to an object from the physical world. It can be null if you don't need this object.
     */
    public @NotNull Class<? extends AbstractSceneEntity> getSceneEntityClass() {
        return this.sceneEntityClass;
    }
}