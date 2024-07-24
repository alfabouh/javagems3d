package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderObjectFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private Material overObjectMaterial;
    private ModelRenderParams modelRenderParams;

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull ModelRenderParams modelRenderParams, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, aClass, shaderManager, null);
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull ModelRenderParams modelRenderParams) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, MeshDataGroup meshDataGroup) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getModelRenderParams(), meshDataGroup);
        this.setOverObjectMaterial(renderObjectData.getOverObjectMaterial());
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, Material overObjectMaterial) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getModelRenderParams(), renderObjectData.getMeshDataGroup());
        this.setOverObjectMaterial(overObjectMaterial);
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public AbstractSceneEntity constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            AbstractSceneEntity abstractSceneEntity = this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
            this.onPhysicsObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsException(e);
        }
    }

    protected void onPhysicsObjectCreated(AbstractSceneEntity abstractSceneEntity) {
    }

    public IRenderObjectFabric getRenderFabric() {
        return this.renderFabric;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public RenderObjectData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        this.entityModelConstructor = null;
        return this;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public RenderObjectData setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderObjectData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshDataGroup = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public ModelRenderParams getModelRenderParams() {
        return this.modelRenderParams;
    }

    public RenderObjectData setModelRenderParams(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public Class<? extends AbstractSceneEntity> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getModelRenderParams());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderObjectData;
    }
}
