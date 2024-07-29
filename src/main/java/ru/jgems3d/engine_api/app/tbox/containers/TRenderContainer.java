package ru.jgems3d.engine_api.app.tbox.containers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.system.files.JGPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderAttributes;

public final class TRenderContainer {
    private final MeshRenderAttributes meshRenderAttributes;
    private final JGPath pathToRenderModel;
    private final JGPath pathToRenderShader;
    private final Class<? extends AbstractSceneEntity> sceneEntityClass;
    private final Class<? extends IRenderObjectFabric> renderFabricClass;

    public TRenderContainer(@NotNull Class<? extends IRenderObjectFabric> renderFabricClass, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @NotNull JGPath pathToRenderModel, @NotNull MeshRenderAttributes meshRenderAttributes) {
        this(renderFabricClass, sceneEntityClass, null, pathToRenderModel, meshRenderAttributes);
    }

    public TRenderContainer(@NotNull Class<? extends IRenderObjectFabric> renderFabricClass, @NotNull Class<? extends AbstractSceneEntity> sceneEntityClass, @Nullable JGPath pathToRenderShader, @NotNull JGPath pathToRenderModel, @NotNull MeshRenderAttributes meshRenderAttributes) {
        this.renderFabricClass = renderFabricClass;
        this.sceneEntityClass = sceneEntityClass;
        this.pathToRenderShader = pathToRenderShader;
        this.pathToRenderModel = pathToRenderModel;
        this.meshRenderAttributes = meshRenderAttributes;
    }

    public JGPath getPathToRenderShader() {
        return this.pathToRenderShader;
    }

    public MeshRenderAttributes getMeshRenderAttributes() {
        return this.meshRenderAttributes;
    }

    public JGPath getPathToRenderModel() {
        return this.pathToRenderModel;
    }

    public Class<? extends IRenderObjectFabric> getRenderFabricClass() {
        return this.renderFabricClass;
    }

    public Class<? extends AbstractSceneEntity> getSceneEntityClass() {
        return this.sceneEntityClass;
    }
}
