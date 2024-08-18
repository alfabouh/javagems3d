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

package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;

public final class TRenderContainer {
    private final MeshRenderAttributes meshRenderAttributes;
    private final JGemsPath pathToRenderModel;
    private final JGemsPath pathToRenderShader;
    private final Class<? extends AbstractSceneEntity> sceneEntityClass;
    private final Class<? extends IRenderObjectFabric> renderFabricClass;

    public TRenderContainer(@NotNull Class<? extends IRenderObjectFabric> renderFabricClass, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @NotNull JGemsPath pathToRenderModel, @NotNull MeshRenderAttributes meshRenderAttributes) {
        this(renderFabricClass, sceneEntityClass, null, pathToRenderModel, meshRenderAttributes);
    }

    public TRenderContainer(@NotNull Class<? extends IRenderObjectFabric> renderFabricClass, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @Nullable JGemsPath pathToRenderShader, @NotNull JGemsPath pathToRenderModel, @NotNull MeshRenderAttributes meshRenderAttributes) {
        this.renderFabricClass = renderFabricClass;
        this.sceneEntityClass = sceneEntityClass;
        this.pathToRenderShader = pathToRenderShader;
        this.pathToRenderModel = pathToRenderModel;
        this.meshRenderAttributes = meshRenderAttributes;
    }

    public JGemsPath getPathToRenderShader() {
        return this.pathToRenderShader;
    }

    public MeshRenderAttributes getMeshRenderAttributes() {
        return this.meshRenderAttributes;
    }

    public JGemsPath getPathToRenderModel() {
        return this.pathToRenderModel;
    }

    public Class<? extends IRenderObjectFabric> getRenderFabricClass() {
        return this.renderFabricClass;
    }

    public Class<? extends AbstractSceneEntity> getSceneEntityClass() {
        return this.sceneEntityClass;
    }
}
