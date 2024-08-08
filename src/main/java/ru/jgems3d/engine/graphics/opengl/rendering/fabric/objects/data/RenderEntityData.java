package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all  ")
public class RenderEntityData {
    private final IRenderObjectFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private MeshRenderData meshRenderData;

    public RenderEntityData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = MeshRenderData.defaultMeshRenderData(shaderManager);
    }

    public RenderEntityData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull MeshRenderData meshRenderData, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderEntityData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, aClass, shaderManager, null);
    }

    public RenderEntityData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull MeshRenderData meshRenderData) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, MeshDataGroup meshDataGroup) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getRenderClass(), renderEntityData.getMeshRenderData(), meshDataGroup);
        this.setEntityModelConstructor(renderEntityData.getEntityModelConstructor());
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, Material overObjectMaterial) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getRenderClass(), renderEntityData.getMeshRenderData(), renderEntityData.getMeshDataGroup());
        this.setEntityModelConstructor(renderEntityData.getEntityModelConstructor());
    }

    public AbstractSceneEntity constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderEntityData renderEntityData = this.copyObject();
        try {
            AbstractSceneEntity abstractSceneEntity = this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderEntityData.class).newInstance(sceneWorld, worldItem, renderEntityData);
            this.onPhysicsObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsRuntimeException(e);
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

    public RenderEntityData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        this.entityModelConstructor = null;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderEntityData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshDataGroup = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public MeshRenderData getMeshRenderData() {
        return this.meshRenderData;
    }

    public RenderEntityData setModelRenderParams(MeshRenderData meshRenderData) {
        this.meshRenderData = meshRenderData;
        return this;
    }

    public Class<? extends AbstractSceneEntity> getRenderClass() {
        return this.aClass;
    }

    protected RenderEntityData copyObject() {
        RenderEntityData renderEntityData = new RenderEntityData(this.getRenderFabric(), this.getRenderClass(), this.getMeshRenderData());
        renderEntityData.setMeshDataGroup(this.getMeshDataGroup());
        renderEntityData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderEntityData;
    }
}
